package io.gitlab.allenb1.apod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private static final String ARG_ENTRY = "ENTRY";

    // TODO: Rename and change types of parameters
    @NonNull private Date mDate = new Date();
    @Nullable private ApodEntry mEntry = null;

    public static ViewFragment newInstance(Date date) {
        ViewFragment fragment = new ViewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    public static ViewFragment newInstance() {
        return new ViewFragment();
    }


    public ViewFragment() {}

    /* Returns date */
    public Date getDate() {
        return mDate;
    }

    /* Sets date */
    public void setDate(Date date) {
        mDate.setTime(date.getTime());
    }

    /* Refetches entry & displays it if possible */
    public void update() {
        if(mEntry == null || mDate != mEntry.getDate())
            refetch(new Runnable() {
                @Override
                public void run() {
                    if (getView() != null)
                        showData(getView(), mEntry);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    if(getView() != null)
                        showError(getView());
                }
            });
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getParcelable(ARG_ENTRY) instanceof ApodEntry) {
            mEntry = savedInstanceState.getParcelable(ARG_ENTRY);
            mDate.setTime(mEntry.getDate().getTime());
        }

        if(getArguments() != null && getArguments().containsKey(ARG_DATE)) {
            mDate.setTime(getArguments().getLong(ARG_DATE));
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ENTRY, mEntry);
    }

    /* Creates view. If entry is loaded, shows entry */
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        if(mEntry != null)
            showData(view, mEntry);
        return view;
    }

    /* Shows entry to given view */
    private void showData(@NonNull final View view, final ApodEntry entry) {
        final View errorView = view.findViewById(R.id.error);
        errorView.setVisibility(View.GONE);

        final View contentView = view.findViewById(R.id.content);
        contentView.setVisibility(View.VISIBLE);

        final TextView textView = view.findViewById(R.id.title);
        textView.setText(entry.getTitle());

        final TextView explanationView  =view.findViewById(R.id.explanation);
        explanationView.setText(entry.getExplanation());

        final TextView copyrightView = view.findViewById(R.id.copyright);
        if(entry.getCopyright() != null) {
            copyrightView.setText(entry.getCopyright());
            copyrightView.setVisibility(View.VISIBLE);
        } else {
            copyrightView.setVisibility(View.GONE);
        }

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

        view.invalidate();
    }

    /* Shows error */
    private void showError(View view) {
        final View errorView = view.findViewById(R.id.error);
        errorView.setVisibility(View.VISIBLE);

        final View contentView = view.findViewById(R.id.content);
        contentView.setVisibility(View.GONE);
    }

    /* Reloads entry. */
    private void refetch(@Nullable final Runnable onLoad,
                         @Nullable final Runnable onError) {
        new ApodFetchTask(new ApodFetchTask.Callback() {
            @Override
            public void onError(Integer responseCode) {
                if(getActivity() != null && onError != null)
                    getActivity().runOnUiThread(onError);
            }

            @Override
            public void onResult(final ApodEntry entry) {
                mEntry = entry;
                if(getActivity() != null && onLoad != null)
                    getActivity().runOnUiThread(onLoad);
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