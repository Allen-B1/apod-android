package io.gitlab.allenb1.apod;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class ApodFetchTask extends AsyncTask<Date, Void, Object> {
    private final static String API_KEY = "DEMO_KEY"; // change this to api key, otherwise DEMO_KEY will be used

    static interface Callback {
        void onError(@Nullable Exception e);
        void onResult(ApodEntry entry);
    }

    private Callback mCallback;

    public ApodFetchTask(Callback callback) {
        mCallback = callback;
    }

    @Override protected void onPreExecute() {}

    @Override protected Object doInBackground(Date... params) {
        Date date = null;
        if(params != null && params.length > 0) {
            date = params[0];
        }
        try {
            return ApodEntry.fetch(API_KEY, date);
        } catch(JSONException | IOException e) {
            return e;
        }
    }

    @Override protected void onPostExecute(Object result) {
        if(result instanceof ApodEntry) {
            mCallback.onResult((ApodEntry)result);
        } else if(result instanceof Exception) {
            mCallback.onError((Exception)result);
        } else {
            mCallback.onError(null);
        }
    }
}