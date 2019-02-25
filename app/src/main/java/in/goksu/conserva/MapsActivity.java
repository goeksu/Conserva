package in.goksu.conserva;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap harita;
    LocationManager locationManager;
    Location konum;
    String adres,ekbilgit;
AlertDialog alertDialog;
EditText ekbilgitb;
boolean merkez = false;

boolean manuelKonum = false;
    Marker marker;


    boolean gps_acik = false;
    boolean gpsnetwork_acik = false;


    FirebaseDatabase database;
    DatabaseReference cagri;

    ImageView ambulans, itfaiye, polis;
    Button konumverisi, paylasimiptal;
    Geocoder geocoder;
    List<Address> adresses;
    TextView adresText, uyari;
    SmsManager mesajgonder = SmsManager.getDefault();

    String id = "benzersizid";


Button kapatDialog;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));


        geocoder = new Geocoder(this, Locale.getDefault());
        database = FirebaseDatabase.getInstance();
        cagri = database.getReference("cagri");

        polis = findViewById(R.id.cop);
        itfaiye = findViewById(R.id.fire);
        ambulans = findViewById(R.id.medic);
        konumverisi = findViewById(R.id.veri);

        adresText = findViewById(R.id.adrestext);
        uyari = findViewById(R.id.uyari);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        servisKontrol();

        konum = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        konumverisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ek();

            }
        });


        itfaiye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sistemUyari(33);
            }
        });

        polis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sistemUyari(32);
            }
        });

        ambulans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sistemUyari(31);
            }
        });


    }


    private void konumBildir(int i) {

if(baglanti()) {

    switch (i) {
        case 1:
            cagri.child("konum").setValue(id + "*" + 1 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude()) + "*" +ekbilgit);
            break;
        case 2:
            cagri.child("konum").setValue(id + "*" + 2 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude())+ "*" +ekbilgit);
            break;
        case 3:
            cagri.child("konum").setValue(id + "*" + 3 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude())+ "*" +ekbilgit);
            break;
    }

}else{

    switch (i) {
        case 1:
            mesajgonder.sendTextMessage("05436211104", null, String.valueOf(id + "*" + 1 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude()) + "*" + ekbilgit), null, null);
            break;
        case 2:
            mesajgonder.sendTextMessage("05436211104", null, String.valueOf(id + "*" + 2 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude()) + "*" + ekbilgit), null, null);
            break;
        case 3:
            mesajgonder.sendTextMessage("05436211104", null, String.valueOf(id + "*" + 3 + "*" + adres + "*" + String.valueOf(konum.getLatitude() + "*" + konum.getLongitude()) + "*" + ekbilgit), null, null);
            break;
            }


}
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        harita = googleMap;
if(konum != null) {
    harita.setMyLocationEnabled(true);
    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(konum.getLatitude(), konum.getLongitude()));
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
    harita.moveCamera(center);
    harita.animateCamera(zoom);

    marker = harita.addMarker(new MarkerOptions()
            .visible(false)
            .position(new LatLng(konum.getLatitude(), konum.getLongitude()))
            .title("Buradayım!")
            .draggable(true));
}
        harita.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
              if(!manuelKonum){
                  marker.setPosition(latLng);
                sistemUyari(21);
                }else{
                  marker.setPosition(latLng);
                 konum.setLatitude(latLng.latitude);
                  konum.setLongitude(latLng.longitude);
                  ortala();
                  adresGuncelle();
              }

            }

        });



