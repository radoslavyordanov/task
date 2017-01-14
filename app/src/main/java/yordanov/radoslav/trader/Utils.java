package yordanov.radoslav.trader;

import com.raizlabs.android.dbflow.sql.language.NameAlias;

/**
 * Created by Radi on 1/14/2017.
 */

public class Utils {
    public static NameAlias getNameAliasForTable(String table) {
        return NameAlias.builder(table).build();
    }
}
