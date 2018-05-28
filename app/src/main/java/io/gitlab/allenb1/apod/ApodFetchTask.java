package io.gitlab.allenb1.apod;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class ApodFetchTask extends AsyncTask<Date, Void, Object> {
    private final static String API_KEY = "3H1YW7OyWq4JW19GU4EWnF4gEmGiqSc2xg10GgYr"; // change this to api key, otherwise DEMO_KEY will be used

    static interface Callback {
        void onError(@Nullable Integer responseCode);
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
        } catch(JSONException | IOException | ApodEntry.FetchError e) {
            return e;
        }
    }

    @Override protected void onPostExecute(Object result) {
        if(result instanceof ApodEntry) {
            mCallback.onResult((ApodEntry) result);
        } else if(result instanceof ApodEntry.FetchError) {
            mCallback.onError(((ApodEntry.FetchError) result).getResponseCode());
        } else {
            mCallback.onError(null);
        }
    }
}