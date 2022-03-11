/*
 * Copyright (c) 2022 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package codes.vps.mockta.util;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class RFC3339 implements Comparable<RFC3339> {

    private GregorianCalendar holder;
    private final OnDemand<Timestamp> timestamp =
            new OnDemand<>(() -> new Timestamp(holder.getTime().getTime()));

    public RFC3339(@NonNull Date d) {
        this(d.getTime());
    }

    public RFC3339(long d) {
        holder = new GregorianCalendar();
        // note that calendar will remain in "default" time zone.
        holder.setTimeInMillis(d);
    }

    public Timestamp getTimestamp() {
        return timestamp.get();
    }

    public long getDate() {
        return holder.getTimeInMillis();
    }

    private static int windInt(StringWinder sw, int count, int max, int min) {

        int aux = 0;
        for (int i = 0; i < count; i++) {
            char c = sw.next();
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("Invalid numeric character:" + c);
            }
            aux *= 10;
            aux += (c - '0');
        }
        if (max >= 0 && aux > max) {
            throw new IllegalArgumentException("Value " + aux + " must be less or equal than " + max);
        }
        if (min >= 0 && aux < min) {
            throw new IllegalArgumentException("Value " + aux + " must be greater or equal than " + min);
        }
        return aux;

    }

    // parse RFC3339 declaration.
    public RFC3339(String s) {
        this(s, false, false);
    }

    public RFC3339(String s, boolean forbidTZ, boolean forbidTime) {

        try {

            StringWinder sw = new ForwardString(s);
            holder = new GregorianCalendar();

            // do not set time zone before time, or else the time changes,
            // as initial time zone is platform-based!

            int year;
            int month;
            int day;
            int hour = 0;
            int minute = 0;
            int second = 0;
            int ms = 0;
            boolean round = false;

            do {

                // date-time = full-date 'T' full-time
                // full-date = date-fullyear "-" date-month "-" date-mday

                // date-fullyear = 4DIGIT
                year = windInt(sw, 4, -1, 0);

                char c;
                if ((c = sw.next()) != '-') {
                    throw new IllegalArgumentException("Missing year separator, got:" + c);
                }

                // date-month = 2DIGIT
                month = windInt(sw, 2, 12, 1) - 1;

                if ((c = sw.next()) != '-') {
                    throw new IllegalArgumentException("Missing month separator, got:" + c);
                }

                // date-mday = 2DIGIT
                day = windInt(sw, 2, 31, 1);

                if (forbidTime) {
                    if (!sw.hasNext()) {
                        break;
                    }
                    throw new IllegalArgumentException("Unexpected characters after date portion");
                }

                if ((c = sw.next()) != 'T') {
                    throw new IllegalArgumentException("Missing time separator, got:" + c);
                }

                // full-time = partial-time time-offset
                // partial-time = time-hour ":" time-minute ":" time-second [time-secfrac]
                // time-hour = 2DIGIT
                hour = windInt(sw, 2, 23, 0);
                if ((c = sw.next()) != ':') {
                    throw new IllegalArgumentException("Missing hour separator, got:" + c);
                }
                // time-minute     = 2DIGIT
                minute = windInt(sw, 2, 59, 0);
                if ((c = sw.next()) != ':') {
                    throw new IllegalArgumentException("Missing minute separator, got:" + c);
                }
                // time-second     = 2DIGIT
                second = windInt(sw, 2, 59, 0);

                if (forbidTZ && !sw.hasNext()) {
                    break;
                }

                c = sw.next();
                if (c == '.') {
                    // we have a present secfrac
                    // time-secfrac = "." 1*DIGIT

                    int seen = 0;

                    while (true) {
                        if (forbidTZ && !sw.hasNext()) {
                            break;
                        }
                        c = sw.next();
                        if (c < '0' || c > '9') {
                            if (forbidTZ) {
                                throw new IllegalArgumentException("Extra characters " + sw.remainder());
                            }
                            break;
                        }
                        seen++;
                        if (seen <= 3) {
                            ms *= 10;
                            ms += (c - '0');
                        } else if (seen == 4) {
                            if (c >= '5') {
                                round = true;
                            }
                        }
                    }

                    if (seen == 0) {
                        throw new IllegalArgumentException("Second fraction contains no number");
                    }

                } else {
                    if (forbidTZ) {
                        throw new IllegalArgumentException("Extra characters " + sw.remainder());
                    }
                }

                if (forbidTZ) {
                    if (sw.hasNext()) {
                        throw new IllegalArgumentException("Extra characters " + sw.remainder());
                    }
                    break;
                }

                if (c == '+' || c == '-') {
                    // time-numoffset  = ("+" / "-") time-hour ":" time-minute
                    int tzHour = windInt(sw, 2, -1, 0);
                    char c2;
                    if ((c2 = sw.next()) != ':') {
                        throw new IllegalArgumentException("Missing time zone hour separator, got:" + c2);
                    }
                    int min = windInt(sw, 2, 59, 0);
                    int offset = tzHour * (int) C.MSEC_IN_HOUR + min * (int) C.MSEC_IN_MIN;
                    if (c == '-') {
                        offset = -offset;
                    }
                    // holder.setTimeZone(TimeZone.getTimeZone(TimeZone.getAvailableIDs(offset)[0]));
                    // new TimeZone()
                    holder.setTimeZone(new SimpleTimeZone(offset, String.valueOf(offset)));
                } else if (c == 'Z') {
                    holder.setTimeZone(TimeZone.getTimeZone("UTC"));
                } else {
                    throw new IllegalArgumentException("Invalid time zone character:" + c);
                }

            } while (false);

            // if we are here, the parsing is successful, and all of parts are
            // populated.

            holder.set(Calendar.MILLISECOND, ms);
            if (round) {
                holder.add(Calendar.MILLISECOND, 1);
            }
            holder.set(Calendar.SECOND, second);
            holder.set(Calendar.MINUTE, minute);
            holder.set(Calendar.HOUR_OF_DAY, hour);
            holder.set(Calendar.DATE, day);
            holder.set(Calendar.MONTH, month);
            holder.set(Calendar.YEAR, year);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse input string " + s, e);
        }

    }

    /**
     * Use this method to simply changes the internal time zone of the date.
     * The time point does not change in this case, the time print out will
     * change, i.e. if it was 3pm on some day in California, and time zone
     * is changed to New York, it will become 6pm in New York.
     *
     * @param tz time zone to change to
     * @return this instance for convenience
     * @see #applyTimeZone(String)
     */
    public RFC3339 changeTimeZone(@NonNull String tz) {
        TimeZone jtz = TimeZone.getTimeZone(tz);
        holder.setTimeZone(jtz);
        return this;
    }

    /**
     * Applies time zone without changing the local time. This changes the point
     * in time, since, if this was 3pm on some day in California, it will
     * become, if time zone is changed to, say, New York, 3pm at New York,
     * which obviously represents a different time.
     *
     * @param tz time zone to apply
     * @return this instance for convenience
     */
    public RFC3339 applyTimeZone(@NonNull String tz) {

        // we don't need to manipulate seconds and millis, because they are
        // not affected by a time zone shift, the worst time zone can do is
        // to change minutes.

        int minute = holder.get(Calendar.MINUTE);
        int hour = holder.get(Calendar.HOUR_OF_DAY);
        int day = holder.get(Calendar.DATE);
        int month = holder.get(Calendar.MONTH);
        int year = holder.get(Calendar.YEAR);

        changeTimeZone(tz);

        holder.set(Calendar.MINUTE, minute);
        holder.set(Calendar.HOUR_OF_DAY, hour);
        holder.set(Calendar.DATE, day);
        holder.set(Calendar.MONTH, month);
        holder.set(Calendar.YEAR, year);

        return this;

    }

    public String toString() {
        return toString(false, false);
    }

    public String toString(boolean noTime, boolean noTimeZone) {

        // Some pieces were taken from
        // http://mericleclerin.blogspot.jp/2013/07/internetdateformat-for-rfc3339.html
        StringBuilder toAppendTo = new StringBuilder();
        // int offset = holder.get(Calendar.ZONE_OFFSET);
        int offset = holder.getTimeZone().getOffset(holder.getTimeInMillis());

        toAppendTo.append(String.format(Locale.US, "%1$04d-%2$02d-%3$02d",
                holder.get(Calendar.YEAR), holder.get(Calendar.MONTH) + 1, holder.get(Calendar.DAY_OF_MONTH)));

        if (!noTime) {
            toAppendTo.append(String.format(Locale.US, "T%1$02d:%2$02d:%3$02d",
                    holder.get(Calendar.HOUR_OF_DAY), holder.get(Calendar.MINUTE), holder.get(Calendar.SECOND)));

            int millis = holder.get(Calendar.MILLISECOND);

            if (millis != 0) {
                toAppendTo.append(String.format(Locale.US, ".%03d", holder.get(Calendar.MILLISECOND)));
            }

        }

        if (!noTimeZone) {
            if (offset == 0) {
                toAppendTo.append('Z');
            } else {
                char c;
                if (offset < 0) {
                    c = '-';
                    offset = -offset;
                } else {
                    c = '+';
                }
                toAppendTo.append(c);
                toAppendTo.append(String.format(Locale.US,
                        "%1$02d:%2$02d",
                        offset / (C.MSEC_IN_HOUR),
                        offset % (C.MSEC_IN_HOUR) / C.MSEC_IN_MIN));
            }
        }

        return toAppendTo.toString();

    }

    @Override
    public int compareTo(@NonNull RFC3339 o) {

        long l1 = getDate() - o.getDate();
        if (l1 < 0) {
            return -1;
        }
        if (l1 > 0) {
            return 1;
        }
        return 0;

    }

    public boolean before(RFC3339 another) {
        return another.compareTo(this) > 0;
    }

    public boolean after(RFC3339 another) {
        return another.compareTo(this) < 0;
    }

    @Nullable
    public static RFC3339 makeOrNull(@Nullable Date d) {
        if (d == null) {
            return null;
        }
        return new RFC3339(d);
    }

    public Calendar getHolder() {
        return holder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timestamp.get());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RFC3339 && timestamp.get().equals(((RFC3339) obj).timestamp.get());
    }

}
