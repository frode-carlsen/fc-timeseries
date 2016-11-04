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

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A calculator able to operate on {@link Number} datatype.
 *
 * Arguments maybe of different Numbers. If so, the datatype will be converted to the datatype given by the preferred
 * order defined by {@link #PREFERRENCE_ORDERED_VALUE_CALCULATORS}.
 */
public class StandardNumberCalculator implements NumberCalculator {

    private static final Map<Class<?>, NumberCalculator> PREFERRENCE_ORDERED_VALUE_CALCULATORS;

    static {

        Map<Class<?>, NumberCalculator> map = new LinkedHashMap<>();

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

    /**
     * Get most preferred value calculator. Datatypes may or may not be of the same {@link Number} type.
     */
    static NumberCalculator getPreferredValueCalculator(Number v1, Number v2) {
        for (Map.Entry<Class<?>, NumberCalculator> entry : PREFERRENCE_ORDERED_VALUE_CALCULATORS.entrySet()) {
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

    private static class LongCalculator implements NumberCalculator {

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

    private static class DoubleCalculator implements NumberCalculator {

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

    private static class IntegerCalculator implements NumberCalculator {

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

    private static class FloatCalculator implements NumberCalculator {

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

    private static class BigDecimalCalculator implements NumberCalculator {

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
