package org.aieseclchku.im.tap_in;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class EventListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ArrayList<EventObject> list;
    private ArrayAdapter arrayAdapter;
    private ListView listview;
    private int LengthOfResult;

    public static EventListFragment newInstance(String param1) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    private void makeGetRequestSearch() {
        // Building GET request Url with Member object passed
        String theUrl = getResources().getString(R.string.server) + "?opt=searchEvent&EventName=";
        Log.d("EncodedUrl: ", theUrl);

        // Using loopj AsyncHttp library to send http request
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle requestHandle = client.get(theUrl, new JsonHttpResponseHandler() {

            public void onStart(String theUrl) {
                // called before request is started
                Log.d("[GET] ", theUrl);
            }

            public void onSuccess(int statusCode, Header[] headers, JSONArray res) {
                // called when response HTTP status is "200 OK"
                Log.d("[GET 200] JSONArray: ", res.toString());
                LengthOfResult = res.length();
                StoreSearchResults(res);
            }

            public void onFailure(int statusCode, Header[] headers, JSONObject errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void StoreSearchResults(JSONArray res) {

        for (int i = 0; i < LengthOfResult; i++) {
            try {
                EventObject m = new EventObject();
                JSONObject json = res.getJSONObject(i);
                m.id = json.has("Id") ? Integer.parseInt(json.getString("Id")) : 0;
                m.name = json.has("EventName") ? json.getString("EventName") : "";

                arrayAdapter.add(m);
                arrayAdapter.notifyDataSetChanged();;


            }catch(JSONException e){
                e.printStackTrace();
                Log.d("[Update Table] JSONException", e.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_event_list, container, false);

        list = new ArrayList<EventObject>();
        addEvent("Activate List, do not remove",0);

        makeGetRequestSearch();


        listview = (ListView) v.findViewById(R.id.listEvents);

        arrayAdapter = new EventArrayAdapter(getActivity(), list);
        arrayAdapter.notifyDataSetChanged();
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Global global = ((Global)getActivity().getApplicationContext());
                global.setCurrEvent(list.get(position).name.toString());
                getFragmentManager().beginTransaction().replace(R.id.container, new EventDetailsFragment().newInstance(list.get(position).name.toString(), ""))
                        .addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });

        list.remove(0);

        return v;
    }

    public void addEvent(String s, int id){
        EventObject e = new EventObject(s, id);
        list.add(e);
        System.out.println("hi");
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
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }




}
