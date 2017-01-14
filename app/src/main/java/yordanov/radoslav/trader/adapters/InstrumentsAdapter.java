package yordanov.radoslav.trader.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.FavouriteInstruments_Table;
import yordanov.radoslav.trader.models.Instrument;

public class InstrumentsAdapter extends ArrayAdapter<Instrument> implements View.OnClickListener {

    public InstrumentsAdapter(Context context, ArrayList<Instrument> instruments) {
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
                    R.layout.instruments_row,
                    null
            );
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.delete = (ImageView) view.findViewById(R.id.delete);
            holder.delete.setOnClickListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the data item for this position
        Instrument instrument = getItem(position);

        // Populate the data into the template view using the data object
        holder.name.setText(instrument.getName());
        holder.price.setText(String.valueOf(instrument.getHighestPrice()));
        holder.delete.setTag(R.id.instrumentIdTag, instrument.getId());
        holder.delete.setTag(R.id.positionTag, position);

        // Return the completed view to render on screen
        return view;
    }

    @Override
    public void onClick(View v) {
        long instrumentId = (long) v.getTag(R.id.instrumentIdTag);
        int position = (int) v.getTag(R.id.positionTag);
        showDeleteDialog(instrumentId, position);
    }

    private void showDeleteDialog(final long id, final int position) {
        Resources res = getContext().getResources();
        new AlertDialog.Builder(getContext())
                .setTitle(res.getString(R.string.removeInstrumentTitle))
                .setMessage(res.getString(R.string.removeInstrumentDesc))
                .setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete using query
                        SQLite.delete(FavouriteInstruments.class)
                                .where(FavouriteInstruments_Table.userId_id.eq(Constants.CURRENT_USER_ID))
                                .and(FavouriteInstruments_Table.instrumentId_id.eq(id))
                                .async()
                                .queryResultCallback(new QueryTransaction.QueryResultCallback<FavouriteInstruments>() {
                                    @Override
                                    public void onQueryResult(QueryTransaction<FavouriteInstruments> transaction, @NonNull CursorResult<FavouriteInstruments> tResult) {
                                        remove(getItem(position));
                                        notifyDataSetInvalidated();
                                    }
                                })
                                .execute();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(res.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static class ViewHolder {
        TextView name;
        TextView price;
        ImageView delete;
    }

}
