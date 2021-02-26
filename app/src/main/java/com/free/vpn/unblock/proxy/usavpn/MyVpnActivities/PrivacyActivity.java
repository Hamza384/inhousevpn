package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.free.vpn.unblock.proxy.usavpn.R;
import com.google.firebase.analytics.FirebaseAnalytics;




public class PrivacyActivity extends AppCompatActivity {


    Toolbar mToolbar;
    TextView privacyTxt;
    FirebaseAnalytics mFirebaseAnalytics;
    Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        mContext = PrivacyActivity.this;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        Constants.sendAnalytics(mFirebaseAnalytics, "Privacy Activity");


        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
            mToolbar.setTitle(R.string.privacy_policy);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


        privacyTxt = findViewById(R.id.txtPrivacy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            privacyTxt.setText(Html.fromHtml(getResources().getString(R.string.privacypolicy), Html.FROM_HTML_MODE_COMPACT));
        } else {
            privacyTxt.setText(Html.fromHtml(getResources().getString(R.string.privacypolicy)));
        }


    }

}