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

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.threeten.extra.Interval;

/**
 * Represents a segment of a timeline, that may have a time-variant function (or be constant).
 *
 * @param <V> datatype on the timeline.
 */
class Timesegment<V> implements Serializable, ValueFunction<V> {

    private final Interval interval;
    private final ValueFunction<V> valueFunction;

    Timesegment(Interval interval, V value) {
        this(interval, Operators.value(value));
    }

    Timesegment(Interval interval, ValueFunction<V> valueFunction) {
        this.interval = interval;
        this.valueFunction = valueFunction;
    }

    Timesegment<V> unaryOp(UnaryOperator<V> ops) {
        return new Timesegment<V>(interval, new UnaryValueFunction<V>(ops, valueFunction));
    }

    Timesegment<V> binaryOp(BinaryOperator<V> ops, ValueFunction<V> otherValueFunction) {
        return new Timesegment<V>(interval, new BinaryValueFunction<V>(ops, this.valueFunction, otherValueFunction));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + interval + ", ["
                + valueFunction.valueAt(interval.getStart().toEpochMilli()) + ", "
                + valueFunction.valueAt(interval.getEnd().toEpochMilli()) + ")>";
    }

    @Override
    public V valueAt(long key) {
        checkBounds(key);
        return valueFunction.valueAt(key);
    }

    Instant getStart() {
        return interval.getStart();
    }

    Instant getEnd() {
        return interval.getEnd();
    }

    Interval getInterval() {
        return interval;
    }

    ValueFunction<V> getValueFunction() {
        return valueFunction;
    }

    boolean overlaps(Timesegment<V> other) {
        return interval.overlaps(other.interval);
    }

    private void checkBounds(long key) {
        if (getStart().toEpochMilli() > key || getEnd().toEpochMilli() < key) {
            throw new IndexOutOfBoundsException(
                    "key out of bounds: [" + getStart().toEpochMilli() + ", " + getEnd().toEpochMilli() + ")");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, valueFunction);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(Objects.equals(obj.getClass(), this.getClass()))) {
            return false;
        }
        Timesegment<?> other = (Timesegment<?>) obj;

        return Objects.equals(interval, other.interval) //
                && Objects.equals(toString(), other.toString()) // cowboy hack, but solves problem of nested functions
                ;
    }

    @SuppressWarnings("rawtypes")
    static final Comparator<Timesegment> ORDER_LINE_SEGMENT = new Comparator<Timesegment>() {
        @Override
        public int compare(Timesegment o1, Timesegment o2) {
            long diff = o1.getStart().toEpochMilli() - o2.getStart().toEpochMilli();
            if (diff == 0) {
                diff = o1.getEnd().toEpochMilli() - o2.getEnd().toEpochMilli();
            }
            return Long.signum(diff);
        }
    };

    private static final class BinaryValueFunction<V> implements ValueFunction<V> {
        private final BinaryOperator<V> op;
        private final ValueFunction<V> valueFunction;
        private final ValueFunction<V> otherValueFunction;

        public BinaryValueFunction(BinaryOperator<V> op, ValueFunction<V> valueFunction, ValueFunction<V> other) {
            this.op = op;
            this.valueFunction = valueFunction;
            this.otherValueFunction = other;
        }

        @Override
        public V valueAt(long key) {
            return op.apply(valueFunction.valueAt(key), otherValueFunction.valueAt(key));
        }
    }

    private static final class UnaryValueFunction<V> implements ValueFunction<V> {
        private UnaryOperator<V> unaryFunction;
        private ValueFunction<V> valueFunction;

        UnaryValueFunction(UnaryOperator<V> unaryFunction, ValueFunction<V> valueFunction) {
            this.unaryFunction = unaryFunction;
            this.valueFunction = valueFunction;
        }

        @Override
        public V valueAt(long key) {
            return unaryFunction.apply(valueFunction.valueAt(key));
        }
    }

    static <V> List<Timesegment<V>> combineAndOrderUnorderedSegments(BinaryOperator<V> op, Collection<Timesegment<V>> segments) {
        Collection<Interval> intervals = IntervalUtil
                .disjoinIntervals(segments.stream().map(s -> s.getInterval()).collect(Collectors.toList()));

        return partitionSegmentsByIntervals(op, segments, intervals);
    }

    static <V> List<Timesegment<V>> partitionSegmentsByIntervals(BinaryOperator<V> op,
            Collection<Timesegment<V>> segments, Collection<Interval> intervals) {
        List<Timesegment<V>> result = new ArrayList<>();

        // O(n^2)...
        for (Interval interval : intervals) {
            List<Timesegment<V>> overlappingSegments = segments.stream() //
                    .filter(s -> s.getInterval().encloses(interval)).collect(Collectors.toList());

            if (overlappingSegments.isEmpty()) {
                continue;
            }

            if (overlappingSegments.size() == 1) {
                result.add(new Timesegment<>(interval, overlappingSegments.get(0).valueFunction));
            } else {
                Timesegment<V> curr = overlappingSegments.get(0);
                ValueFunction<V> combinedFunction = curr.getValueFunction();
                for (int i = 1, sz = overlappingSegments.size(); i < sz; i++) {
                    Timesegment<V> next = overlappingSegments.get(i);
                    combinedFunction = new BinaryValueFunction<>(op, combinedFunction, next.getValueFunction());
                }
                result.add(new Timesegment<>(interval, combinedFunction));
            }

        }

        return result;
    }

}
