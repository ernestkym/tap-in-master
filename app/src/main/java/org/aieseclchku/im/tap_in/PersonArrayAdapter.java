package org.aieseclchku.im.tap_in;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonArrayAdapter extends ArrayAdapter<PersonObject> {

    private PersonAdapterCallback callback;

    private final Context context;
    private ArrayList<PersonObject> Persons;
    private ArrayList<PersonObject> filtered;
    private PersonFilter filter;
    public Button b = null;

    public int count = 0;

    public PersonArrayAdapter(Context context, ArrayList<PersonObject> Persons) {
        super(context, R.layout.layout_registrant_simple, Persons);
        this.context = context;
        this.Persons = Persons;
        this.filtered = Persons;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.layout_registrant_simple, parent, false);
        TextView lblPersonName = (TextView) v.findViewById(R.id.lblPersonName);
        TextView lblPersonUcode = (TextView) v.findViewById(R.id.lblPersonUCode);

        final PersonObject Person = filtered.get(position);

        lblPersonName.setText(Person.name);
        lblPersonUcode.setText(Person.ucode);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.returnPerson(Person);
            }
        });


        return v;
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new PersonFilter();
        }
        return filter;
    }

    public class PersonFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0 ){
                results.values = Persons;
                results.count = Persons.size();
            }
            else {
                ArrayList<PersonObject> filtered = new ArrayList<>();
                ArrayList<PersonObject> unfiltered = new ArrayList<>();
                unfiltered.addAll(Persons);

                for (PersonObject r : unfiltered) {
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
                    else if (r.department.toLowerCase().contains(constraint.toString().toLowerCase()))
                        filtered.add(r);
                    else if (r.role.toLowerCase().contains(constraint.toString().toLowerCase()))
                        filtered.add(r);
                    else if (r.NfcTag.toLowerCase().contains(constraint.toString().toLowerCase()))
                        filtered.add(r);

                }

                results.values = filtered;
                results.count = filtered.size();
                count = filtered.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                //notifyDataSetInvalidated();
                filtered = (ArrayList<PersonObject>) results.values;
                notifyDataSetChanged();
                if (b != null && constraint.length()==8){
                    b.setVisibility(View.VISIBLE);
                    b.setEnabled(true);
                }

            }
            else {
                filtered = (ArrayList<PersonObject>) results.values;
                notifyDataSetChanged();
                if (b != null) {
                    //b.setVisibility(View.INVISIBLE);
                    b.setEnabled(true);
                }
            }

        }
    }

    @Override
    public int getCount()
    {
        return filtered.size();
    }

    @Override
    public PersonObject getItem (int pos){
        return filtered.get(pos);
    }

    public void setCallback(PersonAdapterCallback callback){
        if(callback != null)
            this.callback = callback;
        else
            Log.i("", "callback null");
    }

    public interface PersonAdapterCallback {
        public void returnPerson(PersonObject p);
    }

}
