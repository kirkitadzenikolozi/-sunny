package com.druidstudio.sunny.ui.activities;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.druidstudio.sunny.R;
import com.druidstudio.sunny.ui.fragments.CurrentlyForecastFragment;
import com.druidstudio.sunny.ui.fragments.ForecastMapFragment;
import com.druidstudio.sunny.ui.widget.SlidingTabLayout;
import com.druidstudio.sunny.util.IntentUtils;

/**
 * Created by kirkita on 06.02.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private OurViewPagerAdapter ourViewPagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);

        ourViewPagerAdapter = new OurViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(ourViewPagerAdapter);

        // it's PagerAdapter set.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, R.id.view_pager_tab_text);

        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.material_grey_300));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.animate();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search_forecast:
                searchCityForecastDialog();
                //IntentUtils.searchCityForecast(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchCityForecastDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Search city forecast")
                .setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.search_city_forecast_dialog_layout, null);

        final AutoCompleteTextView searchInputATV = (AutoCompleteTextView) dialogLayout.findViewById(R.id.search_city_forecast_dialog_layout_atv_search_input);
        String[] citiesArray = getResources().getStringArray(R.array.cities);
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, citiesArray);
        searchInputATV.setAdapter(adapter);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CurrentlyForecastFragment.searchCityForecast(MainActivity.this, searchInputATV.getText().toString().trim());
            }
        });


        builder.setView(dialogLayout);

        // Dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchInputATV, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();
    }

    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        private String[] TOP_TITLES = getResources().getStringArray(R.array.pager_titles);

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = null;

            final int pattern = position % 5;
            switch (position) {
                case 0:
                    f = new CurrentlyForecastFragment();
                    break;
                case 1:
                    f = new ForecastMapFragment();
                    break;
                default:
                    Log.e("FRAG: ", "default");
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            return TOP_TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TOP_TITLES[position];
        }
    }

}
