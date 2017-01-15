package yordanov.radoslav.trader.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Collections;
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
    private RepeatingThread mRepeatingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruments_activity);

        initListView();
    }

    private void initListView() {
        final ListView listView = (ListView) findViewById(R.id.instrumentsListView);

        List<Instrument> favouriteInstruments = getFavouriteInstruments();
        mApdater = new InstrumentsAdapter(InstrumentsActivity.this,
                new ArrayList<>(favouriteInstruments));
        listView.setAdapter(mApdater);
        if (favouriteInstruments.size() > 1) {
            mRepeatingThread = new RepeatingThread();
            Thread t = new Thread(new RepeatingThread());
            t.start();
        }

    }

    private List<Instrument> getFavouriteInstruments() {
        NameAlias instrumentAlias = Utils.getNameAliasForTable(Constants.INSTRUMENT_TABLE);
        NameAlias userAlias = Utils.getNameAliasForTable(Constants.USER_TABLE);

        return SQLite.select(
                Instrument_Table.name.withTable(instrumentAlias),
                Instrument_Table.id.withTable(instrumentAlias),
                Instrument_Table.lowestPrice.withTable(instrumentAlias),
                Instrument_Table.highestPrice.withTable(instrumentAlias),
                Instrument_Table.decimalNumbers.withTable(instrumentAlias)
        )
                .from(Instrument.class)
                .innerJoin(FavouriteInstruments.class)
                .on(Instrument_Table.id.withTable().eq(FavouriteInstruments_Table.instrumentId_id))
                .innerJoin(User.class)
                .on(User_Table.id.withTable().eq(FavouriteInstruments_Table.userId_id))
                .where(User_Table.id.withTable(userAlias).eq(Constants.CURRENT_USER_ID))
                .queryList();
    }

    /**
     * Here is the logic for updating the prices.
     * First we get a random number between 1 and the maximum user's
     * favourite instruments. We use this number later in the for loop.
     * After that we create a range list of integers between 0 and the
     * maximum user's favourite instruments. We shuffle the list of integers.
     * Then we iterate through the instruments list.
     * Every iteration of the for loop gets a random number from the
     * list of integers. The maximum iteration of the instruments is the random
     * number that we generated in the beginning. This way we only randomize
     * subset of the user instruments.
     */
    private void updatePrices() {
        ArrayList<Instrument> items = mApdater.getItems();

        int subsetOfInstruments = getSubsetOfInstruments(items.size());

        ArrayList<Integer> listOfIntegers = generateListOfIntegers(items.size());
        Collections.shuffle(listOfIntegers);

        for (int i = 0; i < subsetOfInstruments; i++) {
            int randomInstrumentPosition = listOfIntegers.get(i);
            generateRandomPrice(items.get(randomInstrumentPosition));
        }
        mApdater.notifyDataSetChanged();
    }

    private int getSubsetOfInstruments(int maximum) {
        int randomNum;
        int minimum = 1;

        int range = (maximum - minimum) + 1;
        randomNum = (int) (Math.random() * range) + minimum;

        return randomNum;
    }

    private ArrayList<Integer> generateListOfIntegers(int max) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            list.add(i);
        }

        return list;
    }

    private void generateRandomPrice(Instrument instrument) {
        double minimum = instrument.getLowestPrice();
        double maximum = instrument.getHighestPrice();

        double randomPrice = (Math.random() * maximum) + minimum;
        int decimalNumbers = instrument.getDecimalNumbers();

        instrument.setCurrentPrice(priceFormatter(decimalNumbers, randomPrice));
    }

    private String priceFormatter(int decimalNumbers, double randomPrice) {
        return String.format("%." + decimalNumbers + "f", randomPrice);
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

    private class RepeatingThread implements Runnable {

        private Handler mHandler = new Handler();

        RepeatingThread() {
        }

        void stopHandler() {
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            updatePrices();
            mHandler.postDelayed(this, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRepeatingThread != null)
            mRepeatingThread.stopHandler();
    }

}
