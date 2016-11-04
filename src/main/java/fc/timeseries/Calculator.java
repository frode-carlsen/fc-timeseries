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
import java.util.function.UnaryOperator;

public interface Calculator<V> {

    V plus(V v1, V v2);

    V multiply(V v1, V v2);

    V divide(V v1, V v2);

    V minus(V v1, V v2);

    V abs(V v1);

    V negate(V v1);

    V convertToValue(Object otherValue);

    public static <V> BinaryOperator<V> plus(Calculator<V> calc) {
        return named("+", (t, u) -> calc.plus(t, u));
    }

    public static <V> BinaryOperator<V> minus(Calculator<V> calc) {
        return named("-", (t, u) -> calc.minus(t, u));
    }

    public static <V> BinaryOperator<V> multiply(Calculator<V> calc) {
        return named("*", (t, u) -> calc.multiply(t, u));
    }

    public static <V> BinaryOperator<V> divide(Calculator<V> calc) {
        return named("/", (t, u) -> calc.divide(t, u));
    }

    public static <V> UnaryOperator<V> abs(Calculator<V> calc) {
        return named("abs", (t) -> calc.abs(t));
    }

    public static <V> UnaryOperator<V> negate(Calculator<V> calc) {
        return named("neg", (t) -> calc.negate(t));
    }

    static <V> BinaryOperator<V> named(String name, BinaryOperator<V> op) {
        return new NamedBinaryOperator<V>(name, op);
    }

    static <V> UnaryOperator<V> named(String name, UnaryOperator<V> op) {
        return new NamedUnaryOperator<V>(name, op);
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

}
