package codes.vps.mockta.util;

public interface C {

    long HOUR_IN_DAY = 24L;
    long MIN_IN_HOUR = 60L;
    long SEC_IN_MIN = 60L;
    long DAYS_IN_WEEK = 7;
    long SEC_IN_DAY =
            SEC_IN_MIN * MIN_IN_HOUR * HOUR_IN_DAY;
    long SEC_IN_HOUR = SEC_IN_MIN * MIN_IN_HOUR;
    long MSEC_IN_SEC = 1000L;
    long MSEC_IN_MIN = MSEC_IN_SEC * SEC_IN_MIN;
    long MSEC_IN_HOUR = MSEC_IN_MIN * MIN_IN_HOUR;
    long MSEC_IN_DAY = MSEC_IN_HOUR * HOUR_IN_DAY;
    long MSEC_IN_WEEK = MSEC_IN_DAY * DAYS_IN_WEEK;
    long NSEC_IN_SEC = 1000000000L;
    long NSEC_IN_MSEC = 1000000L;

}
