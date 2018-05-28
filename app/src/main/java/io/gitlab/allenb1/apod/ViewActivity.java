package io.gitlab.allenb1.apod;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ViewActivity extends Activity {
    public final static String EXTRA_DATE = "io.gitlab.allenb1.apod.EXTRA_DATE";

    private Date mDate = new Date();
    private ViewFragment mFragment = ViewFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(DateFormat.getDateInstance().format(mDate));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, mFragment);
        transaction.commit();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.share, Menu.NONE, R.string.share).setIcon(R.drawable.ic_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        update();
    }

    /* Parses a new intent */
    private void update() {
        if(getIntent() != null) {
            if(getIntent().hasExtra(EXTRA_DATE)) {
                long date = getIntent().getLongExtra(EXTRA_DATE, -1);
                if(date >= 0) {
                    mFragment.setDate(new Date(date));
                }
            }

            else if(getIntent().getData() != null)
                try {
                    Date date = ApodEntry.urlToDate(getIntent().getData());
                    if(date != null)
                        mFragment.setDate(date);
                } catch(ParseException | IllegalStateException e) {
                    Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        mFragment.update();
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
