package org.aieseclchku.im.tap_in;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Alpha on 27/12/2014.
 */
public class RegistrantDetailArrayAdapter extends ArrayAdapter<RegistrantObject> {
    private final Context context;
    private ArrayList<RegistrantObject> Registrants;
    private ArrayList<RegistrantObject> filtered;
    private RegistrantFilter filter;
    private String CurrEventName = "";

    private HttpRequest h = new HttpRequest();

    public RegistrantDetailArrayAdapter(Context context, ArrayList<RegistrantObject> Registrants) {
        super(context, R.layout.layout_registrant_detail, Registrants);
        this.context = context;
        this.Registrants = Registrants;
        this.filtered = Registrants;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.layout_registrant_detail, parent, false);
        TextView lblRegistrantName = (TextView) v.findViewById(R.id.lblRegistrantName);
        final TextView lblRegistrantPhone = (TextView) v.findViewById(R.id.lblRegistrantPhone);
        TextView lblRegistrantUcode = (TextView) v.findViewById(R.id.lblRegistrantUcode);
        TextView lblRegistrantEmail = (TextView) v.findViewById(R.id.lblRegistrantEmail);
        final CheckBox chbAttendanceStatus = (CheckBox) v.findViewById(R.id.chbAttendanceStatus);

        final RegistrantObject registrant = filtered.get(position);
        Log.d("Registrant name:",registrant.name);

        lblRegistrantPhone.setText(registrant.mobile);
        lblRegistrantName.setText(registrant.name);
        lblRegistrantUcode.setText(registrant.ucode);
        lblRegistrantEmail.setText(registrant.email);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int counter = 0 ;

                String remark = "There is no remark.";
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(registrant.name + " (" + registrant.ucode + "" + registrant.department + ")" );
                for (int i = 0 ; i < 4 ; i++)
                    if (!registrant.remark[i].equals("")) {

                        if (counter == 0)
                            remark = "Remarks : ";

                        if (counter != 0)
                            remark+= " | " ;
                        remark += registrant.remark[i] ;
                        counter ++ ;
                    }
                alertDialog.setMessage( remark );
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


        chbAttendanceStatus.setChecked(registrant.attendance.equals("1"));

        chbAttendanceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Global global = ((Global) getContext().getApplicationContext());
                CurrEventName = global.getCurrEvent();

                if (isChecked) {
                    HttpRequest h2 = new HttpRequest(getContext());
                    h2.TakeAttendanceWithMobile(CurrEventName, registrant.mobile,registrant.remark,registrant.name);
                }
                else
                {
                    HttpRequest h2 = new HttpRequest(getContext());
                    h2.CancelAttendanceWithMobile(CurrEventName, registrant.mobile , registrant.remark,registrant.name);
                }
            }
        });

        lblRegistrantPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String phone_no = lblRegistrantPhone.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone_no));
                context.startActivity(callIntent);
            }
        });

        return v;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new RegistrantFilter();
        }
        return filter;
    }

    public class RegistrantFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0 ){
                results.values = Registrants;
                results.count = Registrants.size();
            }
            else {
                ArrayList<RegistrantObject> filtered = new ArrayList<>();
                ArrayList<RegistrantObject> unfiltered = new ArrayList<>();
                unfiltered.addAll(Registrants);

                switch (constraint.charAt(0)){
                    case '+':
                        for (RegistrantObject r : unfiltered) {
                            if (r.name.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.mobile.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.email.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.uid.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.ucode.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.remark[0].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.remark[1].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.remark[2].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                            else if (r.remark[3].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("1"))
                                filtered.add(r);
                        }
                        break;
                    case '-':
                        for (RegistrantObject r : unfiltered) {
                            if (r.name.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.mobile.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.email.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.uid.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.ucode.toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.remark[0].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.remark[1].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.remark[2].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                            else if (r.remark[3].toLowerCase().contains(constraint.toString().substring(1).toLowerCase()) && r.attendance.equals("0"))
                                filtered.add(r);
                        }
                        break;
                    default:
                        for (RegistrantObject r : unfiltered) {
                            if (r.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.mobile.toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.email.toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.uid.toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.ucode.toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.remark[0].toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.remark[1].toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.remark[2].toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                            else if (r.remark[3].toLowerCase().contains(constraint.toString().toLowerCase()))
                                filtered.add(r);
                        }
                }

                results.values = filtered;
                results.count = filtered.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
                Toast.makeText(getContext(),"NO RESULT", Toast.LENGTH_SHORT ).show();
            }
            else {
                filtered = (ArrayList<RegistrantObject>) results.values;
                notifyDataSetChanged();
            }

        }
    }

    @Override
    public int getCount()
    {
        return filtered.size();
    }

    @Override
    public RegistrantObject getItem (int pos){
        return filtered.get(pos);
    }

}
