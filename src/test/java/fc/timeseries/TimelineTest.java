package fc.timeseries;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.threeten.extra.Interval;

public class TimelineTest {

    @Test
    public void shall_create_line_from_disjoint_ordered_segments() throws Exception {
        Interval interval_10 = TestHelper.interval("10:00", "11:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");

        Timeline<Double> line = Timeline.ofDisjointAndOrdered(new ValueFunctionTimesegment<>(interval_10, 2.0d),
                new ValueFunctionTimesegment<>(interval_11, 5.0d));

        Assertions.assertThat(line).isNotNull();

    }

    @Test
    public void shall_create_line_from_unordered_segments() throws Exception {
        Interval interval_10 = TestHelper.interval("10:00", "11:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");

        Timeline<Double> expected = Timeline.ofDisjointAndOrdered(
                new ValueFunctionTimesegment<>(interval_10, 2.0d),
                new ValueFunctionTimesegment<>(interval_11, 5.0d));

        Timeline<Number> actual = Timeline.ofUnordered(Operators.PLUS,
                new ValueFunctionTimesegment<>(interval_10, 2.0d),
                new ValueFunctionTimesegment<>(interval_11, 5.0d));

        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void shall_create_line_from_overlapping_unordered_segments() throws Exception {
        Interval interval_10_12 = TestHelper.interval("10:00", "12:00");
        Interval interval_11_13 = TestHelper.interval("11:00", "13:00");

        Interval interval_10_11 = TestHelper.interval("10:00", "11:00");
        Interval interval_11_12 = TestHelper.interval("11:00", "12:00");
        Interval interval_12_13 = TestHelper.interval("12:00", "13:00");

        Timeline<Double> expected = Timeline.ofDisjointAndOrdered(
                new ValueFunctionTimesegment<>(interval_10_11, 2.0d),
                new ValueFunctionTimesegment<>(interval_11_12, 7.0d),
                new ValueFunctionTimesegment<>(interval_12_13, 5.0d));

        Timeline<Number> actual = Timeline.ofUnordered(Operators.PLUS,
                new ValueFunctionTimesegment<>(interval_10_12, 2.0),
                new ValueFunctionTimesegment<>(interval_11_13, 5.0d));

        assertThat(actual).isEqualTo(expected);

    }
}
