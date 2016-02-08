package com.druidstudio.sunny.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.druidstudio.sunny.model.CurrentlyWeather;
import com.druidstudio.sunny.ui.activities.ForecastDetailActivity;

/**
 * Created by kirkita on 05.02.16.
 */
public class IntentUtils {


    public static void forecastDetail(Context context, CurrentlyWeather currentlyWeather) {
        Intent i = new Intent(context, ForecastDetailActivity.class);
        i.putExtra("weather", currentlyWeather);
        context.startActivity(i);
    }


    public static void internet(Context context) {
        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(i);
    }
}
