package com.example.dawidr.androidtestproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AccountSetupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        EditText etUserName = (EditText) findViewById(R.id.etUserName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Button btnContinue = (Button) findViewById(R.id.btnContinue);
                String strUserName = ((EditText) findViewById(R.id.etUserName)).getText().toString();
                String strEmail = ((EditText) findViewById(R.id.etEmail)).getText().toString();

                if (strUserName.length() != 0 && strEmail.length() != 0)
                    btnContinue.setEnabled(true);
                else
                    btnContinue.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etUserName.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
    }

    public void ContinueClick(View view) {

        String strUserName = ((EditText) findViewById(R.id.etUserName)).getText().toString();
        String strEmail = ((EditText) findViewById(R.id.etEmail)).getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.dawidr.androidtestproject", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", strUserName);
        editor.putString("email", strEmail);
        editor.commit();

        Intent intent = new Intent(AccountSetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
