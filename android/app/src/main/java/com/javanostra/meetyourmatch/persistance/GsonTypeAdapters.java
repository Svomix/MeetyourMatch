package com.javanostra.meetyourmatch.persistance;

import android.util.Log; // Для логов при ошибке парсинга

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat; // Для парсинга ISO 8601
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone; // Для UTC/GMT

public class GsonTypeAdapters {

    private static final String TAG = "GsonTypeAdapters";

    private static final ThreadLocal<DateFormat> iso8601FormatLocal = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    });
    private static final ThreadLocal<DateFormat> iso8601FormatLocalNoMillis = ThreadLocal.withInitial(() -> {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    });

    public static class TimestampTypeAdapter extends TypeAdapter<Timestamp> {
        @Override
        public void write(JsonWriter out, Timestamp value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.getTime());
            }
        }

        @Override
        public Timestamp read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek == JsonToken.NUMBER) {
                long millis = in.nextLong();
                return new Timestamp(millis);
            } else if (peek == JsonToken.STRING) {
                String dateString = in.nextString();
                try {
                    Date parsedDate = iso8601FormatLocal.get().parse(dateString);
                    return new Timestamp(parsedDate.getTime());
                } catch (ParseException e1) {
                    try {
                        Date parsedDate = iso8601FormatLocalNoMillis.get().parse(dateString);
                        return new Timestamp(parsedDate.getTime());
                    } catch (ParseException e2) {
                        Log.e(TAG, "Could not parse timestamp string: " + dateString, e2);
                        return null;
                    }
                }
            } else {
                Log.w(TAG, "Unexpected token type for Timestamp: " + peek);
                in.skipValue();
                return null;
            }
        }
    }

    public static class DateTypeAdapter extends TypeAdapter<Date> {
        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.getTime());
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek == JsonToken.NUMBER) {
                long millis = in.nextLong();
                return new Date(millis);
            } else if (peek == JsonToken.STRING) {
                String dateString = in.nextString();
                try {
                    Date parsedDate = iso8601FormatLocal.get().parse(dateString);
                    return parsedDate;
                } catch (ParseException e1) {
                    try {
                        Date parsedDate = iso8601FormatLocalNoMillis.get().parse(dateString);
                        return parsedDate;
                    } catch (ParseException e2) {
                        Log.e(TAG, "Could not parse date string: " + dateString, e2);
                        return null;
                    }
                }
            } else {
                Log.w(TAG, "Unexpected token type for Date: " + peek);
                in.skipValue();
                return null;
            }
        }
    }
}