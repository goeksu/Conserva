package in.goksu.conserva;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class yuzonikiArama extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);


        if(number.equals("123")){
            context.startService(new Intent(context, konum.class));
        }else if(number.equals("5")){
            context.stopService(new Intent(context, konum.class));
        }

    }

}
