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
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import org.threeten.extra.Interval;

import fc.timeseries.OperatorValueFunctions.BinaryValueFunction;
import fc.timeseries.OperatorValueFunctions.UnaryValueFunction;

/**
 * Represents a segment of a timeline, that may have a time-variant function (or be constant).
 *
 * @param <V>
 *            datatype on the timeline.
 */
class ValueFunctionTimesegment<V> implements Serializable, ValueFunction<V>, Timesegment<V> {

    private final Interval interval;
    private final ValueFunction<V> valueFunction;

    ValueFunctionTimesegment(Interval interval, V value) {
        this(interval, OperatorValueFunctions.value(value));
    }

    ValueFunctionTimesegment(Interval interval, ValueFunction<V> valueFunction) {
        this.interval = interval;
        this.valueFunction = valueFunction;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + interval + ", ["
                + valueFunction.valueAt(interval.getStart().toEpochMilli()) + ", "
                + valueFunction.valueAt(interval.getEnd().toEpochMilli()) + ")>";
    }

    ValueFunctionTimesegment<V> unaryOp(UnaryOperator<V> ops) {
        return createNew(getInterval(), new UnaryValueFunction<V>(ops, getValueFunction()));
    }

    ValueFunctionTimesegment<V> binaryOp(BinaryOperator<V> ops, ValueFunction<V> otherValueFunction) {
        return createNew(getInterval(), new BinaryValueFunction<V>(ops, this.getValueFunction(), otherValueFunction));
    }

    @Override
    public V valueAt(long key) {
        checkBounds(key);
        return valueFunction.valueAt(key);
    }

    @Override
    public Instant getStart() {
        return interval.getStart();
    }

    @Override
    public Instant getEnd() {
        return interval.getEnd();
    }

    @Override
    public Interval getInterval() {
        return interval;
    }

    @Override
    public ValueFunction<V> getValueFunction() {
        return valueFunction;
    }

    @Override
    public boolean overlaps(Timesegment<V> other) {
        return interval.overlaps(other.getInterval());
    }

    @Override
    public ValueFunctionTimesegment<V> createNew(Interval interval, ValueFunction<V> valueFunction) {
        return new ValueFunctionTimesegment<V>(interval, valueFunction);
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
        ValueFunctionTimesegment<?> other = (ValueFunctionTimesegment<?>) obj;

        return Objects.equals(interval, other.interval) //
                && Objects.equals(toString(), other.toString()) // cowboy hack, but solves problem of nested functions
                ;
    }

    @SuppressWarnings("rawtypes")
    static final Comparator<ValueFunctionTimesegment> ORDER_LINE_SEGMENT = new Comparator<ValueFunctionTimesegment>() {
        @Override
        public int compare(ValueFunctionTimesegment o1, ValueFunctionTimesegment o2) {
            long diff = o1.getStart().toEpochMilli() - o2.getStart().toEpochMilli();
            if (diff == 0) {
                diff = o1.getEnd().toEpochMilli() - o2.getEnd().toEpochMilli();
            }
            return Long.signum(diff);
        }
    };

}
