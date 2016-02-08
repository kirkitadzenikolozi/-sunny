package com.druidstudio.sunny.ui.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.druidstudio.sunny.Config;
import com.druidstudio.sunny.R;
import com.druidstudio.sunny.model.CurrentlyWeather;
import com.druidstudio.sunny.model.DailyWeather;
import com.druidstudio.sunny.ui.adapters.DailyListAdapter;
import com.druidstudio.sunny.util.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForecastDetailActivity extends AppCompatActivity {

    private static final String TAG = ForecastDetailActivity.class.getSimpleName();

    private CurrentlyWeather passedCurrentlyWeatherObj;

    private List<DailyWeather> dailyWeatherList = new ArrayList<>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DailyListAdapter dailyListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);

        Intent i = getIntent();
        passedCurrentlyWeatherObj = (CurrentlyWeather) i.getSerializableExtra("weather");

        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(passedCurrentlyWeatherObj.getCity());
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_forecast_detail_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather();
            }
        });

        dailyListAdapter = new DailyListAdapter(ForecastDetailActivity.this, dailyWeatherList);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(dailyListAdapter);


        getDailyWeather();
    }


    private void getDailyWeather() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Config.API_BASE_URL + "weather/daily/" + passedCurrentlyWeatherObj.getCity(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject main = new JSONObject(response);
                            JSONArray dailyWeatherArray = main.getJSONArray("results");

                            for (int i = 1; i < dailyWeatherArray.length(); i++) {
                                JSONObject object = dailyWeatherArray.getJSONObject(i);
                                DailyWeather dailyWeather = new DailyWeather();
                                dailyWeather.setTime(TimeUtils.getDayOfWeekByUnixSeconds(object.getLong("time")));
                                dailyWeather.setSummary(object.getString("summary"));
                                dailyWeather.setIcon(object.getString("icon"));
                                dailyWeather.setTemperatureMin(object.getInt("temperatureMin"));
                                dailyWeather.setTemperatureMax(object.getInt("temperatureMax"));
                                dailyWeather.setHumidity(object.getDouble("humidity"));
                                dailyWeather.setPressure(object.getDouble("pressure"));

                                dailyWeatherList.add(dailyWeather);
                            }


                            dailyListAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);

                            Log.e(TAG, "DailyWeatherListSize: " + String.valueOf(dailyWeatherList.size()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void refreshWeather() {
        dailyWeatherList.clear();
        dailyListAdapter.notifyDataSetChanged();
        getDailyWeather();
        swipeRefreshLayout.setRefreshing(false);
    }
}
