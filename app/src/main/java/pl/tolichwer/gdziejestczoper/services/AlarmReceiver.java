package pl.tolichwer.gdziejestczoper.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("GEO_ALARM")){
            Intent geoJobIntent = new Intent(context, GeoJobIntentService.class);
            GeoJobIntentService.enqueueWork(context, geoJobIntent);

        } else if (intent.getAction().equals("LOG_ALARM")) {
            Intent logJobIntent = new Intent(context, LogJobIntentService.class);
            LogJobIntentService.enqueueWork(context, logJobIntent);
        }

    }
}


