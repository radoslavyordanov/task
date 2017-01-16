package yordanov.radoslav.trader;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TraderDatabase.NAME, version = TraderDatabase.VERSION)
public class TraderDatabase {

    public static final String NAME = "TraderDatabase"; // we will add the .db extension

    public static final int VERSION = 1;

    private TraderDatabase() {
        
    }
}

