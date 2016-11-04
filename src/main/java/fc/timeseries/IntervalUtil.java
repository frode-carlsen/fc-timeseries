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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.threeten.extra.Interval;

public class IntervalUtil {

    public static Collection<Interval> disjoinIntervals(Collection<Interval> unmergedIntervals) {
        if (unmergedIntervals == null) {
            return null;
        } else if (unmergedIntervals.isEmpty() || unmergedIntervals.size() == 1) {
            return unmergedIntervals;
        }

        List<Interval> result = new ArrayList<>();

        List<Interval> intervals = new ArrayList<>(unmergedIntervals);
        Collections.sort(intervals, ORDER_INTERVALS);

        Interval current = intervals.get(0);

        int i = 1, sz = intervals.size();

        Interval currentToCompare = null;

        while (i < sz) {
            currentToCompare = intervals.get(i);

            if (!current.getEnd().isAfter(currentToCompare.getStart())) {
                result.add(current);
                current = currentToCompare;
            } else if (current.equals(currentToCompare)) {
                current = currentToCompare;
                // do nothing
            } else if (current.getEnd().isAfter(currentToCompare.getStart())) {
                Interval next_01 = Interval.of(current.getStart(), currentToCompare.getStart());
                result.add(next_01);

                Interval next_02 = Interval.of(currentToCompare.getStart(), current.getEnd());
                result.add(next_02);

                if (current.getEnd().isBefore(currentToCompare.getEnd())) {
                    Interval next_03 = Interval.of(current.getEnd(), currentToCompare.getEnd());
                    // result.add(next_03);
                    current = next_03;
                } else {
                    current = next_02;
                }
            }

            i++;
        }

        result.add(current);

        return result;

    }

    public static Collection<Interval> mergeIntervals(Collection<Interval> unmergedIntervals) {
        if (unmergedIntervals == null) {
            return null;
        } else if (unmergedIntervals.isEmpty() || unmergedIntervals.size() == 1) {
            return unmergedIntervals;
        }

        List<Interval> result = new ArrayList<>();

        List<Interval> intervals = new ArrayList<>(unmergedIntervals);
        Collections.sort(intervals, ORDER_INTERVALS);

        Interval current = intervals.get(0);

        int i = 1, sz = intervals.size();

        while (i < sz) {
            Interval currentToCompare = intervals.get(i);
            if (current.getEnd().isBefore(currentToCompare.getStart())) {
                result.add(current);
                current = currentToCompare;
            } else {
                current = Interval.of(current.getStart(), maxInstant(current.getEnd(), currentToCompare.getEnd()));
            }
            i++;
        }

        result.add(current);

        return result;

    }

    public static Instant maxInstant(Instant v1, Instant v2) {
        if (v1 == null) {
            throw new IllegalArgumentException("Instant v1 is null");
        } else if (v2 == null) {
            throw new IllegalArgumentException("Instant v2 is null");
        }
        return v1.isAfter(v2) ? v1 : v2;

    }

    public static final Comparator<Interval> ORDER_INTERVALS = new Comparator<Interval>() {
        @Override
        public int compare(Interval o1, Interval o2) {
            long diff = o1.getStart().toEpochMilli() - o2.getStart().toEpochMilli();
            if (diff == 0) {
                diff = o1.getEnd().toEpochMilli() - o2.getEnd().toEpochMilli();
            }
            return Long.signum(diff);
        }
    };

}
