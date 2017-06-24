package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by wscubetech on 12/10/15.
 */
public class MyValidations {

    Activity act;

    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(

            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z ]*$");

    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^[a-zA-Z0-9+_.]*${4,16}");

    private static final Pattern MobilePattern = Pattern
            .compile("^[0-9+]{7,13}$");

    private static final Pattern PIN_CODE = Pattern.compile("^[1-9][0-9]{5}$");

    public MyValidations(Activity act) {
        this.act = act;
    }

    public Boolean checkIsEmpty(EditText et, TextInputLayout input, String message) {
        if (et.getText().toString().trim().isEmpty()) {
            input.setError(message);
            requestFocus(input);
            return true;
        } else {
            input.setError(null);
            input.setErrorEnabled(false);
        }
        return false;
    }

    public Boolean checkIsEmpty2(EditText et, TextInputLayout input, String message) {
        if (et.getText().toString().isEmpty()) {
            input.setError(message);
            requestFocus(input);
            return true;
        } else {
            input.setError(null);
            input.setErrorEnabled(false);
        }
        return false;
    }

    public Boolean checkIsEmpty(AutoCompleteTextView auto, TextInputLayout input, String message) {
        if (auto.getText().toString().trim().isEmpty()) {
            input.setError(message);
            requestFocus(input);
            return true;
        } else {
            input.setError(null);
            input.setErrorEnabled(false);
        }
        return false;
    }

    public Boolean isInvalid(EditText et, TextInputLayout input, String message) {
        if (et.getText().toString().trim().isEmpty()) {
            input.setError(message);
            requestFocus(input);
            return true;
        } else {
            if (!isValidEmail(et.getText().toString().trim())) {
                input.setError(message);
                requestFocus(input);
                return true;
            } else {
                input.setError(null);
                input.setErrorEnabled(false);
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void requestFocus(View view) {
        if (view.requestFocus()) {
            act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean checkName(EditText et, TextInputLayout input, String message) {

        String strName = et.getText().toString().trim();

        if (strName.isEmpty()) {
            input.setError(message);
            requestFocus(input);
            return false;
        } else {
            if (!NAME_PATTERN.matcher(strName).matches()) {
                input.setError(message);
                requestFocus(input);
                return false;
            } else {
                input.setError(null);
                input.setErrorEnabled(false);
            }
        }
        return true;
    }

    public boolean checkPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean checkMobile(String mobile) {
        return MobilePattern.matcher(mobile).matches();
    }

    public boolean checkName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public boolean checkPinCode(String pinCode) {
        return PIN_CODE.matcher(pinCode).matches();
    }

}
