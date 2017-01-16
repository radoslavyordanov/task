package yordanov.radoslav.trader.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.models.Instrument;

public class AddInstrumentsAdapter extends ArrayAdapter<Instrument> implements
        CompoundButton.OnCheckedChangeListener {
    private ArrayList<Long> mSelectedInstruments = new ArrayList<>();

    public AddInstrumentsAdapter(Context context, ArrayList<Instrument> instruments) {
        super(context, 0, instruments);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.row_add_instruments,
                    parent,
                    false
            );
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.checkBox.setOnCheckedChangeListener(this);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        Instrument instrument = getItem(position);

        // Populate the data into the template view using the data object
        if (instrument != null) {
            holder.name.setText(instrument.getName());
            holder.checkBox.setTag(instrument.getId());
        }
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mSelectedInstruments.add((Long) buttonView.getTag());
        } else {
            mSelectedInstruments.remove((Long) buttonView.getTag());
        }
    }

    public ArrayList<Long> getSelectedInstruments() {
        return mSelectedInstruments;
    }

    private static class ViewHolder {
        TextView name;
        CheckBox checkBox;
    }

}
