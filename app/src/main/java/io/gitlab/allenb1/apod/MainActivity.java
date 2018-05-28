package io.gitlab.allenb1.apod;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends ListActivity {
    final private DateFormat mDateFormat = DateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setDivider(null);
        setListAdapter(new DateAdapter(this));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Add goto
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            Date date = (Date)getListAdapter().getItem(position);
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra(ViewActivity.EXTRA_DATE, date.getTime());
            startActivity(intent);
        } catch(ClassCastException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show();
        }
    }
}

class DateAdapter extends BaseAdapter {
    private Calendar mStartDate = Calendar.getInstance();
    private Context mContext;

    public DateAdapter(Context ctx) {
        super();
        mContext = ctx;
    }

    @Override public Date getItem(int position) {
        Calendar cal = (Calendar)mStartDate.clone();
        cal.add(Calendar.DATE, -position);
        return cal.getTime();
    }

    @Override
    public int getCount() {
        Calendar c = new GregorianCalendar(1995, 6, 16);
        return (int)((mStartDate.getTimeInMillis() - c.getTimeInMillis()) / (1000 * 60 * 60 * 24) + 1);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public int getItemViewType(int position) {
        if(position == 0) {
            return 1;
        }
        return 0;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(DateFormat.getDateInstance().format(getItem(position)));
        return convertView;
    }
}
