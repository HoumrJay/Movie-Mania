package com.example.pavol.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pavol.popularmovies.adapters.FragmentAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.guide_text)
    TextView guideText;
    @BindView(R.id.helper_relative_layout)
    RelativeLayout helperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        guideText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guideText.setVisibility(View.GONE);
                helperLayout.setBackgroundColor(Color.parseColor("#00000000"));
            }
        });

        FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        appContext = getApplicationContext();

    }

    /* A simple menu that explains some less obvious functions of the app*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.help_button);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                guideText.setVisibility(View.VISIBLE);
                helperLayout.setBackgroundColor(Color.parseColor("#BF000000"));
                return false;
            }
        });

        return true;
    }
}
