/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.MyValidations;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnquiryTrainingActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    EditText etName, etEmail, etPhone, etMessage;
    Spinner spinner;
    TextView txtSubmit,txtHeader;
    Dialog progress;
    DialogMsg dialogMsg;

    String arrayCourses[];
    int positionSelected=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_training);
        init();

        dialogMsg=new DialogMsg(this);
        progress=new MyProgressDialog(this).getDialog();

        toolbarOperation();
        txtSubmit.setOnClickListener(this);
        fillSpinnerWithCourses();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtHeader=(TextView)toolbar.findViewById(R.id.txtHeader);
        spinner=(Spinner)findViewById(R.id.spinner);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etPhone = (EditText) findViewById(R.id.etPhone);
        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
    }

    private void toolbarOperation(){
        setSupportActionBar(toolbar);
        txtHeader.setText("Enquiry");
        toolbar.setBackgroundResource(R.color.color_tile_6);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void fillSpinnerWithCourses(){
        positionSelected=getIntent().getExtras().getInt("SelectedIndex");
        arrayCourses=getResources().getStringArray(R.array.array_training_courses);

        ArrayAdapter adapter=new ArrayAdapter(this,R.layout.row_text_view,arrayCourses);
        spinner.setAdapter(adapter);
        spinner.setSelection(positionSelected);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                positionSelected=i;
                //Toast.makeText(EnquiryTrainingActivity.this,arrayCourses[positionSelected],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSubmit:
                String strName = "", strEmail = "", strPhone = "", strMessage = "";
                strName = etName.getText().toString().trim();
                strEmail = etEmail.getText().toString().trim();
                strPhone = etPhone.getText().toString().trim();
                strMessage = etMessage.getText().toString().trim();

                Toast toast = Toast.makeText(HomeActivity.activity, "", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);

                MyValidations validations = new MyValidations(HomeActivity.activity);
                if (strName.length() < 1 || !validations.checkName(strName)) {
                    toast.setText("Please enter a valid name");
                    toast.show();
                    return;
                }

                if (!validations.checkEmail(strEmail)) {
                    toast.setText("Please enter a valid e-mail");
                    toast.show();
                    return;
                }

                if (!validations.checkMobile(strPhone)) {
                    toast.setText("Please enter a valid phone number");
                    toast.show();
                    return;
                }

                if (strMessage.length() < 1) {
                    toast.setText("Please enter some Message");
                    toast.show();
                    return;
                }

                if(new ConnectionDetector(this).isConnectingToInternet()){
                    okHttpSendQuery(toast,strName,strEmail,strPhone,arrayCourses[positionSelected],strMessage);
                }else{
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }

                break;
        }
    }

    private void okHttpSendQuery(final Toast toast, String... str) {
        progress.show();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.SEND_QUERY_TRAINING_ENQUIRY).newBuilder();
        urlBuilder.addQueryParameter("name", str[0]);
        urlBuilder.addQueryParameter("email", str[1]);
        urlBuilder.addQueryParameter("phone", str[2]);
        urlBuilder.addQueryParameter("selecttraining",str[3]);
        urlBuilder.addQueryParameter("message", str[4]);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Failure", "" + e);
                HomeActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress.isShowing())
                            progress.dismiss();
                        toast.setText(getString(R.string.networkError));
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                HomeActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progress.isShowing())
                            progress.dismiss();
                        toast.setText("Your message is successfully sent, We will contact you soon");
                        toast.show();
                        etName.setText("");
                        etEmail.setText("");
                        etPhone.setText("");
                        etMessage.setText("");
                    }
                });
            }
        });
    }
}
