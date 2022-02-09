package cronofy;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import okhttp3.*;

/**
 * @author Steven Massaro
 */
public class CronofyAPI {

    private final String token;

    public CronofyAPI(String token) {
        this.token = token;
    }

    /**
     * Add an event to the calendar specified.
     *
     * @param endpoint    Cronofy API endpoint
     * @param calendarId  Cronofy CalendarId
     * @param name        name of event
     * @param description description of event
     * @param start       UTC event start time
     * @param end         UTC event end time
     * @param tzid        Intended time zone identifier
     * @return network response code (202 for successful add)
     * @throws IOException
     */
    public int AddEvent(String endpoint, String calendarId, String eventId, String name, String description, Date start, Date end, String tzid) throws IOException {
        return AddEvent(endpoint, calendarId, eventId, name, description, formatDate(start, tzid), formatDate(end, tzid));
    }

    /**
     * Add an event to the calendar specified.
     *
     * @param endpoint    Cronofy API endpoint
     * @param calendarId  Cronofy CalendarId
     * @param name        name of event
     * @param description description of event
     * @param start       UTC event start time
     * @param end         UTC event end time
     * @return network response code (202 for successful add)
     * @throws IOException
     */
    public int AddEvent(String endpoint, String calendarId, String eventId, String name, String description, Date start, Date end) throws IOException {
        return AddEvent(endpoint, calendarId, eventId, name, description, formatDate(start), formatDate(end));
    }

    private int AddEvent(String endpoint, String calendarId, String eventId, String name, String description, String start, String end) throws IOException {

        Writer w = new StringWriter();
        JsonWriter writer = new JsonWriter(w);
        writer.beginObject();
        writer.name("event_id").value(eventId);
        writer.name("summary").value(name);
        writer.name("description").value(description);
        writer.name("start").value(start);
        writer.name("end").value(end);
        writer.endObject();
        writer.close();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, w.toString());
        Request request = new Request.Builder()
                .url(endpoint + "/calendars/" + calendarId + "/events")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Response response = client.newCall(request).execute();
        return response.networkResponse().code();
    }

    /**
     * Returns a string representation of the input, formatted as
     * 2011-12-03T10:15:30Z, and converted from UTC to the time zone specified.
     *
     * @param input UTC date/time
     * @param tzid  time zone identifier
     * @return ISO_INSTANT formatted date/time converted to time zone in tzid
     */
    private String formatDate(Date input, String tzid) {
        return input.toInstant().atZone(ZoneId.of(tzid)).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Returns a string representation of the input, formatted as 2011-12-03
     *
     * @param input UTC date/time
     * @return formatted date string
     */
    private String formatDate(Date input) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(input);
    }
}
