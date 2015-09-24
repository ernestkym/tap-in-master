package org.aieseclchku.im.tap_in;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.app.Fragment;
import android.view.Gravity;

import java.util.ArrayList;

/**
 * Created by Alpha on 31/12/2014.
 */
public class EventArrayAdapter extends ArrayAdapter<EventObject> {
    private final Context context;
    private final ArrayList<EventObject> Events;

    public EventArrayAdapter(Context context, ArrayList<EventObject> Events) {
        super(context, R.layout.layout_event_item, Events);
        this.context = context;
        this.Events = Events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.layout_event_item, parent, false);
        TextView name = (TextView) v.findViewById(R.id.lblEventName_item);

        name.setText(Events.get(position).name);

        String s = Events.get(position).name;
        System.out.println(s);

        return v;
    }

}
