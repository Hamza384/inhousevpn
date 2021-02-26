package com.free.vpn.unblock.proxy.usavpn;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.UnifiedSDKConfig;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.free.vpn.unblock.proxy.usavpn.MyVpnUtils.LifecycleHandler;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;


public class App extends MultiDexApplication {


    private UnifiedSDK unifiedSDK;
    public FirebaseAnalytics mFirebaseAnalytics;
    public static App appInstance;
    public PublisherInterstitialAd mInterstitialAd;
    public PublisherAdRequest ins_adRequest;

    public static synchronized App getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        appInstance = this;
        LoadAds();
        Log.d("lololo", "onCreate: ");
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_intersitail));
        Log.d("lololo", "Mobile Ads: ");
        registerActivityLifecycleCallbacks(new LifecycleHandler());

        //Prefs lib
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        //  OneSignal Initialization

        /*OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/

        hydraInit();
    }

    public void hydraInit() {
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl("https://backend.northghost.com")
                .carrierId("1212_demotest")  // Put Your Own Carrier Id
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .build();

        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);
        UnifiedSDKConfig config = UnifiedSDKConfig.newBuilder().idfaEnabled(false).build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo, config);
        UnifiedSDK.update(notificationConfig);
        UnifiedSDK.setLoggingLevel(Log.VERBOSE);
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        hydraInit();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void LoadAds() {

        try {



            mInterstitialAd = new PublisherInterstitialAd(this);

            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit));

            ins_adRequest = new PublisherAdRequest.Builder()
                    .build();

            mInterstitialAd.loadAd(ins_adRequest);
            Log.d("lololo", "Ad Loaded");
        } catch (Exception e) {
            e.getMessage();
            Log.d("lololo", "LoadAds: "+e.getMessage());
        }
    }

    public boolean requestNewInterstitial() {

        try {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLoaded() {

        try {
            if (mInterstitialAd.isLoaded() && mInterstitialAd != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




}
