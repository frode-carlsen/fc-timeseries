package fc.timeseries;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import fc.timeseries.OperatorValueFunctions.NamedBinaryOperator;
import fc.timeseries.OperatorValueFunctions.NamedUnaryOperator;

/**
 * Standard operators on timeseries. Can handle both {@link Number} and {@link Boolean} datatypes.
 * Others are pluggable through {@link BinaryOperator} or {@link UnaryOperator}.
 */
public class Operators {

    private static final StandardNumberCalculator STANDARD_NUMBER_CALC = new StandardNumberCalculator();

    public static final BinaryOperator<Boolean> AND = new NamedBinaryOperator<Boolean>("and", (t, u) -> t && u);

    public static final BinaryOperator<Boolean> OR = new NamedBinaryOperator<Boolean>("or", (t, u) -> t || u);

    public static final BinaryOperator<Boolean> XOR = new NamedBinaryOperator<Boolean>("xor", (t, u) -> t ^ u);

    public static final UnaryOperator<Boolean> NOT = new NamedUnaryOperator<Boolean>("not", (t) -> !t);

    public static final UnaryOperator<Number> ABS = new NamedUnaryOperator<Number>("abs", (v) -> STANDARD_NUMBER_CALC.abs(v));

    public static final UnaryOperator<Number> NEGATE = new NamedUnaryOperator<Number>("neg", (v) -> STANDARD_NUMBER_CALC.negate(v));

    public static final BinaryOperator<Number> PLUS = new NamedBinaryOperator<Number>("+", (v1, v2) -> STANDARD_NUMBER_CALC.plus(v1, v2));

    public static final BinaryOperator<Number> MINUS = new NamedBinaryOperator<Number>("+", (v1, v2) -> STANDARD_NUMBER_CALC.minus(v1, v2));

    public static final BinaryOperator<Number> MULTIPLY = new NamedBinaryOperator<Number>("+",
            (v1, v2) -> STANDARD_NUMBER_CALC.multiply(v1, v2));

    public static final BinaryOperator<Number> DIVIDE = new NamedBinaryOperator<Number>("+",
            (v1, v2) -> STANDARD_NUMBER_CALC.divide(v1, v2));

}
