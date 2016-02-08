package com.druidstudio.sunny.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.druidstudio.sunny.R;
import com.druidstudio.sunny.model.CurrentlyWeather;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ForecastMapFragment extends Fragment {

    private static final String TAG = ForecastMapFragment.class.getSimpleName();

    private MapView mapView;
    private static GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forecast_map, container, false);

        mapView = (MapView) v.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mMap = mapView.getMap();
        MapsInitializer.initialize(this.getActivity());


        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Tbilisi and move the camera
        // Tbilisi lat & lng
        LatLng latLngToZoom = new LatLng(41.7151377, 44.827096);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngToZoom));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }



    public static void setupMapData() {
        List<CurrentlyWeather> currentlyWeatherList = CurrentlyForecastFragment.currentlyWeatherList;
        for (int i = 0; i < currentlyWeatherList.size(); i++) {
            CurrentlyWeather weather = currentlyWeatherList.get(i);

            LatLng latLng = new LatLng(weather.getLat(), weather.getLng());
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(CurrentlyWeather.getIconId(weather.getIcon())))
                    .position(latLng)
                    .title(weather.getCity())
            );
        }
    }
}
