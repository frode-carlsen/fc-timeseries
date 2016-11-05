package fc.timeseries;

import java.time.Instant;
import java.util.function.BinaryOperator;

import org.threeten.extra.Interval;

public interface Timesegment<V> extends ValueFunction<V> {

    Interval getInterval();

    boolean overlaps(Timesegment<V> other);

    Timesegment<V> combineWith(BinaryOperator<V> op, Interval interval, Timesegment<V> curr);

    Timesegment<V> overlap(Interval interval);

    boolean contains(Instant instant);

}
