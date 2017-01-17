package yordanov.radoslav.trader.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.queriable.AsyncQuery;
import com.raizlabs.android.dbflow.structure.BaseModel;

import yordanov.radoslav.trader.TraderDatabase;

@Table(database = TraderDatabase.class)
public class User extends BaseModel {

    @PrimaryKey(autoincrement = true)
    long id; // package-private recommended, not required

    @Column
    @Unique
    private String email; // private with getters and setters

    @Column
    private String password; // private with getters and setters

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void insertUser(User user) {
        user.save();
    }

    public static AsyncQuery<User> getUser(String email, String password) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(User_Table.email.eq(email))
                .and(User_Table.password.eq(password));

        return SQLite.select()
                .from(User.class)
                .where(conditionGroup)
                .async();
    }
}