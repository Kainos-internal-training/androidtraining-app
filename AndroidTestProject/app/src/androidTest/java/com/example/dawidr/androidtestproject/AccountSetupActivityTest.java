package com.example.dawidr.androidtestproject;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

public class AccountSetupActivityTest extends ActivityInstrumentationTestCase2<AccountSetupActivity> {

    private AccountSetupActivity accountSetupActivity;
    private EditText etUserName;
    private EditText etEmail;
    private Button btnContinue;

    public AccountSetupActivityTest() {
        super(AccountSetupActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        accountSetupActivity = getActivity();

        etUserName = (EditText) accountSetupActivity.findViewById(R.id.AccountSetup_et_UserName);
        etEmail = (EditText) accountSetupActivity.findViewById(R.id.AccountSetup_et_Email);
        btnContinue = (Button) accountSetupActivity.findViewById(R.id.btnContinue);
    }

    public void test_validate_two_fields_empty() throws InterruptedException {

        BlockingOnUIRunnable actionRunnable = new BlockingOnUIRunnable(accountSetupActivity, new BlockingOnUIRunnableListener() {
            public void onRunOnUIThread() {
                etUserName.setText("");
                etEmail.setText("");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
        actionRunnable.startOnUiAndWait();
    }

    public void test_validate_first_field_empty() throws InterruptedException {

        BlockingOnUIRunnable actionRunnable = new BlockingOnUIRunnable(accountSetupActivity, new BlockingOnUIRunnableListener() {
            public void onRunOnUIThread() {
                etUserName.setText("");
                etEmail.setText("something");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
        actionRunnable.startOnUiAndWait();
    }

    public void test_validate_second_field_empty() throws InterruptedException {

        BlockingOnUIRunnable actionRunnable = new BlockingOnUIRunnable(accountSetupActivity, new BlockingOnUIRunnableListener() {
            public void onRunOnUIThread() {
                etUserName.setText("something");
                etEmail.setText("");
                assertEquals(btnContinue.isEnabled(), false);
            }
        });
        actionRunnable.startOnUiAndWait();
    }

    public void test_validate_both_fields_filled() throws InterruptedException {

        BlockingOnUIRunnable actionRunnable = new BlockingOnUIRunnable(accountSetupActivity, new BlockingOnUIRunnableListener() {
            public void onRunOnUIThread() {
                etUserName.setText("something");
                etEmail.setText("something");
                assertEquals(btnContinue.isEnabled(), true);
            }
        });
        actionRunnable.startOnUiAndWait();
    }
}
