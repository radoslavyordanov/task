package yordanov.radoslav.trader.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

import yordanov.radoslav.trader.Constants;
import yordanov.radoslav.trader.R;
import yordanov.radoslav.trader.models.User;
import yordanov.radoslav.trader.models.User_Table;
import yordanov.radoslav.trader.utils.SharedPreferencesUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private CheckBox mRememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        checkIfUserIsLogged();

        initViews();

        setTitle(getResources().getString(R.string.login));
    }

    private void checkIfUserIsLogged() {
        long userId = SharedPreferencesUtils.getLongData(
                this, Constants.USER_ID_PREF, Constants.NO_USER);
        boolean rememberMe = SharedPreferencesUtils.getBooleanData(
                this, Constants.REMEMBER_ME_PREF, false);
        if (userId != Constants.NO_USER && rememberMe) {
            Constants.CURRENT_USER_ID = userId;
            Intent intent = new Intent(LoginActivity.this, InstrumentsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initViews() {
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mRememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
    }

    @Override
    public void onClick(View v) {
        validateUser();
    }

    private void validateUser() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(User_Table.email.eq(email))
                .and(User_Table.password.eq(password));

        SQLite.select()
                .from(User.class)
                .where(conditionGroup)
                .async()
                .queryResultCallback(new QueryTransaction.QueryResultCallback<User>() {
                    @Override
                    public void onQueryResult(QueryTransaction<User> transaction,
                                              @NonNull CursorResult<User> tResult) {
                        // called when query returns on UI thread
                        List<User> users = tResult.toListClose();
                        if (users.size() == 1) {
                            Constants.CURRENT_USER_ID = users.get(0).getId();
                            if (mRememberMeCheckBox.isChecked()) {
                                SharedPreferencesUtils.setLongData(LoginActivity.this,
                                        Constants.USER_ID_PREF, Constants.CURRENT_USER_ID);
                                SharedPreferencesUtils.setBooleanData(LoginActivity.this,
                                        Constants.REMEMBER_ME_PREF, true);
                            }
                            Intent intent = new Intent(LoginActivity.this,
                                    InstrumentsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showInvalidCredentialsDialog();
                        }
                    }
                }).execute();
    }

    private void showInvalidCredentialsDialog() {
        Resources res = getResources();
        new AlertDialog.Builder(this)
                .setTitle(res.getString(R.string.error))
                .setMessage(res.getString(R.string.invalidCredentials))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}