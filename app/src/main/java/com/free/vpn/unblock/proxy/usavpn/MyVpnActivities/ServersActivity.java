package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.free.vpn.unblock.proxy.usavpn.R;
import com.google.android.material.tabs.TabLayout;
import com.free.vpn.unblock.proxy.usavpn.MyVpnAdapters.TabsAdapter;
import com.free.vpn.unblock.proxy.usavpn.MyVpnFragments.FreeServerFragment;
import com.free.vpn.unblock.proxy.usavpn.MyVpnFragments.VIPServerFragment;
import com.google.firebase.analytics.FirebaseAnalytics;



public class ServersActivity extends AppCompatActivity {

    private TabsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button btnFreeServer, btnVipServer;
    Toolbar mToolbar;
    FirebaseAnalytics mFirebaseAnalytics;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        mContext = ServersActivity.this;

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabsAdapter(getSupportFragmentManager());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        Constants.sendAnalytics(mFirebaseAnalytics, "Server Activity");

        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
            mToolbar.setTitle("Server Activity");
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        btnFreeServer = findViewById(R.id.btnFreeServer);
        btnVipServer = findViewById(R.id.btnVipServer);
        adapter.addFragment(new FreeServerFragment(), "Free Server");
        adapter.addFragment(new VIPServerFragment(), "Vip Server");



        btnFreeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnVipServer.setBackgroundResource(R.drawable.button_round_download);
                    btnFreeServer.setBackgroundResource(R.drawable.button_round_history_notpressed);
                } catch (Resources.NotFoundException e) {
                    e.getMessage();
                }


                btnFreeServer.setTextColor(getResources().getColor(R.color.white));
                btnVipServer.setTextColor(getResources().getColor(R.color.blue));
                viewPager.setCurrentItem(0);
            }
        });

        btnVipServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnVipServer.setBackgroundResource(R.drawable.button_round_history_notpressed);
                    btnFreeServer.setBackgroundResource(R.drawable.button_round_download);
                } catch (Resources.NotFoundException e) {
                    e.getMessage();
                }
                btnVipServer.setTextColor(getResources().getColor(R.color.white));
                btnFreeServer.setTextColor(getResources().getColor(R.color.blue));
                viewPager.setCurrentItem(1);
            }
        });


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
