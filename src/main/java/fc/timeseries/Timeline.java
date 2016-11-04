package fc.timeseries;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A timeline consisting of many segments
 *
 * @param <V>
 *            type of values on the timeline.
 */
public class Timeline<V> {

    private Collection<Timesegment<V>> segments;

    private Timeline(Collection<Timesegment<V>> segments) {
        this.segments = segments;
    }

    @SafeVarargs
    public static <V> Timeline<V> ofDisjointAndOrdered(Timesegment<V>... segments) {
        return new Timeline<>(Arrays.asList(segments));
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<segments=" + segments + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(Objects.equals(getClass(), obj.getClass()))) {
            return false;
        }
        Timeline<?> other = (Timeline<?>) obj;
        return Objects.equals(segments, other.segments);
    }

    public static <V> Timeline<V> ofDisjointAndOrdered(Collection<Timesegment<V>> segments) {
        return new Timeline<>(segments);
    }

    @SafeVarargs
    public static <V> Timeline<V> ofUnordered(Calculator<V> calc, Timesegment<V>... segments) {
        return ofUnordered(calc, Arrays.asList(segments));
    }

    public static <V> Timeline<V> ofUnordered(Calculator<V> calc, Collection<Timesegment<V>> segments) {
        List<Timesegment<V>> combinedSegments = Timesegment.combineUnordered(Calculator.plus(calc), segments);
        return Timeline.ofDisjointAndOrdered(combinedSegments);
    }

}
