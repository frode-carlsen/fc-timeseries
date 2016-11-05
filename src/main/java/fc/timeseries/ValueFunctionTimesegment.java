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
class ValueFunctionTimesegment<V> implements Serializable, Timesegment<V> {

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
                + valueFunction.valueAt(interval.getStart()) + ", "
                + valueFunction.valueAt(interval.getEnd()) + ")>";
    }

    ValueFunctionTimesegment<V> unaryOp(UnaryOperator<V> ops) {
        return new ValueFunctionTimesegment<V>(getInterval(), new UnaryValueFunction<V>(ops, getValueFunction()));
    }

    ValueFunctionTimesegment<V> binaryOp(BinaryOperator<V> ops, ValueFunction<V> otherValueFunction) {
        return new ValueFunctionTimesegment<V>(getInterval(), new BinaryValueFunction<V>(ops, this.getValueFunction(), otherValueFunction));
    }

    @Override
    public Timesegment<V> combineWith(BinaryOperator<V> op, Interval interval, Timesegment<V> curr) {

        BinaryValueFunction<V> function = new BinaryValueFunction<>(op, this.getValueFunction(),
                (curr instanceof ValueFunctionTimesegment) ? ((ValueFunctionTimesegment<V>) curr).getValueFunction() : curr);

        return new ValueFunctionTimesegment<V>(interval, function);

    }

    @Override
    public V valueAt(Instant key) {
        checkBounds(key);
        return valueFunction.valueAt(key);
    }

    @Override
    public Timesegment<V> overlap(Interval interval) {
        return new ValueFunctionTimesegment<>(interval, valueFunction);
    }

    @Override
    public Interval getInterval() {
        return interval;
    }

    public ValueFunction<V> getValueFunction() {
        return valueFunction;
    }

    @Override
    public boolean overlaps(Timesegment<V> other) {
        return interval.overlaps(other.getInterval());
    }

    @Override
    public boolean contains(Instant instant) {
        return interval.contains(instant);
    }

    private void checkBounds(Instant key) {
        if (!getInterval().contains(key)) {
            throw new IndexOutOfBoundsException(
                    "key out of bounds: [" + getInterval() + ")");
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

}
