package org.aieseclchku.im.tap_in;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import android.os.Handler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.nfc.NfcAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.Comparator;

public class RegisterCardFragment extends Fragment implements CardReader.AccountCallback, PersonArrayAdapter.PersonAdapterCallback{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "CardReaderFragment";
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_B| NfcAdapter.FLAG_READER_NFC_BARCODE
            | NfcAdapter.FLAG_READER_NFC_F| NfcAdapter.FLAG_READER_NFC_V;
    public CardReader mCardReader;
    private String SelectedMobile;
    private boolean TagIDreceived;
    private int ver;
    private EditText txtSearch_reg;
    private ListView listUnregisteredPersons;
    private Button btnAddMobile;
    private int LengthOfResult;

    private HttpRequest h = new HttpRequest();

    private ArrayList<PersonObject> persons = new ArrayList<>();;
    private PersonArrayAdapter arrayAdapter;

    private ProgressDialog progressdialog;

    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public static RegisterCardFragment newInstance(String param1, String param2) {
        RegisterCardFragment fragment = new RegisterCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterCardFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_card, container, false);

        txtSearch_reg = (EditText)view.findViewById(R.id.txtSearch_reg);
        listUnregisteredPersons = (ListView) view.findViewById(R.id.listUnregisteredPersons);
        btnAddMobile = (Button) view.findViewById(R.id.btnAddMobile);
        TextView lblRegCard = (TextView) view.findViewById(R.id.lblRegCard);
        TextView lblUpdate = (TextView) view.findViewById(R.id.lblUpdate);

        TextView load = new TextView(getActivity());
        load.setText("Loading");
        listUnregisteredPersons.setEmptyView(load);

        ver = Integer.valueOf(android.os.Build.VERSION.SDK);
        Log.d("Ver:",Integer.toString(ver));

        arrayAdapter = new PersonArrayAdapter(getActivity(), persons);
        arrayAdapter.setCallback(this);
        arrayAdapter.b = btnAddMobile;

        h.GetUnregisteredPersons(arrayAdapter);

        txtSearch_reg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Filter [" + s + "]");
                arrayAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lblRegCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.clear();
                h.GetUnregisteredPersons(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                listUnregisteredPersons.setAdapter(arrayAdapter);
            }
        });

        lblUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.clear();
                h.GetUnregisteredPersons(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
                listUnregisteredPersons.setAdapter(arrayAdapter);
            }
        });

        txtSearch_reg.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtSearch_reg, InputMethodManager.SHOW_IMPLICIT);

        btnAddMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedMobile = txtSearch_reg.getText().toString();
                listenNfc(SelectedMobile);
            }
        });

        arrayAdapter.sort(new Comparator<PersonObject>() {
            @Override
            public int compare(PersonObject item1, PersonObject item2) {
                return item1.name.toLowerCase().compareTo(item2.name.toLowerCase());
            }
        });

        arrayAdapter.notifyDataSetChanged();
        listUnregisteredPersons.setAdapter(arrayAdapter);

        return view;
    }

    private void enableReaderMode() {
        Log.i(TAG,"Enabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(activity, mCardReader, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        Activity activity = getActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }

    @Override
    public void onAccountReceived(final String tagID) {



        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HttpRequest h2 = new HttpRequest(getActivity());
                h2.RegisterNfc(SelectedMobile, tagID);
            }
        });

        disableReaderMode();
        progressdialog.dismiss();
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
    }


    @Override
    public void returnPerson(PersonObject p){
        SelectedMobile = p.mobile;
        listenNfc(p.name);
    }

    private void listenNfc(String s){
        if (ver >= 19) {
            mCardReader = new CardReader(this);
            enableReaderMode();
        }

        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Please tap card for " + s);
        progressdialog.show();

        txtSearch_reg.selectAll();
    }
}
