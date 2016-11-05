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
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * Common {@link ValueFunction} and matching operations.
 */
class OperatorValueFunctions {

    /**
     * A {@link ValueFunction} that combines two other functions through a {@link BinaryOperator}.
     *
     * @param <V>
     *            the datatype used in the {@link Timeline}
     */
    static class BinaryOperatorFunction<V> implements ValueFunction<V> {
        private final BinaryOperator<V> ops;
        private final ValueFunction<V> lhs;
        private final ValueFunction<V> rhs;

        BinaryOperatorFunction(BinaryOperator<V> ops, ValueFunction<V> lhs, ValueFunction<V> rhs) {
            this.ops = ops;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public V valueAt(Instant key) {
            return ops.apply(lhs.valueAt(key), rhs.valueAt(key));
        }

        @Override
        public int hashCode() {
            return Objects.hash(ops, lhs, rhs);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(Objects.equals(this.getClass(), obj.getClass()))) {
                return false;
            }
            BinaryOperatorFunction<?> other = (BinaryOperatorFunction<?>) obj;
            return Objects.equals(ops, other.ops) && Objects.equals(lhs, other.lhs) && Objects.equals(rhs, other.rhs);
        }
    }

    /**
     * A value function of a constant expression (example a number such as 2.0d)
     *
     * @param <V>
     *            the datatype used in the timeline.
     */
    static class ConstantValueFunction<V> implements ValueFunction<V> {

        private V value;

        public ConstantValueFunction(V value) {
            this.value = value;
        }

        @Override
        public V valueAt(Instant key) {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(Objects.equals(this.getClass(), obj.getClass()))) {
                return false;
            }
            ConstantValueFunction<?> other = (ConstantValueFunction<?>) obj;
            return Objects.equals(value, other.value);
        }
    }

    static <V> ValueFunction<V> value(V value) {
        return new ConstantValueFunction<V>(value);
    }

    static class NamedBinaryOperator<V> implements BinaryOperator<V> {

        private BinaryOperator<V> op;
        private String name;

        NamedBinaryOperator(String name, BinaryOperator<V> op) {
            this.name = name;
            this.op = op;
        }

        @Override
        public V apply(V t, V u) {
            return op.apply(t, u);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(Objects.equals(getClass(), obj.getClass()))) {
                return false;
            }

            NamedBinaryOperator<?> other = (NamedBinaryOperator<?>) obj;
            return Objects.equals(name, other.name);
        }

    }

    static class NamedUnaryOperator<V> implements UnaryOperator<V> {

        private UnaryOperator<V> op;
        private String name;

        NamedUnaryOperator(String name, UnaryOperator<V> op) {
            this.name = name;
            this.op = op;
        }

        @Override
        public V apply(V t) {
            return op.apply(t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(Objects.equals(getClass(), obj.getClass()))) {
                return false;
            }

            NamedUnaryOperator<?> other = (NamedUnaryOperator<?>) obj;
            return Objects.equals(name, other.name);
        }

    }

    static final class BinaryValueFunction<V> implements ValueFunction<V> {
        private final BinaryOperator<V> op;
        private final ValueFunction<V> valueFunction;
        private final ValueFunction<V> otherValueFunction;

        public BinaryValueFunction(BinaryOperator<V> op, ValueFunction<V> valueFunction, ValueFunction<V> other) {
            this.op = op;
            this.valueFunction = valueFunction;
            this.otherValueFunction = other;
        }

        @Override
        public V valueAt(Instant key) {
            return op.apply(valueFunction.valueAt(key), otherValueFunction.valueAt(key));
        }
    }

    static final class UnaryValueFunction<V> implements ValueFunction<V> {
        private UnaryOperator<V> unaryFunction;
        private ValueFunction<V> valueFunction;

        UnaryValueFunction(UnaryOperator<V> unaryFunction, ValueFunction<V> valueFunction) {
            this.unaryFunction = unaryFunction;
            this.valueFunction = valueFunction;
        }

        @Override
        public V valueAt(Instant key) {
            return unaryFunction.apply(valueFunction.valueAt(key));
        }
    }

}
