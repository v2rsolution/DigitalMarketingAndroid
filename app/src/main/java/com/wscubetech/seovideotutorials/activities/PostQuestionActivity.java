/*Designed and Developed by V2R Solution*/

package com.wscubetech.seovideotutorials.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.dpizarro.autolabel.library.AutoLabelUISettings;
import com.dpizarro.autolabel.library.Label;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.Constants;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;
import com.wscubetech.seovideotutorials.utils.ValidationsListeners;
import com.wscubetech.seovideotutorials.utils.ViewQuesTagsFromServer;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    TextView txtHeader;

    TextView txtUserName;
    ImageView imgUser;

    TextInputLayout inpQuestion, inpTag;
    EditText etQuestion;
    AutoCompleteTextView autoTag;

    AutoLabelUI tagView;
    AutoLabelUISettings labelUISettings;

    TextView txtAddTag, txtPost;

    UserModel userModel;

    DialogMsg dialogMsg;
    Dialog progress;

    String tagArray[];

    QuestionListModel quesModel;

    boolean finishAnswerList=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        init();
        userModel = new UserDetailsPrefs(this).getUserModel();
        dialogMsg = new DialogMsg(this);
        progress = new MyProgressDialog(this).getDialog();

        toolbarOperation();
        getSetQuesAndUserDetails();
        textChangeListeners();
        onClickListeners();
        autoCompleteTagListener();

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            okHttpGetTags();
        } else {
            Toast.makeText(this, "Couldn't get tags due to lack of connection", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        txtHeader = (TextView) toolbar.findViewById(R.id.txtHeader);

        inpQuestion = (TextInputLayout) findViewById(R.id.inpQuestion);
        inpTag = (TextInputLayout) findViewById(R.id.inpTag);
        etQuestion = (EditText) findViewById(R.id.etQuestion);

        autoTag = (AutoCompleteTextView) findViewById(R.id.autoTag);
        autoTag.setThreshold(1);

        txtAddTag = (TextView) findViewById(R.id.txtAddTag);
        txtPost = (TextView) findViewById(R.id.txtPost);

        tagView = (AutoLabelUI) findViewById(R.id.tagView);

        /*labelUISettings = new AutoLabelUISettings.Builder()
                .withMaxLabels(5)
                .withIconCross(R.drawable.cross)
                .withBackgroundResource(R.drawable.btn_primary_question_answer)
                .withLabelsClickables(false)
                .withShowCross(true)
                .withIconCross(R.drawable.ic_tag_cross)
                .withTextColor(android.R.color.white)
                .withTextSize(R.dimen.font_size_14)
                .withLabelPadding(R.dimen.dim_7)
                .build();*/

        //tagView.setSettings(labelUISettings);


    }

    private void toolbarOperation() {
        setSupportActionBar(toolbar);
        txtHeader.setText("Post a Question");
        toolbar.setBackgroundResource(R.color.color_tile_4);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void textChangeListeners() {
        ValidationsListeners listeners = new ValidationsListeners(this);
        etQuestion.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpQuestion));
        autoTag.addTextChangedListener(listeners.new MyTextWatcherInputLayout(inpTag));
    }

    private void onClickListeners() {
        txtAddTag.setOnClickListener(this);
        txtPost.setOnClickListener(this);
    }

    private void getSetQuesAndUserDetails() {
        quesModel = (QuestionListModel) getIntent().getExtras().getSerializable("QuesModel");
        if (quesModel.getQuesId().trim().length() > 0) {
            etQuestion.setText(quesModel.getQuesTitle().trim());
            etQuestion.setSelection(quesModel.getQuesTitle().trim().length());

            String strTags[] = quesModel.getTags().split(",");
            for (String strTag : strTags) {
                tagView.addLabel(strTag);
            }

            txtPost.setText("Update");
        } else {
            txtPost.setText("Post");
        }
        txtUserName.setText(userModel.getUserName());
        if (userModel.getUserImage().trim().length() > 1)
            new LoadUserImage().loadImageInImageView(this, Urls.imageUrl + userModel.getUserImage(), imgUser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtAddTag:
                String strTag = autoTag.getText().toString().trim();
                if (strTag.length() < 1) {
                    inputOperationError(inpTag, autoTag, "Please enter some tag to add");
                    return;
                }

                tagPicking(strTag);


                break;
            case R.id.txtPost:
                String strQues = etQuestion.getText().toString().trim();
                if (strQues.length() < 1) {
                    inputOperationError(inpQuestion, etQuestion, "Please enter a question to post");
                    return;
                }
                List<Label> labels = tagView.getLabels();
                if (labels.size() < 1) {
                    Toast.makeText(this, "Please insert at least one tag", Toast.LENGTH_LONG).show();
                    return;
                }

                String tagsComma = "";
                for (Label label : labels) {
                    tagsComma += label.getText() + ",";
                }
                tagsComma = tagsComma.substring(0, tagsComma.length() - 1);
                Log.v("TagsComma", tagsComma);

                quesModel.setQuesTitle(strQues);
                quesModel.setTags(tagsComma);
                if (new ConnectionDetector(this).isConnectingToInternet()) {
                    okHttpPostQuestion(strQues, tagsComma);
                } else {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.connectionError));
                }

                break;
        }
    }

    public void inputOperationError(TextInputLayout inp, EditText et, String msg) {
        inp.setErrorEnabled(true);
        inp.setError(msg);
        et.requestFocus();
    }

    private void tagPicking(String strTag) {
        boolean found = false;
        List<Label> arrayLabels = tagView.getLabels();
        for (Label tag : arrayLabels) {
            if (tag.getText().trim().equalsIgnoreCase(strTag)) {
                found = true;
                break;
            }
        }

        if (found) {
            Toast.makeText(this, "Tag already picked", Toast.LENGTH_SHORT).show();
        } else {
            if (arrayLabels.size() >= 6) {
                Toast.makeText(this, "You cannot pick more than 6 tags for a question", Toast.LENGTH_SHORT).show();
            } else {
                tagView.addLabel(strTag.toLowerCase());
                autoTag.setText("");
            }
        }
    }

    private void okHttpGetTags() {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("tag_main_cat_id", Constants.SEO_CAT_ID));
        OkHttpCalls calls = new OkHttpCalls(Urls.VIEW_TAGS, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseViewTags(true, "");
            }

            @Override
            public void onResponse(Call call, Response res) throws IOException {
                String response = Html.fromHtml(res.body().string()).toString();
                handleResponseViewTags(false, response);
            }
        });
    }

    private void handleResponseViewTags(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                if (failed) {
                    Toast.makeText(PostQuestionActivity.this, getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Log.v("ResponseTags", response);
                        JSONObject json = new JSONObject(response);
                        if (json.getInt("response") == 1) {
                            String message = json.getString("message").trim();
                            if (message.length() > 1) {
                                tagArray = message.toLowerCase().split(",");
                                ArrayAdapter adapter = new ArrayAdapter(PostQuestionActivity.this, R.layout.row_auto_complete_tag, tagArray);
                                autoTag.setAdapter(adapter);
                            }
                        }
                    } catch (Exception e) {
                        Log.v("TagParsing", "" + e);
                    }
                }
            }
        });
    }

    private void autoCompleteTagListener() {
        autoTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (tagArray != null && tagArray.length > 0) {
                    //Toast.makeText(PostQuestionActivity.this,""+tagArray[i],Toast.LENGTH_LONG).show();
                    tagPicking(autoTag.getText().toString().trim());
                }
            }
        });
    }

    //[0]->question  [1]->tags
    private void okHttpPostQuestion(String... params) {
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("ques_user_id", userModel.getUserId()));
        arrayKeyValueModel.add(new KeyValueModel("ques_main_cat_id", Constants.SEO_CAT_ID));
        arrayKeyValueModel.add(new KeyValueModel("user_question", params[0]));
        arrayKeyValueModel.add(new KeyValueModel("question_tags", params[1]));

        String existingQuesId = quesModel.getQuesId().trim();
        String url;
        if (existingQuesId.length() > 0) {
            url = Urls.EDIT_QUESTION;
            arrayKeyValueModel.add(new KeyValueModel("ques_id", existingQuesId));
            finishAnswerList=true;
        } else {
            url = Urls.ADD_QUESTION;
            finishAnswerList=false;
        }

        Log.v("UrlQuestion", url);
        OkHttpCalls calls = new OkHttpCalls(url, arrayKeyValueModel);
        calls.initiateCallPost(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponse(true, "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(false, response.body().string());
            }
        });
    }

    private void handleResponse(final boolean failed, final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                if (failed) {
                    dialogMsg.showNetworkErrorDialog(getString(R.string.networkError));
                    return;
                }
                try {
                    Log.v("ResponseQuesAddEdit", response);
                    JSONObject json = new JSONObject(response);
                    String msg=quesModel.getQuesId().trim().length()<1?"Your question has been posted\nIt will take about 8-10 hours to review and post your question into the app":"Question successfully updated";
                    if (json.getInt("result") == 1) {
                        dialogMsg.showSuccessDialog(msg, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogMsg.getDialog().dismiss();
                                if(quesModel.getQuesId().trim().length()>0){
                                    AnswerListActivity.activity.finish();
                                    Intent intent=PostQuestionActivity.this.getIntent();
                                    intent.setClass(PostQuestionActivity.this,AnswerListActivity.class);
                                    intent.putExtra("QuesModel", quesModel);
                                    startActivity(intent);
                                }
                                PostQuestionActivity.this.finish();
                            }
                        });
                    } else {
                        Toast.makeText(PostQuestionActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.v("ExceptionParse", "" + e);
                    Toast.makeText(PostQuestionActivity.this, "Parsing error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
