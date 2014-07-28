package com.example.dawidr.androidtestproject;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class AccountSetupTest extends ActivityInstrumentationTestCase2<AccountSetupActivity> {

    private AccountSetupActivity accountSetupActivity;
    private EditText etUserName;
    private EditText etEmail;
    private Button btnContinue;

    public AccountSetupTest() {
        super(AccountSetupActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        accountSetupActivity = getActivity();

        etUserName = (EditText) accountSetupActivity.findViewById(R.id.etUserName);
        etEmail = (EditText) accountSetupActivity.findViewById(R.id.etEmail);
        btnContinue = (Button) accountSetupActivity.findViewById(R.id.btnContinue);
    }

    public void test_validate_two_fields_empty() {
        accountSetupActivity.runOnUiThread(new Runnable() {
            public void run() {

                etUserName.setText("");
                etEmail.setText("");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
    }

    public void test_validate_first_field_empty() {
        accountSetupActivity.runOnUiThread(new Runnable() {
            public void run() {

                etUserName.setText("");
                etEmail.setText("something");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
    }

    public void test_validate_second_field_empty() {
        accountSetupActivity.runOnUiThread(new Runnable() {
            public void run() {

                etUserName.setText("something");
                etEmail.setText("");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
    }

    public void test_validate_both_fields_filled() {
        accountSetupActivity.runOnUiThread(new Runnable() {
            public void run() {

                etUserName.setText("something");
                etEmail.setText("something");
                assertEquals(btnContinue.isEnabled(), true);
            }
        });
    }
}
