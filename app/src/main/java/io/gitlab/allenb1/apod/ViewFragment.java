package io.gitlab.allenb1.apod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Date;

public class ViewFragment extends Fragment {
    private static final String ARG_DATE = "DATE";

    // TODO: Rename and change types of parameters
    private Date mDate;

    public static ViewFragment newInstance(Date date) {
        ViewFragment fragment = new ViewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }


    public ViewFragment() {}

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
        if(getView() != null)
            updateLayout(getView());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(ARG_DATE)) {
            mDate = new Date(getArguments().getLong(ARG_DATE));
        } else {
            mDate = new Date();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view, container, false);
        updateLayout(view);
        return view;
    }

    protected void updateLayout(final View view) {
        new ApodFetchTask(new ApodFetchTask.Callback() {
            @Override
            public void onError(Exception e) {
                if(e != null)
                    e.printStackTrace();
                if(getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final View errorView = view.findViewById(R.id.error);
                            errorView.setVisibility(View.VISIBLE);

                            final View contentView = view.findViewById(R.id.content);
                            contentView.setVisibility(View.GONE);
                        }
                    });
            }

            @Override
            public void onResult(final ApodEntry entry) {
                if(getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final TextView textView = view.findViewById(R.id.title);
                            textView.setText(entry.getTitle());

                            final TextView explanationView  =view.findViewById(R.id.explanation);
                            explanationView.setText(entry.getExplanation());

                            if(entry.getMediaType() == ApodEntry.TYPE_IMAGE) {
                                final ImageView imageView = view.findViewById(R.id.image);
                                imageView.setVisibility(View.VISIBLE);
                                new DownloadImageTask(imageView).execute(entry.getUrl());
                            } else {
                                final WebView webView = view.findViewById(R.id.video);
                                webView.setVisibility(View.VISIBLE);
                                webView.getSettings().setJavaScriptEnabled(true);
                                webView.setWebViewClient(new WebViewClient() {
                                    @Override public void onPageFinished(WebView webView, String url) {
                                        super.onPageFinished(webView, url);
                                        webView.getParent().requestLayout();
                                    }
                                });
                                webView.loadUrl(entry.getUrl());
                            }
                        }
                    });
            }
        }).execute(mDate);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}