package yordanov.radoslav.trader.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.AsyncQuery;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.raizlabs.android.dbflow.structure.BaseModel;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.TraderDatabase;
import yordanov.radoslav.trader.utils.DBFlowUtils;

@Table(database = TraderDatabase.class)
public class Instrument extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id; // package-private recommended, not required

    @Column
    private String name; // private with getters and setters

    @Column
    private double lowestPrice;

    @Column
    private double highestPrice;

    @Column
    private String currentPrice;

    @Column
    private int decimalNumbers;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers(int decimalNumbers) {
        this.decimalNumbers = decimalNumbers;
    }

    public static void insertInstrument(Instrument instrument) {
        instrument.save();
    }

    public static AsyncQuery<Instrument> getFavouriteInstruments() {
        NameAlias instrumentAlias = DBFlowUtils.getNameAliasForTable(Constants.INSTRUMENT_TABLE);
        NameAlias userAlias = DBFlowUtils.getNameAliasForTable(Constants.USER_TABLE);

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
                .async();
    }

    @SuppressWarnings("unchecked")
    public static AsyncQuery<Instrument> getMissingInstruments() {
        StringQuery instrumentQuery = new StringQuery(
                Instrument.class,
                "SELECT " + Instrument_Table.name + ", " + Instrument_Table.id
                        + "FROM " + Constants.INSTRUMENT_TABLE + " WHERE" + Instrument_Table.id +
                        "NOT IN(" + "SELECT IFNULL(" + FavouriteInstruments_Table.instrumentId_id +
                        ", '') FROM " + Constants.FAVOURITE_INSTRUMENTS_TABLE + " WHERE " +
                        FavouriteInstruments_Table.userId_id +
                        " = " + Constants.CURRENT_USER_ID + ")"
        );
        return instrumentQuery.async();
    }
}