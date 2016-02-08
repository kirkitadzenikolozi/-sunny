package com.druidstudio.sunny.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.druidstudio.sunny.R;
import com.druidstudio.sunny.model.CurrentlyWeather;
import com.druidstudio.sunny.util.TimeUtils;

import java.util.List;

/**
 * Created by kirkita on 06.02.16.
 */
public class CurrentlyForecastListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<CurrentlyWeather> currentlyWeatherList;

    public CurrentlyForecastListAdapter(Context context, List<CurrentlyWeather> currentlyWeatherList) {
        this.context = context;
        this.currentlyWeatherList = currentlyWeatherList;
    }

    @Override
    public int getCount() {
        return currentlyWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return currentlyWeatherList.get(position);
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
            convertView = inflater.inflate(R.layout.currently_forecast_list_item, null);

        TextView city = (TextView) convertView.findViewById(R.id.currently_forecast_list_item_city);
        TextView temperature = (TextView) convertView.findViewById(R.id.currently_forecast_list_item_temperature);
        TextView summary = (TextView) convertView.findViewById(R.id.currently_forecast_list_item_summary);
        TextView date = (TextView) convertView.findViewById(R.id.currently_forecast_list_item_date);
        ImageView icon = (ImageView) convertView.findViewById(R.id.currently_forecast_list_item_icon);

        CurrentlyWeather currentlyWeather = currentlyWeatherList.get(position);
        icon.setBackgroundResource(CurrentlyWeather.getIconId(currentlyWeather.getIcon()));
        city.setText(currentlyWeather.getCity());
        temperature.setText(currentlyWeather.getTemperature() + "°");
        summary.setText(currentlyWeather.getSummary());
        date.setText(TimeUtils.getDayOfMonth() + " " + TimeUtils.getDayOfWeek().substring(0, 3));

        return convertView;
    }
}
