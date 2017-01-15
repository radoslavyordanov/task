package yordanov.radoslav.trader.adapters;

import android.content.Context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    R.layout.add_instruments_row,
                    null
            );
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            holder.checkBox.setOnCheckedChangeListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the data item for this position
        Instrument instrument = getItem(position);

        // Populate the data into the template view using the data object
        if (instrument != null) {
            holder.name.setText(instrument.getName());
            holder.checkBox.setTag(instrument.getId());
        }
        // Return the completed view to render on screen
        return view;
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
