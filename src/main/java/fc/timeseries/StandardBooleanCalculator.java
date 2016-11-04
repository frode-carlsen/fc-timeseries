package fc.timeseries;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import fc.timeseries.Operators.NamedBinaryOperator;
import fc.timeseries.Operators.NamedUnaryOperator;

public class StandardBooleanCalculator {

    public static final BinaryOperator<Boolean> AND = new NamedBinaryOperator<Boolean>("and", (t, u) -> t && u);

    public static final BinaryOperator<Boolean> OR = new NamedBinaryOperator<Boolean>("or", (t, u) -> t || u);

    public static final BinaryOperator<Boolean> XOR = new NamedBinaryOperator<Boolean>("xor", (t, u) -> t ^ u);

    public static final UnaryOperator<Boolean> NOT = new NamedUnaryOperator<Boolean>("not", (t) -> !t);


}
