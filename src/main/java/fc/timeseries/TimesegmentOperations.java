package fc.timeseries;

import java.util.Objects;
import java.util.function.BinaryOperator;

class TimesegmentOperations {

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
        public V valueAt(long key) {
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

    static class ConstantValueFunction<V> implements ValueFunction<V> {

        private V value;

        public ConstantValueFunction(V value) {
            this.value = value;
        }

        @Override
        public V valueAt(long key) {
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
}
