package yordanov.radoslav.trader;

import yordanov.radoslav.trader.models.FavouriteInstruments;
import yordanov.radoslav.trader.models.Instrument;
import yordanov.radoslav.trader.models.User;

/**
 * Created by Radi on 1/14/2017.
 */

public class Constants {
    public static final String USER_ID_PREF = "USER_ID";
    public static final String REMEMBER_ME_PREF = "REMEMBER_ME";

    public static long CURRENT_USER_ID;

    public static final String FAVOURITE_INSTRUMENTS_TABLE = FavouriteInstruments.class.getSimpleName();
    public static final String INSTRUMENT_TABLE = Instrument.class.getSimpleName();
    public static final String USER_TABLE = User.class.getSimpleName();
}
