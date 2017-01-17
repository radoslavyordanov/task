package yordanov.radoslav.trader.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.adapters.AddInstrumentsAdapter;
import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.Instrument;

public class AddInstrumentsActivity extends AppCompatActivity {

    private AddInstrumentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_instruments);

        initListView();

        setTitle(getString(R.string.addInstruments));
    }

    private void initListView() {
        final ListView listView = (ListView) findViewById(R.id.addInstrumentsListView);

        Instrument.getMissingInstruments().queryListResultCallback(
                new QueryTransaction.QueryResultListCallback<Instrument>() {
                    @Override
                    public void onListQueryResult(
                            QueryTransaction transaction, @NonNull List<Instrument> tResult) {
                        mAdapter = new AddInstrumentsAdapter(AddInstrumentsActivity.this,
                                new ArrayList<>(tResult));
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
            FavouriteInstruments.insertFavouriteInstrument(favouriteInstrument);
        }

        if (selectedInstrumentIDs.isEmpty()) {
            onBackPressed();
        } else {
            Intent openAddInstruments = new Intent(AddInstrumentsActivity.this,
                    InstrumentsActivity.class);
            openAddInstruments.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openAddInstruments);
            finish();
        }
    }
}