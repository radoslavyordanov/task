package yordanov.radoslav.trader.utils;

import com.raizlabs.android.dbflow.sql.language.NameAlias;

public class DBFlowUtils {

    private DBFlowUtils() {

    }

    public static NameAlias getNameAliasForTable(String table) {
        return NameAlias.builder(table).build();
    }
}