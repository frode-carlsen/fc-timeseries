package fc.timeseries;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class NumberCalculator implements Calculator<Number> {

    private static final Map<Class<?>, Calculator<Number>> PREFERRENCE_ORDERED_VALUE_CALCULATORS;

    static {

        Map<Class<?>, Calculator<Number>> map = new LinkedHashMap<>();

        map.put(BigDecimal.class, new BigDecimalCalculator());
        map.put(Double.class, new DoubleCalculator());
        map.put(Float.class, new FloatCalculator());
        map.put(Long.class, new LongCalculator());
        map.put(Integer.class, new IntegerCalculator());

        PREFERRENCE_ORDERED_VALUE_CALCULATORS = map;
    }

    private static boolean anyOfType(Class<?> cls, Number v1, Number v2) {
        return Objects.equals(cls, v1.getClass()) || (v2 != null && Objects.equals(cls, v2.getClass()));
    }

    private static BigDecimal toBigDecimal(Number v1) {
        return BigDecimal.valueOf(v1.doubleValue());
    }

    static Calculator<Number> getPreferredValueCalculator(Number v1, Number v2) {
        for (Map.Entry<Class<?>, Calculator<Number>> entry : PREFERRENCE_ORDERED_VALUE_CALCULATORS.entrySet()) {
            if (anyOfType(entry.getKey(), v1, v2)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("No matching value calculator found for types: [" + v1 + ", " + v2 + "]");
    }

    @Override
    public Number plus(Number v1, Number v2) {
        return getPreferredValueCalculator(v1, v2).plus(v1, v2);
    }

    @Override
    public Number multiply(Number v1, Number v2) {
        return getPreferredValueCalculator(v1, v2).multiply(v1, v2);
    }

    @Override
    public Number divide(Number v1, Number v2) {
        return getPreferredValueCalculator(v1, v2).divide(v1, v2);
    }

    @Override
    public Number minus(Number v1, Number v2) {
        return getPreferredValueCalculator(v1, v2).minus(v1, v2);
    }

    @Override
    public Number abs(Number v1) {
        return getPreferredValueCalculator(v1, null).abs(v1);
    }

    @Override
    public Number negate(Number v1) {
        return getPreferredValueCalculator(v1, null).negate(v1);
    }

    @Override
    public Number convertToValue(Object verdi) {
        if (verdi == null || verdi instanceof Number) {
            return (Number) verdi;
        }
        if (verdi instanceof String) {
            try {
                return new BigDecimal((String) verdi);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid conversion from" + verdi + " til BigDecimal", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        throw new IllegalArgumentException("No valid conversion from " + verdi + " til BigDecimal"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static class LongCalculator implements Calculator<Number> {

        @Override
        public Number plus(Number v1, Number v2) {
            return convertToValue(v1) + convertToValue(v2);
        }

        @Override
        public Number multiply(Number v1, Number v2) {
            return convertToValue(v1) * convertToValue(v2);
        }

        @Override
        public Number divide(Number v1, Number v2) {
            return convertToValue(v1) / convertToValue(v2);
        }

        @Override
        public Number minus(Number v1, Number v2) {
            return convertToValue(v1) - convertToValue(v2);
        }

        @Override
        public Number abs(Number v1) {
            return Math.abs(convertToValue(v1));
        }

        @Override
        public Number negate(Number v1) {
            return Math.negateExact(convertToValue(v1));
        }

        @Override
        public Long convertToValue(Object v) {
            if (v instanceof Long) {
                return (Long) v;
            } else {
                return ((Number) v).longValue();
            }
        }

    }

    private static class DoubleCalculator implements Calculator<Number> {

        @Override
        public Number plus(Number v1, Number v2) {
            return convertToValue(v1) + convertToValue(v2);
        }

        @Override
        public Number multiply(Number v1, Number v2) {
            return convertToValue(v1) * convertToValue(v2);
        }

        @Override
        public Number divide(Number v1, Number v2) {
            return convertToValue(v1) / convertToValue(v2);
        }

        @Override
        public Number minus(Number v1, Number v2) {
            return convertToValue(v1) - convertToValue(v2);
        }

        @Override
        public Number abs(Number v1) {
            return Math.abs(convertToValue(v1));
        }

        @Override
        public Number negate(Number v1) {
            return -1L * (convertToValue(v1));
        }

        @Override
        public Double convertToValue(Object v) {
            if (v instanceof Double) {
                return (Double) v;
            } else {
                return ((Number) v).doubleValue();
            }
        }

    }

    private static class IntegerCalculator implements Calculator<Number> {

        @Override
        public Number plus(Number v1, Number v2) {
            return convertToValue(v1) + convertToValue(v2);
        }

        @Override
        public Number multiply(Number v1, Number v2) {
            return convertToValue(v1) * convertToValue(v2);
        }

        @Override
        public Number divide(Number v1, Number v2) {
            return convertToValue(v1) / convertToValue(v2);
        }

        @Override
        public Number minus(Number v1, Number v2) {
            return convertToValue(v1) - convertToValue(v2);
        }

        @Override
        public Number abs(Number v1) {
            return Math.abs(convertToValue(v1));
        }

        @Override
        public Number negate(Number v1) {
            return Math.negateExact(convertToValue(v1));
        }

        @Override
        public Integer convertToValue(Object v) {
            if (v instanceof Integer) {
                return (Integer) v;
            } else {
                return ((Number) v).intValue();
            }
        }

    }

    private static class FloatCalculator implements Calculator<Number> {

        @Override
        public Number plus(Number v1, Number v2) {
            return convertToValue(v1) + convertToValue(v2);
        }

        @Override
        public Number multiply(Number v1, Number v2) {
            return convertToValue(v1) * convertToValue(v2);
        }

        @Override
        public Number divide(Number v1, Number v2) {
            return convertToValue(v1) / convertToValue(v2);
        }

        @Override
        public Number minus(Number v1, Number v2) {
            return convertToValue(v1) - convertToValue(v2);
        }

        @Override
        public Number abs(Number v1) {
            return Math.abs(convertToValue(v1));
        }

        @Override
        public Number negate(Number v1) {
            return -1f * (convertToValue(v1));
        }

        @Override
        public Float convertToValue(Object v) {
            if (v instanceof Float) {
                return (Float) v;
            } else {
                return ((Number) v).floatValue();
            }
        }

    }

    private static class BigDecimalCalculator implements Calculator<Number> {

        @Override
        public Number plus(Number v1, Number v2) {
            return convertToValue(v1).add(convertToValue(v2));
        }

        @Override
        public Number multiply(Number v1, Number v2) {
            // TODO: mathcontext?
            return convertToValue(v1).multiply(convertToValue(v2));
        }

        @Override
        public Number divide(Number v1, Number v2) {
            // TODO: scale + roundingMode?
            return convertToValue(v1).divide(convertToValue(v2));
        }

        @Override
        public Number minus(Number v1, Number v2) {
            return convertToValue(v1).subtract(convertToValue(v2));
        }

        @Override
        public Number abs(Number v1) {
            return convertToValue(v1).abs();
        }

        @Override
        public Number negate(Number v1) {
            return convertToValue(v1).negate();
        }

        @Override
        public BigDecimal convertToValue(Object v) {
            if (v instanceof BigDecimal) {
                return (BigDecimal) v;
            } else {
                return toBigDecimal((Number) v);
            }
        }

    }

}
