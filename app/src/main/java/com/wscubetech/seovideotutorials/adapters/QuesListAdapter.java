package com.wscubetech.seovideotutorials.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dpizarro.autolabel.library.AutoLabelUI;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.AnswerListActivity;
import com.wscubetech.seovideotutorials.dialogs.DialogMsg;
import com.wscubetech.seovideotutorials.dialogs.MyProgressDialog;
import com.wscubetech.seovideotutorials.model.KeyValueModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;
import com.wscubetech.seovideotutorials.utils.AddEditAnswer;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.FirstTimeTargetView;
import com.wscubetech.seovideotutorials.utils.ForceShowPopUpIcon;
import com.wscubetech.seovideotutorials.utils.LikeDislikeQuesAns;
import com.wscubetech.seovideotutorials.utils.LoadUserImage;
import com.wscubetech.seovideotutorials.utils.OkHttpCalls;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wscubetech on 5/4/17.
 */

public class QuesListAdapter extends RecyclerView.Adapter<QuesListAdapter.ViewHolder> {

    ArrayList<QuestionListModel> arrayModel = new ArrayList<>();
    Activity act;
    String comingFrom; //QuestionList    AnswerList
    LoadUserImage loadUserImage;
    UserModel userModel;
    LikeDislikeQuesAns likeDislikeQuesAns;

    QuestionListModel quesModel; //for edit operation, showing in dialog (answer)


    public QuesListAdapter(Activity act, ArrayList<QuestionListModel> arrayModel, String comingFrom) {
        this.act = act;
        this.arrayModel = arrayModel;
        this.comingFrom = comingFrom;

        userModel = new UserDetailsPrefs(act).getUserModel();
        loadUserImage = new LoadUserImage();

        likeDislikeQuesAns = new LikeDislikeQuesAns(act);
    }

