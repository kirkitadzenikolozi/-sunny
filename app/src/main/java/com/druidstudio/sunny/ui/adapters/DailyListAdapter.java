package com.druidstudio.sunny.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.druidstudio.sunny.R;
import com.druidstudio.sunny.model.DailyWeather;

import java.util.List;

/**
 * Created by kirkita on 06.02.16.
 */
public class DailyListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<DailyWeather> dailyWeatherList;

    public DailyListAdapter (Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
    }

    @Override
    public int getCount() {
        return dailyWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.daily_forecast_list_item, null);

        TextView time = (TextView) convertView.findViewById(R.id.daily_forecast_list_item_day_of_week);
        TextView temperatureMin = (TextView) convertView.findViewById(R.id.daily_forecast_list_item_temperature_min);
        TextView temperatureMax = (TextView) convertView.findViewById(R.id.daily_forecast_list_item_temperature_max);
        TextView summary = (TextView) convertView.findViewById(R.id.daily_forecast_list_item_summary);

        DailyWeather dailyWeather = dailyWeatherList.get(position);
        time.setText(dailyWeather.getTime());
        temperatureMin.setText(String.valueOf(dailyWeather.getTemperatureMin()) + "°");
        temperatureMax.setText(String.valueOf(dailyWeather.getTemperatureMax()) + "°");
        summary.setText(dailyWeather.getSummary());

        return convertView;
    }
}
