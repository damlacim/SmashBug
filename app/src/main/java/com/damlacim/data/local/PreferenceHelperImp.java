package com.damlacim.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelperImp implements PreferenceHelper {
    private static final String PREF_FILE_NAME = "game_prefs";
    private static final String KEY_SCORE = "score";

    private SharedPreferences prefs;

    public PreferenceHelperImp(Context context) {
        prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getScore() {
        return prefs.getInt(KEY_SCORE, 0);
    }

    @Override
    public void setScore(int score) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SCORE, score);
        editor.apply();
    }
}