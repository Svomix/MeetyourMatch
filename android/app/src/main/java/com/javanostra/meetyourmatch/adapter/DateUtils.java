package com.javanostra.meetyourmatch.adapter;

import android.content.Context;
import android.text.format.DateFormat;

import com.javanostra.meetyourmatch.R;
import com.javanostra.meetyourmatch.persistance.entity.ChatMessage;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    public static String formatTime(Context context, long timeInMillis) {
        if (context == null || timeInMillis <= 0) return "";
        return DateFormat.getTimeFormat(context.getApplicationContext()).format(timeInMillis);
    }

    public static String formatDateForHeader(Context context, long timeInMillis) {
        if (context == null || timeInMillis <= 0) return "";
        Calendar messageTime = Calendar.getInstance(Locale.getDefault());
        messageTime.setTimeInMillis(timeInMillis);
        Calendar now = Calendar.getInstance(Locale.getDefault());

        if (isSameDay(messageTime, now)) {
            return context.getString(R.string.date_header_today);

        } else {
            Calendar yesterday = Calendar.getInstance(Locale.getDefault());
            yesterday.add(Calendar.DATE, -1);
            if (isSameDay(messageTime, yesterday)) {
                return context.getString(R.string.date_header_yesterday);

            } else if (isSameYear(messageTime, now)) {
                return DateFormat.format("dd MMMM", messageTime).toString();

            } else {
                return DateFormat.format("dd MMMM yyyy", messageTime).toString();

            }
        }
    }


    public static boolean isSameDay(long millis1, long millis2) {
        if (millis1 <= 0 || millis2 <= 0) return false;

        Calendar cal1 = Calendar.getInstance(Locale.getDefault());
        cal1.setTimeInMillis(millis1);

        Calendar cal2 = Calendar.getInstance(Locale.getDefault());
        cal2.setTimeInMillis(millis2);

        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameYear(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean shouldAddDateHeader(Timestamp previousTimestamp, Timestamp currentTimestamp) {
        if (currentTimestamp == null || currentTimestamp.getTime() <= 0) {
            return false; // Нет текущего сообщения или некорректный timestamp
        }
        if (previousTimestamp == null || previousTimestamp.getTime() <= 0) {
            return true; // Предыдущего сообщения нет, значит, для текущего нужен заголовок
        }

        Calendar prevCal = Calendar.getInstance(Locale.getDefault());
        prevCal.setTimeInMillis(previousTimestamp.getTime());
        Calendar currCal = Calendar.getInstance(Locale.getDefault());
        currCal.setTimeInMillis(currentTimestamp.getTime());

        return !isSameDay(prevCal, currCal);
    }

    public static boolean shouldAddDateHeader(ChatMessage previousMessage, ChatMessage currentMessage) {

        if (currentMessage == null || currentMessage.getTimestamp().getTime() <= 0) return false;
        if (previousMessage == null || previousMessage.getTimestamp().getTime() <= 0) return true;

        Calendar prevCal = Calendar.getInstance(Locale.getDefault());
        prevCal.setTimeInMillis(previousMessage.getTimestamp().getTime());
        Calendar currCal = Calendar.getInstance(Locale.getDefault());
        currCal.setTimeInMillis(currentMessage.getTimestamp().getTime());

        return !isSameDay(prevCal, currCal);
    }
}