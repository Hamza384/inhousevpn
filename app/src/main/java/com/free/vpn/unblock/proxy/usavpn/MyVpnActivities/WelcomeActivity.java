package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;


import com.free.vpn.unblock.proxy.usavpn.R;

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
                    .image(R.drawable.ic_secure)
                    .title("Safe and Secure")
                    .description("QuickVPN is Very Fast & Secure.Easy to Use")
                    .build());
            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.blue)
                    .image(R.drawable.iv_security_secure)
                    .title("Premium Features")
                    .description("Get Premium Servers and enjoy your Security")
                    .build());

            addSlide(new SlideFragmentBuilder()
                    .backgroundColor(R.color.colorPrimaryDark)
                    .buttonsColor(R.color.blue)
                    .image(R.drawable.iv_privacy_secure)
                    .title("Private Surfing")
                    .description("With QuickVPN you can Anonymously Surf")
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

