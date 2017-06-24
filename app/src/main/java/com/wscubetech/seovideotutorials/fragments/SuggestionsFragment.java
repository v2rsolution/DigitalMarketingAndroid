package com.wscubetech.seovideotutorials.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.MyValidations;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 29/3/17.
 */

public class SuggestionsFragment extends Fragment implements View.OnClickListener {

    TextInputLayout inpName, inpMessage;
    EditText etName, etMessage;
    TextView txtSend;
    String title="";
    DialogMsg dialogMsg;
    Dialog progress;

    public static Fragment newInstance(String title){
        Fragment fragment=new SuggestionsFragment();
        Bundle bundle=new Bundle();
        bundle.putString("Title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_suggestions, container, false);
        v.setOnClickListener(this);
        init(v);
        return v;
    }

    private void init(View v) {
        inpName = (TextInputLayout) v.findViewById(R.id.inpName);
        inpMessage = (TextInputLayout) v.findViewById(R.id.inpMessage);
        etMessage = (EditText) v.findViewById(R.id.etMessage);
        etName = (EditText) v.findViewById(R.id.etName);
        txtSend=(TextView)v.findViewById(R.id.txtSend);
    }

    @Override
    public void onStart() {
        super.onStart();
        title=this.getArguments().getString("Title");
        HomeActivity.txtHeader.setText(title);
        dialogMsg=new DialogMsg(HomeActivity.activity);
        progress=new MyProgressDialog(HomeActivity.activity).getDialog();
        textChangeListeners();
        onClickListeners();

        UserModel userModel=new UserDetailsPrefs(getActivity()).getUserModel();
        if(!userModel.getUserId().equals("")){
            etName.setText(userModel.getUserName());
            etName.setSelection(userModel.getUserName().length());
        }
    }

    private void textChangeListeners(){
        ValidationsListeners listeners=new ValidationsListeners(HomeActivity.activity);
        etName.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpName));
        etMessage.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpMessage));
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    private void onClickListeners(){
        txtSend.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtSend:
                String strName,strMsg;
                strName=etName.getText().toString().trim();
                strMsg=etMessage.getText().toString().trim();

                MyValidations validations=new MyValidations(HomeActivity.activity);
                if(strName.length()<1 || !validations.checkName(strName)){
                    inputOperationError(inpName,etName,"Please enter a valid name");
                    return;
                }

                if(strMsg.length()<1){
                    inputOperationError(inpMessage,etMessage,"Please enter some message to send");
                    return;
                }

                if(new ConnectionDetector(getActivity()).isConnectingToInternet()){
                    okHttpSendSuggestion(strName,strMsg);
                }else{
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }

                break;
        }
    }

    private void okHttpSendSuggestion(String... params){
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel=new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("user_name",params[0]));
        arrayKeyValueModel.add(new KeyValueModel("suggestion_message",params[1]));
        OkHttpCalls calls=new OkHttpCalls(Urls.SEND_SUGGESTION,arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(true,"");
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response=res.body().string();
                handleResponse(false,response);
            }
        });
    }

    private void handleResponse(final boolean failed, String response){
        HomeActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress.isShowing())
                    progress.dismiss();
                if(failed){
                    dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                }else{
                    dialogMsg.showSuccessDialog("Thank You\nYour valuable suggestion has been successfully sent", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogMsg.getDialog().dismiss();
                            HomeActivity.activity.onBackPressed();
                        }
                    });
                }
            }
        });
    }
}
