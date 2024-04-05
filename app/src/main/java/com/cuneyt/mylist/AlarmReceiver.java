package com.cuneyt.mylist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.cuneyt.mylist.entities.TodoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private List<TodoModel> todoModels = new ArrayList<TodoModel>();
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private NotificationCompat.Builder notiBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notiIntent = new Intent(context, MainActivity.class); // Status Bar'a düşen bildirime tıklandığında gideceği sayfa.
        notiIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent destinationIntent = PendingIntent.getActivity(context,1, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT); // Buradaki FLAG_UPDATE_CURRENT sayesinde listedeki silmek istediğim alarm iptal olur, diğerleri aktif kalır

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // OREO VE ÜSTÜ SÜRÜMLER İÇİN
            String channelID = "channelID"; // Oreo'da bildirimlerin bir araya toplanması için gerekli.
            String channelName = "channelName";
            String channelDefinition = "channelDef";
            int channelPrecedence = NotificationManager.IMPORTANCE_HIGH; // Kanal önceliği. Bildirim önceliği. Yüksek olarak ayarlandı.

            NotificationChannel channel = notificationManager.getNotificationChannel(channelID); // Oreo ile gelen özellik. NotificationChannel

            if (channel == null){
                channel = new NotificationChannel(channelID, channelName, channelPrecedence);
                channel.setDescription(channelDefinition);
                notificationManager.createNotificationChannel(channel);
            }

            notiBuilder = new NotificationCompat.Builder(context,channelID);
            notiBuilder.setContentTitle("Not Defterim"); // Bildirim başlığı
            notiBuilder.setContentText("Göz atmanız gereken notlar var."); // Bildirim içeriği
            notiBuilder.setSmallIcon(R.drawable.ic_alarm); // Bildirim logosu
            notiBuilder.setAutoCancel(true); // Bildirim'e tıklandığında otomatik kaybolur.
            notiBuilder.setContentIntent(destinationIntent);

        }else { // OREO ÖNCESİ SÜRÜMLER İÇİN

            notiBuilder = new NotificationCompat.Builder(context); // Oreo öncesi sürümlerde kanal özelliği yok.
            notiBuilder.setContentTitle("Not Defterim"); // Bildirim başlığı
            notiBuilder.setContentText("Göz atmanız gereken notlar var."); // Bildirim içeriği
            notiBuilder.setSmallIcon(R.drawable.ic_alarm); // Bildirim logosu
            notiBuilder.setAutoCancel(true); // Bildirim'e tıklandığında otomatik kaybolur.
            notiBuilder.setContentIntent(destinationIntent);
            notiBuilder.setPriority(Notification.PRIORITY_HIGH); // Bildirim önceliği.
        }

        notificationManager.notify(1, notiBuilder.build()); // Bildirim'i göster.

    }
}
