package fc.timeseries;

import java.util.Arrays;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.threeten.extra.Interval;

public class IntervalUtilTest {

    @Test
    public void shall_merge_non_overlapping_intervals() {
        Interval interval_10 = TestHelper.interval("10:00", "11:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");

        Interval expected = TestHelper.interval("10:00", "13:00");

        Collection<Interval> result = IntervalUtil.mergeIntervals(Arrays.asList(interval_11, interval_10));

        Assertions.assertThat(result).containsOnly(expected);

    }

    @Test
    public void shall_not_merge_disjoint_intervals() {
        Interval interval_10 = TestHelper.interval("10:00", "11:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");
        Interval interval_15 = TestHelper.interval("15:00", "17:00");

        Interval expected_10 = TestHelper.interval("10:00", "13:00");
        Interval expected_15 = TestHelper.interval("15:00", "17:00");

        Collection<Interval> result = IntervalUtil.mergeIntervals(Arrays.asList(interval_11, interval_10, interval_15));

        Assertions.assertThat(result).containsOnly(expected_10, expected_15);

    }

    @Test
    public void shall_ignore_duplicate_and_overlapping_intervals_when_merging() {
        Interval interval_10 = TestHelper.interval("10:00", "11:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");
        Interval interval_11_2 = TestHelper.interval("11:00", "13:00");
        Interval interval_11_3 = TestHelper.interval("11:30", "12:00");
        Interval interval_15 = TestHelper.interval("15:00", "17:00");

        Interval expected_10 = TestHelper.interval("10:00", "13:00");
        Interval expected_15 = TestHelper.interval("15:00", "17:00");

        Collection<Interval> result = IntervalUtil
                .mergeIntervals(Arrays.asList(interval_11, interval_11_2, interval_10, interval_15, interval_11_3));

        Assertions.assertThat(result).containsOnly(expected_10, expected_15);

    }

    @Test
    public void shall_split_overlapping_intervals() {
        Interval interval_10 = TestHelper.interval("10:00", "12:00");
        Interval interval_11 = TestHelper.interval("11:00", "13:00");

        Interval expected_10 = TestHelper.interval("10:00", "11:00");
        Interval expected_11 = TestHelper.interval("11:00", "12:00");
        Interval expected_12 = TestHelper.interval("12:00", "13:00");

        Collection<Interval> result = IntervalUtil
                .disjoinIntervals(Arrays.asList(interval_11, interval_10));

        Assertions.assertThat(result).containsOnly(expected_10, expected_11, expected_12);

    }

    @Test
    public void shall_not_split_non_overlapping_intervals() {
        Interval interval_10 = TestHelper.interval("10:00", "12:00");
        Interval interval_12 = TestHelper.interval("12:00", "13:00");

        Interval expected_10 = TestHelper.interval("10:00", "12:00");
        Interval expected_12 = TestHelper.interval("12:00", "13:00");

        Collection<Interval> result = IntervalUtil
                .disjoinIntervals(Arrays.asList(interval_10, interval_12));

        Assertions.assertThat(result).containsOnly(expected_10, expected_12);

    }

    @Test
    public void shall_not_split_non_adjacent_intervals() {
        Interval interval_10 = TestHelper.interval("10:00", "12:00");
        Interval interval_12 = TestHelper.interval("14:00", "15:00");

        Interval expected_10 = TestHelper.interval("10:00", "12:00");
        Interval expected_12 = TestHelper.interval("14:00", "15:00");

        Collection<Interval> result = IntervalUtil
                .disjoinIntervals(Arrays.asList(interval_10, interval_12));

        Assertions.assertThat(result).containsOnly(expected_10, expected_12);

    }

}
