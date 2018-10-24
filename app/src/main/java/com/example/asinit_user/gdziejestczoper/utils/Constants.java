package com.example.asinit_user.gdziejestczoper.utils;


public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final int SERVICE_NOTIFICATION_ID = 1337;

    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String GEOCODING_FAILURE = "GEOCODING_FAILURE";

    public static final long START_DAY = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000);;
    public static final long END_DAY = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;
}
