package fc.timeseries;

import java.time.Instant;

import org.threeten.extra.Interval;

public interface Timesegment<V> {

    Instant getStart();

    Instant getEnd();

    Interval getInterval();

    boolean overlaps(Timesegment<V> other);

    Timesegment<V> createNew(Interval interval, ValueFunction<V> valueFunction);

    ValueFunction<V> getValueFunction();

}
