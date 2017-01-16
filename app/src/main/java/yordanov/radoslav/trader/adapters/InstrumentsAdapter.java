package yordanov.radoslav.trader.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
    private ArrayList<Instrument> mItems = new ArrayList<>();

    public InstrumentsAdapter(Context context, ArrayList<Instrument> instruments) {
        super(context, 0, instruments);
        mItems = instruments;
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
                    R.layout.row_instruments,
                    parent,
                    false
            );
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.delete.setOnClickListener(this);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        Instrument instrument = getItem(position);

        // Populate the data into the template view using the data object
        if (instrument != null) {
            holder.name.setText(instrument.getName());

            updatePriceBackground(holder.price, instrument.getCurrentPrice());

            holder.delete.setTag(R.id.instrumentIdTag, instrument.getId());
            holder.delete.setTag(R.id.positionTag, position);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    private void updatePriceBackground(TextView priceTV, String currentPriceStr) {
        double oldPrice, currentPrice;
        try {
            oldPrice = Double.parseDouble(priceTV.getText().toString());
            currentPrice = Double.parseDouble(currentPriceStr);
        } catch (NumberFormatException e) {
            return;
        }

        if (oldPrice == 0) {
            priceTV.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.rounded_bg_grey));
        } else if (currentPrice > oldPrice) {
            priceTV.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.rounded_bg_green));
        } else if (currentPrice < oldPrice) {
            priceTV.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.rounded_bg_red));
        } else {
            priceTV.setBackground(ContextCompat.getDrawable(getContext(),
                    R.drawable.rounded_bg_grey));
        }
        priceTV.setText(currentPriceStr);
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
                .setTitle(res.getString(R.string.confirmation))
                .setMessage(res.getString(R.string.removeInstrumentDesc))
                .setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onDeleteClick(dialog, id, position);
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

    private void onDeleteClick(DialogInterface dialog, long id, final int position) {
        // Delete using query
        SQLite.delete(FavouriteInstruments.class)
                .where(FavouriteInstruments_Table.userId_id
                        .eq(Constants.CURRENT_USER_ID))
                .and(FavouriteInstruments_Table.instrumentId_id.eq(id))
                .async()
                .queryResultCallback(
                        new QueryTransaction.QueryResultCallback<FavouriteInstruments>() {
                            @Override
                            public void onQueryResult(
                                    QueryTransaction<FavouriteInstruments> transaction,
                                    @NonNull CursorResult<FavouriteInstruments> tResult) {
                                remove(getItem(position));
                                notifyDataSetChanged();
                            }
                        })
                .execute();
        dialog.dismiss();
    }

    public ArrayList<Instrument> getItems() {
        return mItems;
    }

    private static class ViewHolder {
        TextView name;
        TextView price;
        ImageView delete;
    }

}
