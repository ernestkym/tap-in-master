package org.aieseclchku.im.tap_in;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Comparator;
import java.util.logging.Handler;

/**
 * Created by Alpha on 23/01/2015.
 */
public class HttpRequest {
    private int LengthOfResult;
    private String theUrl;
    private ArrayAdapter arrayAdapter;
    public String entrance = "http://aiesechku.org/Click-in/php/index.php?";
    private String[ ] dummy ={"","","",""};
    private Context c ;

    HttpRequest(Context context){
        c = context ;
    }

    HttpRequest(){

    }
    public void RegisterNfc(String mobile, String tagID){
        theUrl = entrance +"opt=registerNfcTag&Mobile=" + mobile + "&NfcTag=" + tagID;
        makeGetRequestSearch("none",dummy,true,"","no action");
    }
    public void TakeAttendanceWithTagID(String EventName, String tagID,String[] remark,boolean found,String name){
        theUrl = entrance +"opt=takeAttn&EventName=" + EventName + "&NfcTag=" + tagID + "&University=HKU";
        makeGetRequestSearch("none",remark,found,name,"Take Attendance");
    }
    public void TakeAttendanceWithMobile(String EventName, String mobile,String[] remark,String name){
        theUrl = entrance +"opt=takeAttn&EventName=" + EventName + "&Mobile=" + mobile + "&University=HKU";
        makeGetRequestSearch("none",remark,true,name,"Take Attendance");
    }
    public void CancelAttendanceWithMobile(String EventName, String mobile,String[] remark,String name){
        theUrl = entrance +"opt=cancelAttn&EventName=" + EventName + "&Mobile=" + mobile + "&University=HKU";
        makeGetRequestSearch("none",remark,true,name,"Cancel Attendance");
    }
    public void GetEventRegistrants(String EventName, ArrayAdapter arrayAdapter){
        this.arrayAdapter = arrayAdapter;
        theUrl = entrance +"opt=eventDetails&EventName=" + EventName;
        makeGetRequestSearch("Registrant",dummy,true,"","no action");
    }
    /**public void GetEventRegistrant(String EventName, String type, String content){
        theUrl = entrance +"opt=eventDetails&EventName=" + EventName + "&" + type + "=" + content;
        makeGetRequestSearch("Registrant",dummy,true,"","no action");
    }*/
    public void GetUnregisteredPersons(ArrayAdapter arrayAdapter){
        this.arrayAdapter = arrayAdapter;
        theUrl = entrance +"opt=unregisteredNfcList";
        makeGetRequestSearch("Person",dummy,true,"","no action");
    }
    public void makeGetRequestSearch(String type,String[] remark,boolean found, final String name,String a) {
        // Building GET request Url with Member object passed
        final String t = type;
        final String[] remarks = remark ;
        final boolean f = found ;
        final String n = name ;
        final String action = a;
        Log.d("EncodedUrl: ", theUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(theUrl, new JsonHttpResponseHandler() {
            public void onStart(String theUrl) {
                // called before request is started
                Log.d("[GET] ", theUrl);
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject res){

                switch(t){
                    case "Registrant":
                        StoreRegistrantResult(res); break;
                    case "Person":
                        StorePersonResult(res); break;
                    case "none":
                        String result = null;
                        try {
                            result = res.getInt("Status")==1?"Successful":"Unsuccessful";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (result.equals("Unsuccessful"))
                            showResult(remarks, 0,"",action);
                        else {
                            if (f == true)
                                showResult(remarks, 1,n,action);
                            else
                                showResult(remarks, -1,n,action);

                        }


                    default:
                        Log.d("[GET 200] JSONObject: ", res.toString()); break;
                }
            }

            public void onSuccess(int statusCode, Header[] headers, JSONArray res) {

                LengthOfResult = res.length();
                switch(t){
                    case "Registrant":
                        StoreRegistrantResults(res); break;
                    case "Person":
                        StorePersonResults(res); break;

                    default:
                        Log.d("[GET 200] JSONArray: ", res.toString()); break;
                }
            }
            public void onFailure(int statusCode, Header[] headers, JSONObject errorResponse, Throwable e) { }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void showResult(String[] remarks,int s,String name,String action){

        String remarkHere ="Status : Successful \n"+"Name : "+ name + "\nThere is no remark.";

        int counter = 0 ;

        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle(action);
        if (s == 1) {
            for (int i = 0; i < 4; i++)
                if (!remarks[i].equals("")) {

                    if (counter == 0)
                        remarkHere = "Status : Successful \n"+"Name : "+ name + "\nRemarks : ";

                    if (counter != 0)
                        remarkHere += " | ";
                    remarkHere += remarks[i];
                    counter++;
                }
            alertDialog.setMessage(remarkHere);
        }
        else
        if (s == 0)
            alertDialog.setMessage("Status : Unsuccessful \nYou haven't registered your UID card.");
        else
            alertDialog.setMessage("Status : Unsuccessful \nYou are not registered in this event.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }






    //Store Result
    public void StoreRegistrantResult(JSONObject json) {

        try {
            RegistrantObject m = new RegistrantObject() ;
            m.uid = json.has("Uid") ? json.getString("Uid") : "";
            m.email = json.has("Email") ? json.getString("Email") : "";
            m.name = json.has("Name") ? json.getString("Name") : "";
            m.mobile = json.has("Mobile") ? json.getString("Mobile") : "";
            m.attendance = json.has("Attendance") ? json.getString("Attendance") : "";
            m.ucode = json.has("University") ? json.getString("University") : "" ;
            m.department = json.has("Department") ? json.getString("Department") : "" ;
            m.remark[0] = json.has("Remark1") && json.getString("Remark1") != "null" ? json.getString("Remark1") : "" ;
            m.remark[1] = json.has("Remark2") && json.getString("Remark2") != "null" ? json.getString("Remark2") : "" ;
            m.remark[2] = json.has("Remark3") && json.getString("Remark3") != "null" ? json.getString("Remark3") : "" ;
            m.remark[3] = json.has("Remark4") && json.getString("Remark4") != "null" ? json.getString("Remark4") : "" ;
            m.NfcTag = json.has("NfcTag") && json.getString("NfcTag") != "null" ? json.getString("NfcTag") : "";

        }catch(JSONException e){
            e.printStackTrace();
            Log.d("[Update Table] JSONException", e.toString());
        }
    }
    public void StoreRegistrantResults(JSONArray res) {

        for (int i = 0; i < LengthOfResult; i++) {
            try {
                RegistrantObject m = new RegistrantObject() ;
                JSONObject json = res.getJSONObject(i);
                m.uid = json.has("Uid") ? json.getString("Uid") : "";
                m.email = json.has("Email") ? json.getString("Email") : "";
                m.name = json.has("Name") ? json.getString("Name") : "";
                m.mobile = json.has("Mobile") ? json.getString("Mobile") : "";
                m.attendance = json.has("Attendance") ? json.getString("Attendance") : "";
                m.ucode = json.has("University") ? json.getString("University") : "" ;
                m.department = json.has("Department") ? json.getString("Department") : "" ;
                m.remark[0] = json.has("Remark1") && json.getString("Remark1") != "null" ? json.getString("Remark1") : "" ;
                m.remark[1] = json.has("Remark2") && json.getString("Remark2") != "null" ? json.getString("Remark2") : "" ;
                m.remark[2] = json.has("Remark3") && json.getString("Remark3") != "null" ? json.getString("Remark3") : "" ;
                m.remark[3] = json.has("Remark4") && json.getString("Remark4") != "null" ? json.getString("Remark4") : "" ;
                m.NfcTag = json.has("NfcTag") && json.getString("NfcTag") != "null" ? json.getString("NfcTag") : "";

                arrayAdapter.insert(m, 0);
                arrayAdapter.sort(new Comparator<RegistrantObject>() {
                    @Override
                    public int compare(RegistrantObject item1, RegistrantObject item2) {
                        return item1.name.toLowerCase().compareTo(item2.name.toLowerCase());
                    }
                });

                arrayAdapter.notifyDataSetChanged();

            }catch(JSONException e){
                e.printStackTrace();
                Log.d("[Update Table] JSONException", e.toString());
            }
        }
    }

    public void StorePersonResult(JSONObject json) {

        try {
            PersonObject p = new PersonObject();
            p.uid = "";
            p.email = "";
            p.name = json.has("Name") ? json.getString("Name") : "";
            p.mobile = json.has("Mobile") ? json.getString("Mobile") : "";
            p.ucode = json.has("University") ? json.getString("University") : "";

        }catch(JSONException e){
            e.printStackTrace();
            Log.d("[Update Table] JSONException",e.toString());
        }

    }
    public void StorePersonResults(JSONArray res) {

        for (int i = 0; i < LengthOfResult; i++) {
            try {
                PersonObject m = new PersonObject() ;
                JSONObject json = res.getJSONObject(i);
                m.uid = "";
                m.email = "";
                m.name = json.has("Name") ? json.getString("Name") : "";
                m.mobile = json.has("Mobile") ? json.getString("Mobile") : "";
                m.ucode = json.has("University") ? json.getString("University") : "";

                arrayAdapter.insert(m, 0);
                arrayAdapter.notifyDataSetChanged();

            }catch(JSONException e){
                e.printStackTrace();
                Log.d("[Update Table] JSONException",e.toString());
            }
        }
    }
}
