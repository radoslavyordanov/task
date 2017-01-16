package yordanov.radoslav.trader;

import com.raizlabs.android.dbflow.sql.language.NameAlias;

public class Utils {
    private Utils() {

    }

    public static NameAlias getNameAliasForTable(String table) {
        return NameAlias.builder(table).build();
    }
}
