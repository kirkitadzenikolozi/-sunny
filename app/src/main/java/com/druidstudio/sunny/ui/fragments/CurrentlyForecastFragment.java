package com.druidstudio.sunny.ui.fragments;


import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.druidstudio.sunny.ui.adapters.CurrentlyForecastListAdapter;
import com.druidstudio.sunny.util.ConnectionDetector;
import com.druidstudio.sunny.util.IntentUtils;
import com.druidstudio.sunny.util.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentlyForecastFragment extends ListFragment {

    private static final String TAG = CurrentlyForecastFragment.class.getSimpleName();

    private ListView listView;
    private TextView networkWarningTv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;

    static private CurrentlyForecastListAdapter currentlyForecastListAdapter;


    public static List<CurrentlyWeather> currentlyWeatherList = new ArrayList<>();


    //is internet or not
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;



    public CurrentlyForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_currently_forecast, container, false);

        init(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            syncCurrentlyWeather();
        } else {

            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("TURN ON", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            IntentUtils.internet(getActivity());
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        CurrentlyWeather currentlyWeather = currentlyWeatherList.get(position);
        IntentUtils.forecastDetail(getActivity(), currentlyWeather);
    }

    private void init(final View view) {
        networkWarningTv = (TextView) view.findViewById(R.id.no_internet_connection_tv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_currently_forecast_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather(view);
            }
        });

        currentlyForecastListAdapter = new CurrentlyForecastListAdapter(getActivity(), currentlyWeatherList);
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(currentlyForecastListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CurrentlyWeather currentlyWeather = currentlyWeatherList.get(position);
                IntentUtils.forecastDetail(getActivity(), currentlyWeather);
            }
        });
    }

    private void syncCurrentlyWeather() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Config.API_BASE_URL + "weather/currently",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray result = data.getJSONArray("results");

                            for (int i = 0; i < result.length(); i++) {
                                JSONObject object = result.getJSONObject(i);
                                CurrentlyWeather currentlyWeather = new CurrentlyWeather();
                                currentlyWeather.setLat(object.getDouble("lat"));
                                currentlyWeather.setLng(object.getDouble("lng"));
                                currentlyWeather.setCity(object.getString("city"));
                                currentlyWeather.setTime(TimeUtils.unixToDate(object.getLong("time")));
                                currentlyWeather.setSummary(object.getString("summary"));
                                currentlyWeather.setIcon(object.getString("icon"));
                                currentlyWeather.setTemperature(object.getInt("temperature"));
                                currentlyWeather.setHumidity(object.getDouble("humidity"));
                                currentlyWeather.setPressure(object.getDouble("pressure"));

                                currentlyWeatherList.add(currentlyWeather);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        currentlyForecastListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        ForecastMapFragment.setupMapData();
                        Log.e(TAG, "WeatherListCount: " + String.valueOf(currentlyWeatherList.size()));


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void refreshWeather(View view) {


        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            currentlyWeatherList.clear();
            currentlyForecastListAdapter.notifyDataSetChanged();
            syncCurrentlyWeather();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshWeather(rootView);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    public static void searchCityForecast(Context context, String cityName) {
        currentlyWeatherList.clear();
        currentlyForecastListAdapter.notifyDataSetChanged();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Config.API_BASE_URL + "weather/currently/" + cityName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject main = new JSONObject(response);
                            JSONArray jsonArray = main.getJSONArray("results");
                            JSONObject object = (JSONObject) jsonArray.get(0);
                            CurrentlyWeather currentlyWeather = new CurrentlyWeather();
                            currentlyWeather.setCity(object.getString("city"));
                            currentlyWeather.setTime(TimeUtils.unixToDate(object.getLong("time")));
                            currentlyWeather.setSummary(object.getString("summary"));
                            currentlyWeather.setIcon(object.getString("icon"));
                            currentlyWeather.setTemperature(object.getInt("temperature"));
                            currentlyWeather.setHumidity(object.getDouble("humidity"));
                            currentlyWeather.setPressure(object.getDouble("pressure"));


                            currentlyWeatherList.add(currentlyWeather);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        currentlyForecastListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