harita.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
    @Override
    public boolean onMyLocationButtonClick() {
        if(manuelKonum){
        sistemUyari(22);}
        return false;
    }
});


       harita.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
           @Override
           public void onMyLocationChange(Location location) {
               baglanti();
               if (!manuelKonum) {
                   konum = location;
                   adresGuncelle();

                  if(!merkez){
                      CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(konum.getLatitude(), konum.getLongitude()));
                      CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                      harita.moveCamera(center);
                      harita.animateCamera(zoom);

                      merkez = true;
                  }
               }
           }
       });





    }

    private void adresGuncelle() {

        try {
            adresses = geocoder.getFromLocation(konum.getLatitude(), konum.getLongitude(), 1);
            adres = adresses.get(0).getAddressLine(0);
            adresText.setText(adres);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void servisKontrol() {
        izinAl();
        try {
            gps_acik = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            gpsnetwork_acik = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}


        if(!gps_acik && !gpsnetwork_acik) {
            sistemUyari(1);
            }

        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
           wifi.setWifiEnabled(true);
            Toast.makeText(this, "Wifi açıldı.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sistemUyari(int i) {
        if(konum == null && i >= 30 && i<= 33){
            Toast.makeText(this, "Konum verisi alınamadı!", Toast.LENGTH_SHORT).show();
            i = 0;
        }
        try {

            alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setCanceledOnTouchOutside(false);

            switch (i){
                case 1:
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    alertDialog.setMessage(getResources().getString(R.string.gpsuyari));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MapsActivity.this.startActivity(new Intent(action));
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "Harita üzerinde konum seçerek yardım oluşturabilirsiniz.", Toast.LENGTH_SHORT).show();
                            marker.setVisible(true);
                            dialog.dismiss();
                        }
                    });
                    break;

                case 21:
                    alertDialog.setMessage("Konumunuzu elle seçiyorsunuz");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            manuelKonum = true;
                           marker.setVisible(true);
                           konum.setLatitude(marker.getPosition().latitude);
                            konum.setLongitude(marker.getPosition().longitude);
                           ortala();
                            adresGuncelle();
                            Toast.makeText(MapsActivity.this, "Konum butonu ile otomatik konuma geçebilirsiniz.", Toast.LENGTH_SHORT).show();

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    break;

                case 22:
                    alertDialog.setMessage("Cihaz konumu otomatik alınıyor.");
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            marker.setVisible(false);
                            manuelKonum = false;

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });
                    break;
                case 31:

                    alertDialog.setMessage(getResources().getString(R.string.ambulansile));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
konumBildir(1);

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "Konum bildirimi yapılmadı", Toast.LENGTH_SHORT).show();

                        }
                    });
                    break;
                case 32:
                    alertDialog.setMessage(getResources().getString(R.string.polisile));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            konumBildir(2);

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "Konum bildirimi yapılmadı", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 33:
                    alertDialog.setMessage(getResources().getString(R.string.itfaiyeile));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getResources().getString(R.string.evet), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            konumBildir(3);

                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getResources().getString(R.string.hayir), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "Konum bildirimi yapılmadı", Toast.LENGTH_SHORT).show();

                        }
                    });
                    break;



            }


            alertDialog.show();
        } catch(Exception ex) {}



    }

    private void ortala() {
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(konum.getLatitude(), konum.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        harita.moveCamera(center);
        harita.animateCamera(zoom);
    }

    public boolean baglanti() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue==0);

        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void ek(){
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.ekbilgi);
        dialog.setTitle("Ek bilgi");

        dialog.setCanceledOnTouchOutside(false);

         ekbilgitb = dialog.findViewById(R.id.ektextbox);
        kapatDialog = dialog.findViewById(R.id.ok);
kapatDialog.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ekbilgit = ekbilgitb.getText().toString();
        dialog.dismiss();
    }
});

        dialog.show();

    }

    private void konumPaylasiliyor() {
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.konumpaylasiliyor);
        dialog.setCanceledOnTouchOutside(false);

        paylasimiptal = dialog.findViewById(R.id.paylasimiptal);
        paylasimiptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MapsActivity.this, konum.class));
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private boolean izinAl() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                ){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.INTERNET,
            },100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
                    && grantResults[7] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "İzinler alındı", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this, "İzinlerin alınması gerekli", Toast.LENGTH_SHORT).show();
                izinAl();
            }
        }
    }
    private boolean servisCalismasi(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(servisCalismasi(in.goksu.conserva.konum.class)){
            konumPaylasiliyor();
        }
    }
}



