package com.wscubetech.seovideotutorials.utils;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.wscubetech.seovideotutorials.R;


/**
 * Created by wscubetech on 16/6/16.
 */
public class ValidationsListeners {

    Activity activity;
    MyValidations validations;

    public ValidationsListeners(Activity activity) {
        this.activity = activity;
        validations = new MyValidations(activity);
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    public void inputOperationNoError(TextInputLayout inp) {
        inp.setError(null);
        inp.setErrorEnabled(false);
    }

    public class MyTextWatcherInputLayout implements TextWatcher {

        TextInputLayout inpLayout;

        public MyTextWatcherInputLayout(TextInputLayout inpLayout) {
            this.inpLayout = inpLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String input = editable.toString().trim();
            switch (inpLayout.getId()) {
                case R.id.inpName:
                    if (validations.checkName(input)) {
                        inputOperationNoError(inpLayout);
                    }
                    break;
                case R.id.inpEmail:
                    if (validations.checkEmail(input))
                        inputOperationNoError(inpLayout);
                    break;

                case R.id.inpPassword:
                    if (input.length() > 0) {
                        inputOperationNoError(inpLayout);
                    }
                    break;
                case R.id.inpConfirmPassword:
                    EditText etPass = (EditText) activity.findViewById(R.id.etPassword);
                    String strPass = etPass.getText().toString().trim();
                    if (strPass.equals(input)) {
                        inputOperationNoError(inpLayout);
                    }
                case R.id.inpMessage:
                    if (input.length() > 0) {
                        inputOperationNoError(inpLayout);
                    }
                    break;

                case R.id.inpQuestion:
                case R.id.inpTag:
                case R.id.inpAnswer:
                case R.id.inpOldPass:
                    if (input.length() > 0) {
                        inputOperationNoError(inpLayout);
                    }
                    break;

            }
        }
    }

}
