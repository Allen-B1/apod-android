import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by allen on 5/26/18.
 */

public class ApodEntry {
    public final static byte TYPE_IMAGE = 0;
    public final static byte TYPE_VIDEO = 1;

    private Date date;
    private String title;
    private String explanation;
    private String url;
    private byte media_type;

    private ApodEntry() {

    }

    public static ApodEntry fetch(@Nullable String apiKey, @Nullable Date date) {
        if(apiKey == null)
            apiKey = "DEMO_KEY";

        if(date == null)
            date = new Date();

        return null;
    }
}
