/**
 *   Copyright 2016- Frode Carlsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fc.timeseries;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.threeten.extra.Interval;

/**
 * A timeline consisting of many segments
 *
 * @param <V>
 *            datatype used on the timeline. Must be supported by an appropriate calculator (e.g
 *            {@link StandardNumberCalculator}).
 */
public class Timeline<V> implements Timesegment<V> {

    private List<Timesegment<V>> segments;
    @SuppressWarnings("rawtypes")
    static final Comparator<Timesegment> ORDER_LINE_SEGMENT = new Comparator<Timesegment>() {
        @Override
        public int compare(Timesegment o1, Timesegment o2) {
            long diff = o1.getInterval().getStart().toEpochMilli() - o2.getInterval().getStart().toEpochMilli();
            if (diff == 0) {
                diff = o1.getInterval().getEnd().toEpochMilli() - o2.getInterval().getEnd().toEpochMilli();
            }
            return Long.signum(diff);
        }
    };

    private Timeline(Collection<Timesegment<V>> segments) {
        this.segments = new ArrayList<>(segments);
    }

    public Timeline<V> add(Interval interval, ValueFunction<V> valueFunction) {
        List<Timesegment<V>> segmentCopy = new ArrayList<>(segments);
        if (segmentCopy.stream().anyMatch((s) -> s.getInterval().overlaps(interval))) {
            throw new IllegalArgumentException("Interval " + interval + " overlaps with existing segments");
        }
        segmentCopy.add(new ValueFunctionTimesegment<>(interval, valueFunction));
        Collections.sort(segmentCopy, Timeline.ORDER_LINE_SEGMENT);
        return new Timeline<>(segmentCopy);
    }

    public Collection<Timesegment<V>> getSegments() {
        return new ArrayList<>(segments);
    }

    public Instant getStart() {
        if (segments.size() < 1) {
            throw new IllegalStateException("Empty timeline");
        }
        return segments.get(0).getInterval().getStart();
    }

    public Instant getEnd() {
        if (segments.size() < 1) {
            throw new IllegalStateException("Empty timeline");
        }
        return segments.get(segments.size() - 1).getInterval().getEnd();
    }

    @Override
    public V valueAt(Instant instant) {
        for (Timesegment<V> ts : segments) {
            if (ts.contains(instant)) {
                return ts.valueAt(instant);
            }
        }
        return null;
    }

    @Override
    public Timesegment<V> overlap(Interval interval) {
        List<Timesegment<V>> list = segments.stream()
                .filter(s -> s.getInterval().overlaps(interval))
                .map(s -> s.overlap(interval))
                .collect(Collectors.toList());

        return new Timeline<>(list);
    }

    @Override
    public boolean contains(Instant instant) {
        return segments.stream().anyMatch(s -> s.getInterval().contains(instant));
    }

    @Override
    public Interval getInterval() {
        return Interval.of(getStart(), getEnd());
    }

    @Override
    public boolean overlaps(Timesegment<V> other) {
        return getInterval().overlaps(other.getInterval());
    }

    @Override
    public Timesegment<V> combineWith(BinaryOperator<V> op, Interval interval, Timesegment<V> timesegment) {
        List<Timesegment<V>> list = new ArrayList<>(segments);
        list.add(timesegment);
        return ofUnorderedSegments(op, list);
    }

    @SafeVarargs
    public static <V> Timeline<V> ofDisjointAndOrdered(Timesegment<V>... segments) {
        return new Timeline<>(Arrays.asList(segments));
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<segments=" + segments + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(Objects.equals(getClass(), obj.getClass()))) {
            return false;
        }
        Timeline<?> other = (Timeline<?>) obj;
        return Objects.equals(segments, other.segments);
    }

    static <V> List<Timesegment<V>> combineAndOrderUnorderedSegments(BinaryOperator<V> op,
            Collection<Timesegment<V>> segments) {
        Collection<Interval> intervals = IntervalUtil
                .disjoinIntervals(segments.stream().map(s -> s.getInterval()).collect(Collectors.toList()));

        return partitionSegmentsByIntervals(op, segments, intervals);
    }

    static <V> List<Timesegment<V>> partitionSegmentsByIntervals(BinaryOperator<V> op,
            Collection<Timesegment<V>> segments, Collection<Interval> intervals) {
        List<Timesegment<V>> result = new ArrayList<>();

        // O(n^2)...
        for (Interval interval : intervals) {
            result.addAll(combineForInterval(op, interval, segments));
        }

        return result;
    }

    static <V> List<Timesegment<V>> combineForInterval(BinaryOperator<V> op, Interval interval, Collection<Timesegment<V>> segments) {

        List<Timesegment<V>> overlappingSegments = segments.stream() //
                .filter(s -> s.getInterval().encloses(interval)).collect(Collectors.toList());

        List<Timesegment<V>> result = new ArrayList<>();

        if (overlappingSegments.isEmpty()) {
            return Collections.emptyList();
        } else if (overlappingSegments.size() == 1) {
            result.add(overlappingSegments.get(0).overlap(interval));
        } else {

            Timesegment<V> combined = null;
            for (Timesegment<V> curr : overlappingSegments) {

                if (combined == null) {
                    combined = curr;
                    continue;
                }

                combined = combined.combineWith(op, interval, curr);

            }

            result.add(combined);
        }

        return result;
    }

    /**
     * Optimitized for when caller knows segments are not overlapping and are ordered by time.
     */
    public static <V> Timeline<V> ofDisjointAndOrderedSegments(Collection<Timesegment<V>> segments) {
        return new Timeline<>(segments);
    }

    @SafeVarargs
    public static <V> Timeline<V> ofUnordered(BinaryOperator<V> op, Timesegment<V>... segments) {
        return Timeline.ofUnorderedSegments(op, Arrays.asList(segments));
    }

    public static <V> Timeline<V> ofUnorderedSegments(BinaryOperator<V> op, Collection<Timesegment<V>> segments) {
        List<Timesegment<V>> combined = combineAndOrderUnorderedSegments(op, segments);
        return Timeline.ofDisjointAndOrderedSegments(combined);
    }

}
