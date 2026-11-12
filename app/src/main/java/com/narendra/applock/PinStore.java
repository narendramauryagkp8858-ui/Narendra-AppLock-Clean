package com.narendra.applock;

import android.content.Context;
import android.content.SharedPreferences;

public class PinStore {

    private static final String PREFS = "narendra_applock";
    private static final String PIN_KEY = "user_pin";
    private static final String DEFAULT_PIN = "8546";

    public static String getPin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE
        );

        return prefs.getString(PIN_KEY, DEFAULT_PIN);
    }

    public static boolean checkPin(Context context, String pin) {
        return getPin(context).equals(pin);
    }

    public static void savePin(Context context, String pin) {
        SharedPreferences prefs = context.getSharedPreferences(
            PREFS, Context.MODE_PRIVATE
        );

        prefs.edit().putString(PIN_KEY, pin).apply();
    }
}
