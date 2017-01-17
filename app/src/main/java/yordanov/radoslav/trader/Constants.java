package yordanov.radoslav.trader;

import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.Instrument;
import yordanov.radoslav.trader.models.User;

public class Constants {

    public static final String USER_ID_PREF = "USER_ID";
    public static final String REMEMBER_ME_PREF = "REMEMBER_ME";

    public static long CURRENT_USER_ID;

    public static long NO_USER = -1;

    public static final String FAVOURITE_INSTRUMENTS_TABLE = FavouriteInstruments.class.getSimpleName();
    public static final String INSTRUMENT_TABLE = Instrument.class.getSimpleName();
    public static final String USER_TABLE = User.class.getSimpleName();

    public static final String INSTRUMENTS_DATA = "instruments.json";

    public static final int UPDATE_INTERVAL = 3000;

    private Constants() {

    }
}