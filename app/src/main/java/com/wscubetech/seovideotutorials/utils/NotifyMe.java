package com.wscubetech.seovideotutorials.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.activities.MainActivity;

import org.json.JSONObject;


/**
 * Created by wscubetech on 10/7/15.
 */
public class NotifyMe {

    Context context;
    String msg = "";
    int comingFrom = 0;

    public NotifyMe(Context context) {
        this.context = context;
    }

    public void notifyNow(String text, String id) {


        try {
            JSONObject jsonObject = new JSONObject(text);
            msg = jsonObject.getString("msg");
            comingFrom = jsonObject.getInt("type");
        } catch (Exception e) {
            e.printStackTrace();
        }


        int nId = Integer.parseInt(id);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("type", comingFrom);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context).
                setContentTitle("WsCubetech").
                setContentText(msg).
                setContentIntent(pi).
                setAutoCancel(true).
                setSmallIcon(R.drawable.logo_splash);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            noti.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        n.notify(nId, noti.build());

    }

}