    //constructor for coming from answer list page
    public QuesListAdapter(Activity act, ArrayList<QuestionListModel> arrayModel, String comingFrom, QuestionListModel quesModel) {
        this(act, arrayModel, comingFrom);
        this.quesModel = quesModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(act).inflate(R.layout.row_ques_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionListModel model = arrayModel.get(position);
        holder.txtQuesTitle.setText(model.getQuesTitle().trim());
        holder.txtQuesBy.setText("By " + model.getUserModel().getUserName());
        holder.txtDate.setText(model.getQuesDate());
        holder.txtAnswerCount.setText(model.getAnsCount());
        holder.txtLikes.setText(String.valueOf(model.getTotalLikes()));
        holder.txtDislikes.setText(String.valueOf(model.getTotalDislikes()));

        if (comingFrom.equals("AnswerList")) {
            holder.txtAnswerCount.setVisibility(View.GONE);
            holder.imgUser.setVisibility(View.VISIBLE);

            holder.imgMore.setVisibility(userModel.getUserId().equals(model.getUserModel().getUserId()) ? View.VISIBLE : View.GONE);

            if (model.getUserModel().getUserImage().trim().length() > 1)
                loadUserImage.loadImageInImageView(act, Urls.imageUrl + model.getUserModel().getUserImage(), holder.imgUser);
        } else {
            holder.txtAnswerCount.setVisibility(View.VISIBLE);
            holder.imgUser.setVisibility(View.GONE);
            holder.imgMore.setVisibility(View.GONE);
        }

        if (isLoggedIn()) {
            holder.btnLike.setChecked(model.getLiked() == 1);
            holder.btnDislike.setChecked(model.getLiked() == 2);
        } else {
            holder.btnLike.setChecked(false);
            holder.btnDislike.setChecked(false);
        }

        if (model.getTags().trim().length() > 0)
            showTags(holder, model.getTags().split(","));
        else
            holder.tagView.setVisibility(View.GONE);

        if (arrayModel.size() > 1)
            holder.viewLine.setVisibility(position == arrayModel.size() - 1 ? View.GONE : View.VISIBLE);
    }

    private void showTags(ViewHolder holder, String array[]) {
        holder.tagView.setVisibility(View.VISIBLE);
        holder.tagView.clear();
        for (String tag : array) {
            holder.tagView.addLabel(tag);
        }

    }

    @Override
    public int getItemCount() {
        return arrayModel.size();
    }

    private boolean isLoggedIn() {
        return userModel.getUserId().trim().length() > 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ShineButton.OnCheckedChangeListener, View.OnLongClickListener {

        AutoLabelUI tagView;
        TextView txtQuesTitle, txtQuesBy, txtDate, txtAnswerCount;
        RelativeLayout relMain, relLikeDislike;
        ImageView imgUser, imgMore;
        View viewLine;
        ShineButton btnLike, btnDislike;
        LinearLayout linLike, linDislike;
        TextView txtLikes, txtDislikes;

        public ViewHolder(View v) {
            super(v);
            txtQuesTitle = (TextView) v.findViewById(R.id.txtQuesTitle);
            txtQuesBy = (TextView) v.findViewById(R.id.txtQuesBy);
            txtAnswerCount = (TextView) v.findViewById(R.id.txtAnswerCount);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
            relMain = (RelativeLayout) v.findViewById(R.id.relMain);
            relLikeDislike = (RelativeLayout) v.findViewById(R.id.relLikeDislike);
            tagView = (AutoLabelUI) v.findViewById(R.id.tagView);
            imgUser = (ImageView) v.findViewById(R.id.imgUser);
            btnLike = (ShineButton) v.findViewById(R.id.btnLike);
            btnDislike = (ShineButton) v.findViewById(R.id.btnDislike);
            txtLikes = (TextView) v.findViewById(R.id.txtLikes);
            txtDislikes = (TextView) v.findViewById(R.id.txtDislikes);
            linLike = (LinearLayout) v.findViewById(R.id.linLike);
            linDislike = (LinearLayout) v.findViewById(R.id.linDislike);

            viewLine = v.findViewById(R.id.viewLine);
            imgMore = (ImageView) v.findViewById(R.id.imgMore);

            if (comingFrom.equalsIgnoreCase("QuestionList")) {
                relMain.setOnClickListener(this);
            }

            relMain.setOnLongClickListener(this);
            imgMore.setOnClickListener(this);

            if (isLoggedIn()) {
                btnDislike.setEnabled(true);
                btnLike.setEnabled(true);
            } else {
                btnDislike.setEnabled(false);
                btnLike.setEnabled(false);
            }

            btnLike.setOnCheckStateChangeListener(this);
            btnDislike.setOnCheckStateChangeListener(this);

        }

        @Override
        public void onClick(View view) {
            QuestionListModel model = arrayModel.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.relMain:
                    Intent intent = act.getIntent();
                    intent.setClass(act,AnswerListActivity.class);
                    intent.putExtra("QuesModel", model);
                    act.startActivity(intent);
                    break;
                case R.id.imgMore:
                    onLongClick(relMain);
                    break;
            }
        }

        @Override
        public void onCheckedChanged(View view, boolean checked) {
            final int position = getAdapterPosition();
            switch (view.getId()) {
                case R.id.btnLike:
                    if (new ConnectionDetector(act).isConnectingToInternet()) {
                        if (checked && btnDislike.isChecked()) {
                            updateDislikeCount(this, false, position);
                            btnDislike.setChecked(false);
                        }
                        updateLikeCount(this, checked, position);
                        likeDislikeQuesAns.okHttpLikeDislike(arrayModel.get(position).getQuesId(), userModel.getUserId(), comingFrom.equalsIgnoreCase("AnswerList") ? "2" : "1", checked ? "1" : "0");
                    } else {
                        btnLike.setChecked(!checked);
                    }
                    break;
                case R.id.btnDislike:
                    if (new ConnectionDetector(act).isConnectingToInternet()) {
                        if (checked && btnLike.isChecked()) {
                            updateLikeCount(this, false, position);
                            btnLike.setChecked(false);
                        }

                        updateDislikeCount(this, checked, position);
                        likeDislikeQuesAns.okHttpLikeDislike(arrayModel.get(position).getQuesId(), userModel.getUserId(), comingFrom.equalsIgnoreCase("AnswerList") ? "2" : "1", checked ? "2" : "0");

                    } else {
                        btnDislike.setChecked(!checked);
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            final Dialog progress = new MyProgressDialog(act).getDialog();
            switch (view.getId()) {
                case R.id.relMain:
                    //edit/delete only by the user himself/herself

                    if (comingFrom.equals("AnswerList") && userModel.getUserId().trim().equals(arrayModel.get(getAdapterPosition()).getUserModel().getUserId().trim())) {
                        showPopupMenu(imgMore, progress, getAdapterPosition());
                    }

                    break;
            }
            return false;
        }
    }

    private void updateLikeCount(ViewHolder holder, boolean checked, int position) {
        QuestionListModel model = arrayModel.get(position);
        int totalLikes = model.getTotalLikes();
        totalLikes = checked ? totalLikes + 1 : totalLikes - 1;
        model.setTotalLikes(totalLikes);
        model.setLiked(checked ? 1 : 0);
        arrayModel.set(position, model);
        holder.txtLikes.setText(String.valueOf(totalLikes));
    }

    private void updateDislikeCount(ViewHolder holder, boolean checked, int position) {
        QuestionListModel model = arrayModel.get(position);
        int totalDislikes = model.getTotalDislikes();
        totalDislikes = checked ? totalDislikes + 1 : totalDislikes - 1;
        model.setTotalDislikes(totalDislikes);
        model.setLiked(checked ? 2 : 0);
        arrayModel.set(position, model);
        holder.txtDislikes.setText(String.valueOf(totalDislikes));
    }

    public void showPopupMenu(View view, Dialog progress, int position) {
        PopupMenu popup = new PopupMenu(act, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(progress, position));
        popup.show();
        ForceShowPopUpIcon.forceShowNow(popup);
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        Dialog progress;
        int position;

        public MyMenuItemClickListener(Dialog progress, int position) {
            this.progress = progress;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.itemEdit:
                    AddEditAnswer editAnswer = new AddEditAnswer(act, progress, arrayModel, position, QuesListAdapter.this);
                    editAnswer.showDialogAddEdit(quesModel);
                    break;
                case R.id.itemDelete:
                    if (new ConnectionDetector(act).isConnectingToInternet())
                        okHttpDeleteAnswer(progress, position);
                    else
                        Toast.makeText(act, act.getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    }

    private void firstTimeDemo(View view) {
        final FirstTimeTargetView firstTimeTargetView = new FirstTimeTargetView(act, "FirstTimeAnswerLongTap");
        firstTimeTargetView.firstTimeDemonstration(view, "Edit Answer", "Long tap here to edit/delete your answer", 60, false, new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                firstTimeTargetView.firstTimeCompletion();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                onTargetClick(view);
            }
        });
    }

    private void okHttpDeleteAnswer(final Dialog progress, final int position) {
        QuestionListModel answerModel = arrayModel.get(position);
        progress.show();
        ArrayList<KeyValueModel> arrayKeyValueModel = new ArrayList<>();
        arrayKeyValueModel.add(new KeyValueModel("answer_id", answerModel.getQuesId()));

        OkHttpCalls calls = new OkHttpCalls(Urls.DELETE_ANSWER, arrayKeyValueModel);
        calls.initiateCall(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleResponseDelete(true, progress, position);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("ResponseDeleteAns", response.body().string());
                handleResponseDelete(false, progress, position);
            }
        });
    }

    private void handleResponseDelete(final boolean failed, final Dialog progress, final int position) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress.isShowing())
                    progress.dismiss();
                if (failed) {
                    Toast.makeText(act, act.getString(R.string.networkError), Toast.LENGTH_LONG).show();
                } else {
                    final DialogMsg dialogMsg = new DialogMsg(act);
                    dialogMsg.showSuccessDialog("Answer successfully deleted", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogMsg.getDialog().dismiss();
                            arrayModel.remove(position);
                            notifyItemRemoved(position);
                        }
                    });

                }
            }
        });
    }
}
