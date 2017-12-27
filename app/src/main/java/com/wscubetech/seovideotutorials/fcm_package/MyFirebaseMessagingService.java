package com.wscubetech.seovideotutorials.fcm_package;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.activities.HomeActivity;
import com.wscubetech.seovideotutorials.model.NotificationModel;
import com.wscubetech.seovideotutorials.model.QuestionListModel;
import com.wscubetech.seovideotutorials.user_model.UserDetailsPrefs;
import com.wscubetech.seovideotutorials.user_model.UserModel;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by wscubetech on 26/11/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> dataMap = remoteMessage.getData();
//        Log.v(TAG,"Notification: "+remoteMessage.getNotification().getBody());

        //Calling method to generate notification
        String response = dataMap.get("body");
        Log.d(TAG, "Notification Message Body: " + response);

        parseMyNotification(response);
    }


    private void parseMyNotification(String response) {
        Log.v("NotifyingResponse", response);
        NotificationModel notificationModel = new NotificationModel();
        QuestionListModel quesModel = new QuestionListModel();
        try {
            JSONObject json = new JSONObject(response);
            notificationModel.setNotificationTitle(json.getString("msg"));
            notificationModel.setNotificationFor(json.getString("notification_for"));
            //for answer posted, notify user who has posted the question
            if (notificationModel.getNotificationFor().trim().equals("6")) {
                quesModel.setQuesId(json.getString("ques_id"));
                quesModel.setQuesTitle(json.getString("user_question"));
                quesModel.setTags(json.getString("question_tags"));
                quesModel.setAnsCount(json.getString("view_question_count"));
                quesModel.setQuesDate(json.getString("ques_time"));
                quesModel.setTotalLikes(Integer.parseInt(json.getString("total_likes")));
                quesModel.setTotalDislikes(Integer.parseInt(json.getString("total_dislikes")));

                quesModel.setLikeCount("" + (quesModel.getTotalLikes() - quesModel.getTotalDislikes()));

                quesModel.setTotalViews(Integer.parseInt(json.getString("total_views")));

                UserModel userModel = new UserDetailsPrefs(this).getUserModel();
                if (userModel.getUserId().trim().length() > 0) {
                    quesModel.setLiked(Integer.parseInt(json.getString("liked")));
                }


                userModel = new UserModel();
                userModel.setUserId(json.getString("ques_user_id"));
                userModel.setUserName(json.getString("seo_users_name"));
                userModel.setUserEmail(json.getString("seo_users_email"));
                userModel.setUserImage(json.getString("seo_users_image"));
                quesModel.setUserModel(userModel);
            } else {
                quesModel = null;
            }
            notificationModel.setNotificationImage(json.getString("image"));
            notificationModel.setNotificationId(json.getString("notification_id"));
        } catch (Exception e) {
            Log.v("ExceptionNotifying", "" + e);
        }
        sendNotification(notificationModel, quesModel);
    }


    private void sendNotification(NotificationModel model, QuestionListModel quesModel) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("NotificationModel", model);

        //for answer posted, notify user who has posted the question
        if (model.getNotificationFor().equalsIgnoreCase("6") && quesModel != null) {
            intent.putExtra("QuesModel", quesModel);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(model.getNotificationTitle())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));

        new GenBitmap(model, notificationBuilder).execute();

    }

    public Bitmap getBitmap(String url) {
        URL newUrl = null;
        try {
            newUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Bitmap bmp = (BitmapFactory.decodeStream(newUrl.openConnection().getInputStream()));
            return bmp == null ? null : Bitmap.createScaledBitmap(bmp, 500, 220, true);
        } catch (Exception e) {
            Log.v("BitmapException", "" + e);
        }
        return null;
    }

    public class GenBitmap extends AsyncTask<Void, Void, Void> {

        Bitmap bmp = null;
        NotificationModel model;
        NotificationCompat.Builder notificationBuilder;

        public GenBitmap(NotificationModel model, NotificationCompat.Builder notificationBuilder) {
            this.model = model;
            this.notificationBuilder = notificationBuilder;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                bmp = getBitmap(Urls.imageUrl + model.getNotificationImage());
            } catch (Exception e) {
                Log.v("bmpDoInBck", "" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && bmp != null) {
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp).setSummaryText(model.getNotificationTitle()));
                }
            } catch (Exception e) {
                Log.v("NotifyingImageException", "" + e);
            }

            UserModel userModel = new UserDetailsPrefs(MyFirebaseMessagingService.this).getUserModel();
            if (model.getNotificationFor().equals("6") && userModel.getUserId().trim().length() < 1) {
                //user not logged in
                return;
            }
            try {
                int nId = Integer.parseInt(model.getNotificationId());
                notificationManager.notify(nId, notificationBuilder.build());
            } catch (NumberFormatException e) {
                notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
            }
        }
    }

    public int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_white_logo : R.drawable.logo_splash;
    }
}
