package in.goksu.conserva;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class sesTuslari extends BroadcastReceiver {
    static int i = 0;
    static int saniye = -1;
    Date zaman = Calendar.getInstance().getTime();

    @Override
    public void onReceive(Context context, Intent intent) {
       if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
         if(saniye == -1){
             i++;
            saniye = zaman.getSeconds();}


            if(zaman.getSeconds() - saniye <= 1 || zaman.getSeconds() - saniye <= -59){
                i++;
            }else{
                i =1;
                saniye = zaman.getSeconds();
            }

            if(i>=36){

                context.startService(new Intent(context, konum.class));
             i =0;
             saniye = 0;
            }


        }
    }
}
