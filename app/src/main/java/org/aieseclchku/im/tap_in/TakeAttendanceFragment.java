package org.aieseclchku.im.tap_in;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class TakeAttendanceFragment extends Fragment implements CardReader.AccountCallback, RegistrantSimpleArrayAdapter.RegistrantAdapterCallback {
    private static final String ARG_CurrEventName = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_B| NfcAdapter.FLAG_READER_NFC_BARCODE
                    | NfcAdapter.FLAG_READER_NFC_F| NfcAdapter.FLAG_READER_NFC_V;
    public CardReader mCardReader;
    private int ver;
    private EditText txtSearch_attend;
    private ArrayList<RegistrantObject> registrants;
    private RegistrantSimpleArrayAdapter arrayAdapter;
    private String CurrEventName = "Please select an event first";
    private OnFragmentInteractionListener mListener;
    private AlertDialog.Builder builder;
    private String[] dummy = {"","","",""};

    private HttpRequest h = new HttpRequest();

    public static TakeAttendanceFragment newInstance(String param1, String param2) {
        TakeAttendanceFragment fragment = new TakeAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CurrEventName, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global global = ((Global)getActivity().getApplicationContext());
        CurrEventName = global.getCurrEvent();
        if (global.getCurrEvent().equals(""))
            getFragmentManager().beginTransaction().replace(R.id.container, new EventListFragment()).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        if (getArguments()!=null) {
            CurrEventName = getArguments().getString(ARG_CurrEventName);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_take_attendance, container, false);

        TextView lblTap = (TextView) view.findViewById(R.id.lblTap);
        TextView lblOr = (TextView) view.findViewById(R.id.lblOr);
        TextView lblCurrEventName = (TextView) view.findViewById(R.id.lblCurrEventName);
        ListView listRegistrants = (ListView) view.findViewById(R.id.listRegistrants_attend);

        txtSearch_attend = (EditText)view.findViewById(R.id.txtSearch_attend);
        registrants = new ArrayList<>();


        lblCurrEventName.setText("Current Event : " + CurrEventName);
        txtSearch_attend.setEnabled(true);
        txtSearch_attend.setHintTextColor(getResources().getColor(R.color.white));
        lblTap.setTextColor(getResources().getColor(R.color.white));
        lblOr.setTextColor(getResources().getColor(R.color.white));

        ver = Integer.valueOf(android.os.Build.VERSION.SDK);
        Log.d("Ver:", Integer.toString(ver));
        if (ver >= 19) {
            mCardReader = new CardReader(this);
            enableReaderMode();
        }

        arrayAdapter = new RegistrantSimpleArrayAdapter(getActivity(), registrants);
        arrayAdapter.setCallback(this);

        h.GetEventRegistrants(CurrEventName, arrayAdapter);

        txtSearch_attend.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Filter [" + s + "]");
                arrayAdapter.getFilter().filter(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }
        });

        arrayAdapter.sort(new Comparator<PersonObject>() {
            public int compare(PersonObject item1, PersonObject item2) {
                return item1.name.toLowerCase().compareTo(item2.name.toLowerCase());
            }
        });

        arrayAdapter.notifyDataSetChanged();
        listRegistrants.setAdapter(arrayAdapter);

        return view;

    }

    private void enableReaderMode() {
        Log.d("","Enabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.d("", "Disabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    public void onAccountReceived(final String tagID) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                RegistrantObject r = arrayAdapter.findByTag(tagID);


                System.out.println("TagID = "+ tagID);
                System.out.println(CurrEventName);

                HttpRequest h2 = new HttpRequest(getActivity());

                if (r==null)
                    h2.TakeAttendanceWithTagID(CurrEventName, tagID,dummy,false,"");
                else
                    h2.TakeAttendanceWithTagID(CurrEventName, tagID,r.remark,true,r.name);


                /**if(true){
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Attendance Recorded");
                    builder.setMessage("Take Attendance for: " + r.name );
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int a) {
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                }*/
            }
        });
    }

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
    @Override
    public void onPause() {
        super.onPause();
        if (ver >= 19) {disableReaderMode();}
    }
    @Override
    public void onResume() {
        super.onResume();
        if (ver >= 19) {enableReaderMode();}
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
        public boolean EventSelectedOrNot();
        public String getCurrentEventName();
    }

    @Override
    public void returnPerson(final RegistrantObject r){

        String remark = "There is no remark.";
        int counter = 0 ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int a) {

                HttpRequest h1 = new HttpRequest(getActivity());
                h1.TakeAttendanceWithMobile(CurrEventName, r.mobile,r.remark,r.name);

                txtSearch_attend.setText("");
            }
        });

        for (int i = 0 ; i < 4 ; i++)
            if (!r.remark[i].equals("")) {

                if (counter == 0)
                    remark = "Remarks : ";

                if (counter != 0)
                    remark+= " | " ;
                remark += r.remark[i] ;
                counter ++ ;
            }
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int a) {  }
        });
        builder.setMessage("Take Attendance for " + r.name + " ?" + "\n" + remark);
        builder.setTitle("Take Attendance");
        builder.create();
        builder.show();

        txtSearch_attend.selectAll();


    }
}
