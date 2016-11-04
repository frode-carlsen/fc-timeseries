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
