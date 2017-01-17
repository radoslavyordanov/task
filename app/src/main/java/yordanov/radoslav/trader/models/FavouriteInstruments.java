package yordanov.radoslav.trader.models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.AsyncQuery;
import com.raizlabs.android.dbflow.structure.BaseModel;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.TraderDatabase;

@Table(database = TraderDatabase.class)
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

    public static void insertFavouriteInstrument(FavouriteInstruments favouriteInstrument) {
        favouriteInstrument.save();
    }

    public static AsyncQuery<FavouriteInstruments> deleteFavouriteInstrumentById(long id) {
        return SQLite.delete(FavouriteInstruments.class)
                .where(FavouriteInstruments_Table.userId_id
                        .eq(Constants.CURRENT_USER_ID))
                .and(FavouriteInstruments_Table.instrumentId_id.eq(id))
                .async();
    }
}