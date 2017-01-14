package yordanov.radoslav.trader.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.adapters.AddInstrumentsAdapter;
import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.FavouriteInstruments_Table;
import yordanov.radoslav.trader.models.Instrument;
import yordanov.radoslav.trader.models.Instrument_Table;

public class AddInstrumentsActivity extends AppCompatActivity {
    private AddInstrumentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_instruments_activity);
        initListView();
    }

    @SuppressWarnings("unchecked")
    private void initListView() {
        final ListView listView = (ListView) findViewById(R.id.addInstrumentsListView);

    /*    SQLite.select()
                .from(Instrument.class)
                .where(Instrument_Table.id.is(
                        PropertyFactory.from(SQLite.select(FavouriteInstruments_Table.instrumentId_id)
                                .from(FavouriteInstruments.class)
                                .where(FavouriteInstruments_Table.userId_id.eq(Constants.CURRENT_USER_ID))
                        )))
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Instrument>() {
                    @Override
                    public void onQueryResult(QueryTransaction<Instrument> transaction, @NonNull CursorResult<Instrument> tResult) {
                        // called when query returns on UI thread
                        List<Instrument> instruments = tResult.toListClose();
                        mAdapter = new AddInstrumentsAdapter(AddInstrumentsActivity.this, new ArrayList<>(instruments));
                        listView.setAdapter(mAdapter);
                    }
                }).execute();*/


        StringQuery instrumentQuery = new StringQuery(
                Instrument.class,
                "SELECT " + Instrument_Table.name + ", " + Instrument_Table.id
                        + "FROM " + Constants.INSTRUMENT_TABLE + " WHERE" + Instrument_Table.id +
                        "NOT IN(" + "SELECT IFNULL(" + FavouriteInstruments_Table.instrumentId_id +
                        ", '') FROM " + Constants.FAVOURITE_INSTRUMENTS_TABLE + " WHERE " +
                        FavouriteInstruments_Table.userId_id +
                        " = " + Constants.CURRENT_USER_ID + ")"
        );

        instrumentQuery
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback() {
                    @Override
                    public void onQueryResult(QueryTransaction transaction,
                                              @NonNull CursorResult tResult) {
                        List<Instrument> instrumentsList = tResult.toList();
                        mAdapter = new AddInstrumentsAdapter(AddInstrumentsActivity.this,
                                new ArrayList<>(instrumentsList));
                        listView.setAdapter(mAdapter);
                    }
                })
                .execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_instruments_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            updateUserFavoriteInstruments();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateUserFavoriteInstruments() {
        ArrayList<Long> selectedInstrumentIDs = mAdapter.getSelectedInstruments();
        for (int i = 0; i < selectedInstrumentIDs.size(); i++) {
            FavouriteInstruments favouriteInstrument = new FavouriteInstruments();
            favouriteInstrument.setUserId(Constants.CURRENT_USER_ID);
            favouriteInstrument.setInstrumentId(selectedInstrumentIDs.get(i));
            favouriteInstrument.save();
        }
        Intent openAddInstruments = new Intent(AddInstrumentsActivity.this, InstrumentsActivity.class);
        openAddInstruments.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(openAddInstruments);
        finish();
    }
}