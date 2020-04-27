package com.chen.angel_study.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import com.chen.angel_study.Activitys.ClockActivity;
import com.chen.angel_study.Appication;
import com.chen.angel_study.Manager.PlanManager;
import com.chen.angel_study.Plans.Constants;
import com.chen.angel_study.Plans.Plan;
import com.chen.angel_study.Plans.PlanShow;

public class ClockService extends Service {
    private static final String TAG = "ClockService";
    public static final String EXTRA_PLAN_ID = "extra.plan.id";
    public static final String EXTRA_REMIND_TIME = "extra.remind.time";
    public static final String EXTRA_PLAN = "extra.plan";
    private PlanShow mPlanShow = PlanShow.getInstance();
    public ClockService() {
        Log.d(TAG, "ClockService: Constructor");
    }
    private static final String ID = "channel_1";
    private static final String NAME = "前台服务";
    private PlanManager mEventManager = PlanManager.getInstance();
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate: ");
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_MIN);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, ID)
                    .build();
            startForeground(1, notification);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(TAG, "onStartCommand: ");
//        String CHANNEL_ID = "my_channel_01";
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//        startForeground(-1,mBuilder.build());

        ToClockActivity(getApplicationContext(), intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void ToClockActivity(Context context, Intent intent) {
        Intent i = new Intent();
        i.setClass(context, ClockActivity.class);
        i.putExtra(EXTRA_PLAN_ID, intent.getIntExtra(EXTRA_PLAN_ID, -1));
        Plan plan = mPlanShow.findId(intent.getIntExtra(EXTRA_PLAN_ID, -1));
        Appication application = (Appication) this.getApplication();
        int m = application.getNumber();
        Log.d(TAG, "ee"+m+"");
        if(m == 0)
            stopSelf();
        if (plan == null) {
            Log.d(TAG, "ToClockActivity: 11");
            return;
        }
        i.putExtra(EXTRA_REMIND_TIME, intent.getStringExtra(EXTRA_REMIND_TIME));
        i.putExtra(EXTRA_PLAN, plan);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //保存响铃状态
        plan.setmIsClocked(Constants.PlanFlag.IMPORTANT);
        mEventManager.SavePlan(plan);
        m=m-1;
        application.setNumber(m);
        startActivity(i);
        stopSelf();
        Log.d(TAG, "ToClockActivity: 22"+plan.getmTitle());
        Log.d(TAG, "jj"+m+"");

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        stopForeground(true);
        super.onDestroy();
    }
}
