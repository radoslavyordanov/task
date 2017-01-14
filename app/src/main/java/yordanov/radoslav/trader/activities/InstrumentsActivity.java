package yordanov.radoslav.trader.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.Utils;
import yordanov.radoslav.trader.adapters.InstrumentsAdapter;
import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.FavouriteInstruments_Table;
import yordanov.radoslav.trader.models.Instrument;
import yordanov.radoslav.trader.models.Instrument_Table;
import yordanov.radoslav.trader.models.User;
import yordanov.radoslav.trader.models.User_Table;

public class InstrumentsActivity extends AppCompatActivity {
    private InstrumentsAdapter mApdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruments_activity);

        initListView();
    }

    private void initListView() {
        final ListView listView = (ListView) findViewById(R.id.instrumentsListView);

        NameAlias instrumentAlias = Utils.getNameAliasForTable(Constants.INSTRUMENT_TABLE);
        NameAlias userAlias = Utils.getNameAliasForTable(Constants.USER_TABLE);

        SQLite.select(
                Instrument_Table.name.withTable(instrumentAlias),
                Instrument_Table.id.withTable(instrumentAlias)
        )
                .from(Instrument.class)
                .innerJoin(FavouriteInstruments.class)
                .on(Instrument_Table.id.withTable().eq(FavouriteInstruments_Table.instrumentId_id))
                .innerJoin(User.class)
                .on(User_Table.id.withTable().eq(FavouriteInstruments_Table.userId_id))
                .where(User_Table.id.withTable(userAlias).eq(Constants.CURRENT_USER_ID))
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Instrument>() {
                    @Override
                    public void onQueryResult(QueryTransaction<Instrument> transaction,
                                              @NonNull CursorResult<Instrument> tResult) {
                        // called when query returns on UI thread
                        List<Instrument> instruments = tResult.toListClose();
                        mApdater = new InstrumentsAdapter(InstrumentsActivity.this,
                                new ArrayList<>(instruments));
                        listView.setAdapter(mApdater);
                    }
                }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instruments_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addIntrument:
                Intent openAddInstruments = new Intent(InstrumentsActivity.this,
                        AddInstrumentsActivity.class);
                startActivity(openAddInstruments);
                return true;
            case R.id.logout:
                SharedPreferences appPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                appPreferences.edit().putLong(Constants.USER_ID_PREF, -1).apply();
                appPreferences.edit().putBoolean(Constants.REMEMBER_ME_PREF, false).apply();
                Intent openLoginActivity = new Intent(this, LoginActivity.class);
                startActivity(openLoginActivity);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
