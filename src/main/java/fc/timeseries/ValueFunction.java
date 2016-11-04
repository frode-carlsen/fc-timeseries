package fc.timeseries;

/**
 * Simple function of one variable (time).
 */
public interface ValueFunction<V> {

    V valueAt(long key);
}
