
package com.example.app_learn_chinese_2025.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {

    // Các format ngày có thể có từ server
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS",      // 2025-06-11T13:26:26.677
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",     // 2025-06-11T13:26:26.677Z
            "yyyy-MM-dd'T'HH:mm:ss",          // 2025-06-11T13:26:26
            "yyyy-MM-dd'T'HH:mm:ssZ",         // 2025-06-11T13:26:26Z
            "yyyy-MM-dd HH:mm:ss",            // 2025-06-11 13:26:26
            "yyyy-MM-dd"                      // 2025-06-11
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (json == null || json.isJsonNull()) {
            return null;
        }

        String dateString = json.getAsString();

        // Thử parse với từng format
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getDefault());
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // Thử format tiếp theo
                continue;
            }
        }

        // Nếu không parse được với format nào, thử parse ISO 8601 default
        try {
            // Remove Z if present và parse
            String cleanDateString = dateString.replace("Z", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            return sdf.parse(cleanDateString);
        } catch (ParseException e) {
            throw new JsonParseException("Unable to parse date: " + dateString, e);
        }
    }
}