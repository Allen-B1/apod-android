package io.gitlab.allenb1.apod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApodFetchTask task = new ApodFetchTask(new ApodFetchTask.Callback() {
            @Override
            public void onError(Exception e) {
                if(e != null)
                    e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.title);
                        textView.setTextColor(R.attr.colorError);
                        textView.setText(R.string.error);
                    }
                });
            }

            @Override
            public void onResult(final ApodEntry entry) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.title);
                        textView.setText(entry.getTitle());
                    }
                });
            }

            @Override
            public String getApiKey() {
                return null;
            }
        });
        task.execute();
    }
}
