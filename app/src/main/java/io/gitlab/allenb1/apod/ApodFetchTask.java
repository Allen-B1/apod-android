package io.gitlab.allenb1.apod;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class ApodFetchTask extends AsyncTask<Date, Void, ApodEntry> {
    static interface Callback {
        void onError();
        void onResult(ApodEntry entry);
        String getApiKey();
    }

    private Callback mCallback;

    public ApodFetchTask(Callback callback) {
        mCallback = callback;
    }

    @Override protected void onPreExecute() {}

    @Override protected ApodEntry doInBackground(Date... params) {
        if(params != null && params.length > 0) {
            Date date = params[0];
            try {
                return ApodEntry.fetch(mCallback.getApiKey(), date);
            } catch(JSONException | IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override protected void onPostExecute(ApodEntry result) {
        if(result != null) {
            mCallback.onResult(result);
        } else {
            mCallback.onError();
        }
    }
}