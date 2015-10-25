package jp.tecco.acid_or_alkali.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by makotonishimoto on 2015/10/25.
 */
public class MyPreferences {
    public static final String DEFAULT_NAME = "pref";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public MyPreferences(Context context) {
        this(context, DEFAULT_NAME);
    }

    public MyPreferences(Context context, String name) {
        mPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public MyPreferences edit() {
        mEditor = mPref.edit();
        return this;
    }

    public void apply() {
        mEditor.apply();
        mEditor = null;
    }

    public int getTrueAnswerNum() {
        return mPref.getInt("trueAnswerNum", 0);
    }

    public MyPreferences putTrueAnswerNum(int value) {
        mEditor.putInt("trueAnswerNum", value);
        return this;
    }

    public int getFalseAnswerNum() {
        return mPref.getInt("falseAnswerNum", 0);
    }

    public MyPreferences putFalseAnswerNum(int value) {
        mEditor.putInt("falseAnswerNum", value);
        return this;
    }

    public int getPrefecturesId() {
        return mPref.getInt("prefectureId", 0);
    }

    public MyPreferences putPrefecturesId(int value) {
        mEditor.putInt("prefectureId", value);
        return this;
    }

    public int getStartCount() {
        return mPref.getInt("startCount", 0);
    }

    public MyPreferences putStartCount(int value) {
        mEditor.putInt("startCount", value);
        return this;
    }
}
