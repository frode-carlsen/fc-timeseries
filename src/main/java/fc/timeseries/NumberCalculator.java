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

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import fc.timeseries.OperatorValueFunctions.NamedBinaryOperator;
import fc.timeseries.OperatorValueFunctions.NamedUnaryOperator;

/**
 * A calculator that can combine Number datatypes as they are used on a {@link Timeline}.
 * {@link StandardNumberCalculator} can handle most Number datatypes for ease-of-use.
 */
public interface NumberCalculator {

    Number plus(Number v1, Number v2);

    Number multiply(Number v1, Number v2);

    Number divide(Number v1, Number v2);

    Number minus(Number v1, Number v2);

    Number abs(Number v1);

    Number negate(Number v1);

    Number convertToValue(Object otherValue);

    public static BinaryOperator<Number> plus(NumberCalculator calc) {
        return named("+", (t, u) -> calc.plus(t, u));
    }

    public static BinaryOperator<Number> minus(NumberCalculator calc) {
        return named("-", (t, u) -> calc.minus(t, u));
    }

    public static BinaryOperator<Number> multiply(NumberCalculator calc) {
        return named("*", (t, u) -> calc.multiply(t, u));
    }

    public static BinaryOperator<Number> divide(NumberCalculator calc) {
        return named("/", (t, u) -> calc.divide(t, u));
    }

    public static UnaryOperator<Number> abs(NumberCalculator calc) {
        return named("abs", (t) -> calc.abs(t));
    }

    public static UnaryOperator<Number> negate(NumberCalculator calc) {
        return named("neg", (t) -> calc.negate(t));
    }

    static BinaryOperator<Number> named(String name, BinaryOperator<Number> op) {
        return new NamedBinaryOperator<Number>(name, op);
    }

    static UnaryOperator<Number> named(String name, UnaryOperator<Number> op) {
        return new NamedUnaryOperator<Number>(name, op);
    }


}
