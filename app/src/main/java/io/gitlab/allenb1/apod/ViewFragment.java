package io.gitlab.allenb1.apod;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class ViewFragment extends Fragment {
    private static final String ARG_DATE = "DATE";

    // TODO: Rename and change types of parameters
    private Date mDate;


    public ViewFragment() {
        // Required empty public constructor
    }

    public static ViewFragment newInstance(Date date) {
        ViewFragment fragment = new ViewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());
        fragment.setArguments(args);
        return fragment;
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
        new ApodFetchTask(new ApodFetchTask.Callback() {
            @Override
            public void onError(Exception e) {
                if(e != null)
                    e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView textView = view.findViewById(R.id.title);
                        textView.setTextColor(R.attr.colorError);
                        textView.setText(R.string.error);
                    }
                });
            }

            @Override
            public void onResult(final ApodEntry entry) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView textView = view.findViewById(R.id.title);
                        textView.setText(entry.getTitle());

                        final TextView explanationView  =view.findViewById(R.id.explanation);
                        explanationView.setText(entry.getExplanation());
                    }
                });
            }
        }).execute(mDate);
        // Inflate the layout for this fragment
        return view;
    }

}
