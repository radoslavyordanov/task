package yordanov.radoslav.trader.models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import yordanov.radoslav.trader.AppDatabase;

/**
 * Created by Radi on 1/14/2017.
 */

@Table(database = AppDatabase.class)
public class FavouriteInstruments extends BaseModel {
    @PrimaryKey
    @ForeignKey(tableClass = User.class)
    long userId;

    @PrimaryKey
    @ForeignKey(tableClass = Instrument.class)
    long instrumentId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(long instrumentId) {
        this.instrumentId = instrumentId;
    }
}
