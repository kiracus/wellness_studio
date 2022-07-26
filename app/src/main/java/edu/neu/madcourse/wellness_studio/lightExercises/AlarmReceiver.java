package edu.neu.madcourse.wellness_studio.lightExercises;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.neu.madcourse.wellness_studio.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Intent intentOfLeadingToPage = new Intent(context, LightExercises.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intentOfLeadingToPage,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"lightExerciseAndroid")
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Wellness Studio")
                .setContentText("It's time to do some light exercises")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(123,builder.build());
    }

}
