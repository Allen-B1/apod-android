package io.gitlab.allenb1.apod;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.util.Date;

public class ViewActivity extends Activity {
    public final static String EXTRA_DATE = "io.gitlab.allenb1.apod.EXTRA_DATE";

    private Date mDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().hasExtra(EXTRA_DATE)) {
            long date = getIntent().getLongExtra(EXTRA_DATE, -1);
            if(date >= 0) {
                mDate.setTime(date);
            }
        }

        actionBar.setTitle(DateFormat.getDateInstance().format(mDate));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, ViewFragment.newInstance(mDate));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.share, Menu.NONE, R.string.share).setIcon(R.drawable.ic_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, ApodEntry.dateToUrl(mDate));
                Intent chooser = Intent.createChooser(intent, getString(R.string.share_title));
                if(chooser.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
