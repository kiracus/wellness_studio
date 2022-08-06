package edu.neu.madcourse.wellness_studio;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmWakeupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, WakeupSleepGoal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, 0);

        Toast.makeText(context, "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "wakeAlarmAndroid")
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Alarm Alarm Alarm")
                .setContentText("It is time to wakeup!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(123, builder.build());


//        String uriString = intent.getStringExtra("KEY_TONE_URL");
//
//        Toast.makeText(context,
//                "Alarm received!\n"
//                        + "uriString: " + uriString,
//                Toast.LENGTH_LONG).show();
//
//        Intent secIntent = new Intent(context, SecondActivity.class);
//        secIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        secIntent.putExtra("SEC_RINGTONE_URI", uriString);
//        context.startActivity(secIntent);


//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        Intent intentOfLeadingToPage = new Intent(context, WakeupSleepGoal.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intentOfLeadingToPage,0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"alarmAndroidWakeup")
//                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
//                .setContentTitle("Alarm Alarm Alarm")
//                .setContentText("It is time to wakeup!")
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
//        notificationCompat.notify(123,builder.build());



    }
}
