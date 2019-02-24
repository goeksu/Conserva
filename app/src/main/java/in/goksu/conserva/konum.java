package in.goksu.conserva;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class konum extends Service {
    private LocationListener listener;
    private LocationManager locationManager;

    FirebaseDatabase database;
    DatabaseReference cagri;
    Notification bildirim;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        database = FirebaseDatabase.getInstance();
        cagri = database.getReference("cagri");



        Uri uyariSesi = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        bildirim = new NotificationCompat.Builder(this, "conserva")
                .setContentTitle("Conserva")
                .setContentText("Konum bilgileriniz aktif olarak paylaşılıyor. Durdurmak için tıklayınız!")
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pendingIntent)
                .setSound(uyariSesi)
                .build();


        startForeground(1, bildirim);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                cagri.child("konum").setValue("benzersizid" + "*" + 1 + "*" + "adres" + "*" + String.valueOf(location.getLatitude() + "*" + location.getLongitude()));

                /**
                Intent i = new Intent("konum_guncelleme");
                i.putExtra("koordinat",location.getLongitude()+" "+location.getLatitude());
                sendBroadcast(i);
                 **/
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, listener);

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
            startForeground(0, bildirim);

        }
    }


}
