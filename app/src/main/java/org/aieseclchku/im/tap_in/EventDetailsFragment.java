package org.aieseclchku.im.tap_in;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventDetailsFragment extends Fragment  {

    private static final String ARG_CurrEventName = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String CurrEventName = "";
    private String mParam2;
    private ArrayList<RegistrantObject> registrants;
    private ArrayAdapter arrayAdapter;
    private int LengthOfResult;
    private boolean EventSelected = false;
    private OnFragmentInteractionListener mListener;

    HttpRequest h;

    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CurrEventName, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EventDetailsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CurrEventName = getArguments().getString(ARG_CurrEventName);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Global global = ((Global)getActivity().getApplicationContext());
        CurrEventName = global.getCurrEvent();

        if (CurrEventName=="")
            getFragmentManager().beginTransaction().replace(R.id.container, new EventListFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_event_details, container, false);

        ListView listRegistrants = (ListView) v.findViewById(R.id.listRegistrants);
        TextView lblEventName = (TextView) v.findViewById(R.id.lblEventName);
        EditText txtFilterRegistrants = (EditText) v.findViewById(R.id.txtFilterRegistrants);

        lblEventName.setText(CurrEventName);

        registrants = new  ArrayList<>();

        arrayAdapter = new RegistrantDetailArrayAdapter(getActivity(), registrants);

        h = new HttpRequest();

        h.GetEventRegistrants(CurrEventName, arrayAdapter);

        txtFilterRegistrants.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Filter [" + s + "]");
                arrayAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TextView btnTakeAttendance = (TextView) v.findViewById(R.id.btnTakeAttendance);
        btnTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new TakeAttendanceFragment().newInstance(CurrEventName, "")).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack("Tag").commit();
                mListener.SelectedEvent(CurrEventName);
            }
        });

        arrayAdapter.notifyDataSetChanged();
        listRegistrants.setAdapter(arrayAdapter);


        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
        public void SelectedEvent(String s);
        public String getCurrentEventName();
    }


}

