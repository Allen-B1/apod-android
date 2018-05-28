package io.gitlab.allenb1.apod;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by allen on 5/26/18.
 */

public class ApodEntry implements Parcelable {
    public final static byte TYPE_IMAGE = 0;
    public final static byte TYPE_VIDEO = 1;

    private Date date = new Date();
    private String title;
    private String explanation;
    private String url;
    private byte mediaType;
    private String copyright;

    public static class FetchError extends Exception {
        private int mResponseCode;
        public FetchError(int responseCode) {
            mResponseCode = responseCode;
        }

        public int getResponseCode() {
            return mResponseCode;
        }

        @Override public String getMessage() {
            return Integer.toString(mResponseCode);
        }
    }

    protected ApodEntry(JSONObject response) {
        if (response.has("date"))
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(response.optString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        title = response.optString("title", null);
        url = response.optString("url", null);
        explanation = response.optString("explanation", null);
        copyright = response.optString("copyright", null);
        String mediaTypeString = response.optString("media_type");
        if ("image".equals(mediaTypeString))
            mediaType = TYPE_IMAGE;
        else
            mediaType = TYPE_VIDEO;
    }

    public static ApodEntry fetch(@Nullable String apiKey, @Nullable Date date) throws IOException, JSONException, FetchError {
        if (apiKey == null)
            apiKey = "DEMO_KEY";

        StringBuilder requestStringBuilder = new StringBuilder();
        requestStringBuilder.append("https://api.nasa.gov/planetary/apod?api_key=")
                .append(apiKey);
        if (date != null) {
            requestStringBuilder.append("&date=").append(new SimpleDateFormat("yyyy-MM-dd").format(date));
        }

        HttpsURLConnection connection = null;
        InputStream stream = null;
        try {
            connection = (HttpsURLConnection) new URL(requestStringBuilder.toString()).openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new FetchError(responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream == null)
                throw new IOException("stream is null");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int n; (n = stream.read(buffer)) >= 0; ) {
                out.write(buffer, 0, n);
            }
            String response = out.toString();
            return new ApodEntry(new JSONObject(response));
        } catch (MalformedURLException | JSONException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /* Acessor methods */
    public byte getMediaType() {
        return mediaType;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getUrl() {
        return url;
    }

    @Nullable public String getCopyright() {
        return copyright;
    }

    @Override public String toString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(this.date) + ": " + this.title;
    }

    /* Parcelable */
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                new SimpleDateFormat("yyMMdd").format(date),
                title,
                explanation,
                url,
                Byte.toString(mediaType),
                copyright
        });
    }

    public ApodEntry(Parcel in) {
        String[] data = new String[6];
        try {
            date = new SimpleDateFormat("yyMMdd").parse(data[0]);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        title = data[1];
        explanation = data[2];
        url = data[3];
        mediaType = Byte.valueOf(data[4]);
        copyright = data[5];
    }

    @Override public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ApodEntry createFromParcel(Parcel in) {
            return new ApodEntry(in);
        }

        public ApodEntry[] newArray(int size) {
            return new ApodEntry[size];
        }
    };

    /* Static utility methods */
    public static String dateToUrl(Date date) {
        return new StringBuilder("https://apod.nasa.gov/apod/ap").append(new SimpleDateFormat("yyMMdd").format(date)).append(".html").toString();
    }

    @Nullable public static Date urlToDate(Uri uri) throws IllegalStateException, ParseException {
        String path = uri.getPath();
        if(path.endsWith("astropix.html"))
            return new Date();

        Matcher matcher = Pattern.compile("\\d+").matcher(path);
        if(matcher.find())
            return new SimpleDateFormat("yyMMdd").parse(matcher.group());
        else
            throw new ParseException("No match found", 0);
    }
}