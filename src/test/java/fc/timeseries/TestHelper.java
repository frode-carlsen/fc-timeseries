package fc.timeseries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.threeten.extra.Interval;

public class TestHelper {
    public static Instant medTime(String text) {
        return medTime(LocalDate.now(), text);
    }

    public static Instant medTime(LocalDate date, String text) {
        return LocalDateTime.of(date, LocalTime.parse(text)).atZone(ZoneId.systemDefault()).toInstant();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cloneSerializable(T source) {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buf);
            oos.writeObject(source);
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Interval interval(String fra, String til) {
        return Interval.of(medTime(fra), medTime(til));
    }

    public static Interval interval(LocalDate dato, String tid1, String tid2) {
        return Interval.of(medTime(dato, tid1), medTime(dato, tid2));
    }

}
