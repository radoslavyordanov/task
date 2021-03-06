package yordanov.radoslav.trader;

import android.app.Application;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import yordanov.radoslav.trader.models.Instrument;
import yordanov.radoslav.trader.models.User;

public class TraderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());

        createDB();
    }

    private void createDB() {
        insertUsers();
        insertInstruments();
    }

    private void insertUsers() {
        SQLite.select()
                .from(User.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<User>() {
                    @Override
                    public void onQueryResult(QueryTransaction<User> transaction,
                                              @NonNull CursorResult<User> tResult) {
                        // called when query returns on UI thread
                        List<User> users = tResult.toListClose();
                        if (users.isEmpty()) {
                            User user = new User();
                            user.setEmail("test@test.com");
                            user.setPassword("test");
                            User.insertUser(user);

                            User user1 = new User();
                            user1.setEmail("admin@admin.com");
                            user1.setPassword("admin");
                            User.insertUser(user1);
                        }
                    }
                }).execute();
    }

    private void insertInstruments() {
        SQLite.select()
                .from(Instrument.class)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<Instrument>() {
                    @Override
                    public void onQueryResult(QueryTransaction<Instrument> transaction,
                                              @NonNull CursorResult<Instrument> tResult) {
                        // called when query returns on UI thread
                        List<Instrument> instruments = tResult.toListClose();
                        if (instruments.isEmpty()) {
                            insertInstrumentsFromJSON();
                        }
                    }
                }).execute();
    }

    private void insertInstrumentsFromJSON() {
        try {
            // Get the JSON file from the assets
            JSONArray instrumentsArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < instrumentsArray.length(); i++) {
                JSONObject instrumentsObject = instrumentsArray.getJSONObject(i);

                // Create a new instrument object and insert it in the DB
                Instrument instrument = new Instrument();
                instrument.setName(instrumentsObject.getString("name"));
                instrument.setLowestPrice(instrumentsObject.getDouble("lowestPrice"));
                instrument.setHighestPrice(instrumentsObject.getDouble("highestPrice"));
                instrument.setDecimalNumbers(instrumentsObject.getInt("decimalNumbers"));
                Instrument.insertInstrument(instrument);
            }
        } catch (JSONException e) {
            // parse error
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open(Constants.INSTRUMENTS_DATA);
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
            if (bytesRead > 0) {
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            return null;
        }
        return json;
    }
}