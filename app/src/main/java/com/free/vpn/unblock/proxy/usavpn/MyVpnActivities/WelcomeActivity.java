package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.free.vpn.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class WelcomeActivity extends MaterialIntroActivity {
    SharedPreferences sharedPreferences;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WelcomeActivity.this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!sharedPreferences.getBoolean("isFirstTime", true)) {
            onFinish();
        } else {
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.blue)
                    .image(R.drawable.intro_one)
                    .title("Secure VPN Servers Activity")
                    .description("Premium VPN App is Very Fast & Secure. And Easy to Use")
                    .build());
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.blue)
                    .image(R.drawable.intro_two)
                    .title("Use Premium ")
                    .description("Buy Premium Servers Activity and get more Secure ServersActivity ")
                    .build());

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.blue)
                    .image(R.drawable.intro_two)
                    .title("Use Premium ")
                    .description("Buy Premium Servers Activity and get more Secure ServersActivity ")
                    .build());

        }
    }

    @Override
    public void onFinish() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();
        super.onFinish();
    }
}

