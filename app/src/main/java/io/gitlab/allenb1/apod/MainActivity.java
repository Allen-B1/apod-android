package io.gitlab.allenb1.apod;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends ListActivity {
    final private DateFormat mDateFormat = DateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] list = new String[5];
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < list.length; i++) {
            list[i] = mDateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ArrayAdapter adapter = (ArrayAdapter)getListAdapter();
        String string = adapter.getItem(position).toString();
        try {
            Date date = mDateFormat.parse(string);
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra(ViewActivity.EXTRA_DATE, date.getTime());
            startActivity(intent);
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }
}
