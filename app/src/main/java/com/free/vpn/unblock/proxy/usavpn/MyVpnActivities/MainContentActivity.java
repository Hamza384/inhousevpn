package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.partner.api.callback.Callback;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.exceptions.PartnerRequestException;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.free.vpn.unblock.proxy.usavpn.Config;
import com.free.vpn.unblock.proxy.usavpn.MyVpnUtils.LocalFormat;
import com.free.vpn.unblock.proxy.usavpn.MyVpnUtils.MySharePrefs;
import com.free.vpn.unblock.proxy.usavpn.R;
import com.free.vpn.unblock.proxy.usavpn.speed.Speed;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;


public abstract class MainContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    private final Handler mHandler = new Handler();
    private final long mStartRX = 0;
    private final long mStartTX = 0;
    private final int adCount = 0;
    private final Handler myCustomHandler = new Handler();
    private final Handler myUIHandler = new Handler(Looper.getMainLooper());
    private final int UPDATE_REQUEST_CODE = 1011;
    public InterstitialAd mInterstitialAd;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    LottieAnimationView lottieAnimationView;
    boolean vpnToastCheck = true;
    Handler handlerTrafic = null;
    VPNState vpnState;
    int progressBarValue = 0;
    Handler handler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    @BindView(R.id.downloading)
    TextView textDownloading;
    @BindView(R.id.uploading)
    TextView textUploading;
    @BindView(R.id.connection_status)
    TextView t_connection_status;
    @BindView(R.id.connection_status_image)
    ImageView i_connection_status_image;
    @BindView(R.id.vpn_details)
    ImageView vpn_detail_image;
    @BindView(R.id.tv_timer)
    TextView timerTextView;
    @BindView(R.id.connect_btn)
    ImageView connectBtnTextView;
    @BindView(R.id.connection_state)
    TextView connectionStateTextView;
    @BindView(R.id.flag_image)
    ImageView imgFlag;
    @BindView(R.id.rcv_free)
    RecyclerView rcvFree;
    @BindView(R.id.flag_name)
    TextView flagName;
    @BindView(R.id.footer)
    RelativeLayout footer;
    Context mContext;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    androidx.appcompat.app.AlertDialog alertDialog;
    FirebaseAnalytics mFirebaseAnalytics;
    Button btnCrash;
    private long startTime = 0L;
    private final Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerTextView.setText(String.format("%02d", hrs) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            myCustomHandler.postDelayed(this, 0);
        }

    };
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            myUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };
    //admob native advance)
    private UnifiedNativeAd nativeAd;
    private String STATUS;
    private DrawerLayout drawer;
    private long mLastRxBytes = 0;
    private long mLastTxBytes = 0;
    private long mLastTime = 0;
    private Speed mSpeed;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainContentActivity.this;
        lottieAnimationView = findViewById(R.id.animation_view);


        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        Constants.sendAnalytics(mFirebaseAnalytics, "MainContent Activity");

        /*btnCrash = findViewById(R.id.btnCrash);
        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });*/


        manager = ReviewManagerFactory.create(this);
        callInAppUpdate();
        Constants.setAlarmEveryDay(mContext);


        final RippleBackground rippleBackground = findViewById(R.id.content);
        rippleBackground.startRippleAnimation();


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        vpn_detail_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServerList();
            }
        });


        if (getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {
            // Initialize the Mobile Ads SDK.

            MobileAds.initialize(MainContentActivity.this, getString(R.string.admob_appid));

            //interstitial
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_intersitail));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }

        if (Prefs.contains("connectStart") && Prefs.getString("connectStart", "").equals("on")) {

            isConnected(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean aBoolean) {
                    if (aBoolean) {
                        STATUS = "Disconnect";

                        if (mInterstitialAd.isLoaded()) {
                            /*mInterstitialAd.show();*/
                            disconnectAlert();
                        } else {
                            disconnectAlert();
                        }
                    } else {

                        STATUS = "Connect";

                        if (mInterstitialAd.isLoaded()) {
//                        Interstitial Ad loaded successfully...
                            /*mInterstitialAd.show();*/
                            updateUI();
                            connectToVpn();
                        } else {

                            updateUI();
                            connectToVpn();
                        }
                    }
                }

                @Override
                public void failure(PartnerRequestException error) {
                    Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();

                }

            });
        }

        if (Prefs.contains("noti") && Prefs.getString("noti", "off").equals("off")) {
            OneSignal.setSubscription(false);
        } else
            OneSignal.setSubscription(Prefs.contains("noti") && Prefs.getString("noti", "off").equals("on"));
        handlerTrafic = new Handler();
        handleTraficData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (STATUS == null) return;
        if (STATUS.equals("Connect")) {
            updateUI();
            connectToVpn();
        } else if (STATUS.equals("Disconnect")) {
            disconnectAlert();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            rateUsDialog(R.layout.rate_us_dialog);
        }
    }

    private void rateUsDialog(int layout) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(layout, null);
        Button exitButton = layoutView.findViewById(R.id.btnExit);
        Button closedialogButton = layoutView.findViewById(R.id.btnCloseDialog);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review(true);

            }
        });
        closedialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_rate) {
            rateUs();
        } else if (id == R.id.nav_share) {
            shareApp(Constants.shareSubject, Constants.shareMessage);
        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(mContext, PrivacyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(mContext, AboutActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
//        if the application again available from background vpnState...
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(PartnerRequestException error) {

            }


        });
        callInAppUpdate();
    }

    @Override
    protected void onPause() {
//        application in the background vpnState...
        super.onPause();
        stopUIUpdateTask();
    }

    @Override
    protected void onDestroy() {
        /*if (nativeAd != null) {
            nativeAd.destroy();
        }*/
        super.onDestroy();
    }

    protected abstract void loginToVpn();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {

        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {



                    if(Constants.isFirstTime){
                        Constants.isFirstTime = false;
                        STATUS = "Disconnect";
                        Log.d("lololo", "isFirstTime");
                        if (mInterstitialAd != null) mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                } else {
                                    AdRequest request = new AdRequest.Builder()
                                            .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                            .build();
                                    mInterstitialAd.loadAd(request);
                                }
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                                disconnectAlert();

                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the interstitial ad is closed.
                                disconnectAlert();
                            }
                        });
                        /*disconnectAlert();*/
                        if (getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {

                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                /*disconnectAlert();*/
                            } else {
                                AdRequest request = new AdRequest.Builder()
                                        .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                        .build();
                                mInterstitialAd.loadAd(request);
                                /*disconnectAlert();*/
                            }

                        } else {
                            disconnectAlert();
                        }
                    }else{

                        if (showAdOnCount(MySharePrefs.getInstance(mContext).getIntPref(Constants.KEY_AD_COUNT, 0))) {
                            Log.d(TAG, "success: "+showAdOnCount(MySharePrefs.getInstance(mContext).getIntPref(Constants.KEY_AD_COUNT, 0)));
                            Log.d("lololo", "isFirsttime false");
                            STATUS = "Disconnect";
                            if (mInterstitialAd != null)
                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        // Code to be executed when an ad finishes loading.
                                        if (mInterstitialAd.isLoaded()) {
                                            mInterstitialAd.show();
                                        } else {
                                            AdRequest request = new AdRequest.Builder()
                                                    .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                                    .build();
                                            mInterstitialAd.loadAd(request);
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int errorCode) {
                                        // Code to be executed when an ad request fails.
                                        disconnectAlert();

                                    }

                                    @Override
                                    public void onAdOpened() {
                                        // Code to be executed when the ad is displayed.
                                    }

                                    @Override
                                    public void onAdClicked() {
                                        // Code to be executed when the user clicks on an ad.
                                    }

                                    @Override
                                    public void onAdLeftApplication() {
                                        // Code to be executed when the user has left the app.
                                    }

                                    @Override
                                    public void onAdClosed() {
                                        // Code to be executed when the interstitial ad is closed.
                                        disconnectAlert();
                                    }
                                });
                            /*disconnectAlert();*/
                            if (getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {

                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                    /*disconnectAlert();*/
                                } else {
                                    AdRequest request = new AdRequest.Builder()
                                            .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                            .build();
                                    mInterstitialAd.loadAd(request);
                                    /*disconnectAlert();*/
                                }

                            } else {
                                disconnectAlert();
                            }
                        }else{
                            Log.d("lololo", "else");
                            disconnectAlert();
                        }
                    }









                } else {

                    STATUS = "Connect";

                    if (getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {
//                        Interstitial Ad loaded successfully...
                        /*mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                } else {
                                    AdRequest request = new AdRequest.Builder()
                                            .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                            .build();
                                    mInterstitialAd.loadAd(request);
                                }
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the interstitial ad is closed.
                                updateUI();
                                connectToVpn();
                            }
                        });*/
                        updateUI();
                        connectToVpn();
                        if (mInterstitialAd.isLoaded()) {
                            /*mInterstitialAd.show();*/
                            updateUI();
                            connectToVpn();
                        } else {
                            /*AdRequest request = new AdRequest.Builder()
                                    .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                    .build();
                            mInterstitialAd.loadAd(request);*/
                            updateUI();
                            connectToVpn();
                        }


                    } else {
                        updateUI();
                        connectToVpn();
                    }
                }
            }

            @Override
            public void failure(PartnerRequestException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();

            }


        });

    }

    /*  Different functions defining the vpnState of the vpn
     *  */
    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        myUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        myUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {

        UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                MainContentActivity.this.vpnState = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        loadIcon();
                        connectBtnTextView.setImageResource(R.drawable.ic_connects);
                        connectBtnTextView.setEnabled(true);
                        connectionStateTextView.setText(R.string.disconnected);
                        timerTextView.setVisibility(View.GONE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        textDownloading.setVisibility(View.VISIBLE);
                        textUploading.setVisibility(View.VISIBLE);
                        loadIcon();
                        connectBtnTextView.setEnabled(true);
                        connectBtnTextView.setImageResource(R.drawable.ic_pause);
                        connectionStateTextView.setText(R.string.connected);
                        timer();
                        timerTextView.setVisibility(View.VISIBLE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
                        connectionStateTextView.setText(R.string.connecting);
                        connectBtnTextView.setEnabled(true);
                        timerTextView.setVisibility(View.GONE);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        t_connection_status.setText("Not Selected");
                        connectionStateTextView.setText(R.string.paused);
                        break;
                    }
                    default: {

                    }

                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            //            try to connect to current vpn server...
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void failure(PartnerRequestException error) {

            }


        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
//        try to update the traffic vpnState of the vpn...
        String outString = LocalFormat.easyRead(outBytes, false);
        String inString = LocalFormat.easyRead(inBytes, false);

    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
        } else {
            String trafficUsed = LocalFormat.byteCounter(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = LocalFormat.byteCounter(remainingTrafficResponse.getTrafficLimit()) + "Mb";

        }
    }

    protected void showConnectProgress() {
//        Updating progressbar
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (vpnState == VPNState.CONNECTING_VPN || vpnState == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void hideConnectProgress() {
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(MainContentActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    protected void timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis();
            myCustomHandler.postDelayed(updateTimerThread, 0);
            timeSwapBuff += timeInMilliseconds;

        }
    }

    protected void loadIcon() {
        if (vpnState == VPNState.IDLE) {
            t_connection_status.setText("Not Selected");

        } else if (vpnState == VPNState.CONNECTING_VPN || vpnState == VPNState.CONNECTING_CREDENTIALS) {
            connectBtnTextView.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
        } else if (vpnState == VPNState.CONNECTED) {
            connectBtnTextView.setVisibility(View.VISIBLE);
            t_connection_status.setText("Selected");
            lottieAnimationView.setVisibility(View.GONE);
            if (vpnToastCheck == true) {
                Toasty.success(MainContentActivity.this, "Server Connected", Toast.LENGTH_SHORT).show();
                vpnToastCheck = false;
            }

        }
    }

    protected void disconnectAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to disconnect?");
        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        disconnectFromVnp();
                        vpnToastCheck = true;
                        dialog.dismiss();

                        Toasty.success(MainContentActivity.this, "Server Disconnected", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toasty.success(MainContentActivity.this, "VPN Remains Connected", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    //loading native ad

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
        VideoController vc = nativeAd.getVideoController();
        if (vc.hasVideoContent()) {

            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        } else {
        }
    }


    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */


    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                RelativeLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.native_ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.w("asdsadsad", "ads" + errorCode);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder()
                .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                .build());
    }


    @OnClick(R.id.purchase_layout)
    void goPurchase() {
        startActivity(new Intent(this, UnlockAllActivity.class));
    }

    @OnClick(R.id.btnServerList)
    void showServerList() {
        startActivity(new Intent(this, ServersActivity.class));
    }

    @OnClick(R.id.category)
    void openSideNavigation() {
        drawer.openDrawer(GravityCompat.START, true);
    }

    private void handleTraficData() {

        if (handlerTrafic == null)
            return;


        handlerTrafic.postDelayed(this::setTraficData, 1000);


    }

    private void setTraficData() {
        long currentRxBytes = TrafficStats.getTotalRxBytes();

        long currentTxBytes = TrafficStats.getTotalTxBytes();

        long usedRxBytes = currentRxBytes - mLastRxBytes;
        long usedTxBytes = currentTxBytes - mLastTxBytes;
        long currentTime = System.currentTimeMillis();
        long usedTime = currentTime - mLastTime;

        mLastRxBytes = currentRxBytes;
        mLastTxBytes = currentTxBytes;
        mLastTime = currentTime;

        mSpeed = new Speed(this);
        mSpeed.calcSpeed(usedTime, usedRxBytes, usedTxBytes);
        if (vpnState != null && mSpeed != null && mSpeed.up != null && mSpeed.down != null && vpnState.equals(VPNState.CONNECTED)) {
            textDownloading.setText(mSpeed.down.speedValue + " " + mSpeed.down.speedUnit);
            textUploading.setText(mSpeed.up.speedValue + " " + mSpeed.up.speedUnit);
        } else {
            textDownloading.setText("0  " + mSpeed.down.speedUnit);
            textUploading.setText("0  " + " " + mSpeed.up.speedUnit);
        }

        handleTraficData();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void updateSubscription() {
        if (getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {
            //native
            Log.d(TAG, "onStart----: ");
            refreshAd();
            //interstitital
            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(int i) {

                    if (STATUS.equals("Connect")) {
                        updateUI();
                        connectToVpn();
                    } else if (STATUS.equals("Disconnect")) {
                        disconnectAlert();
                    }
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();

                    if (STATUS.equals("Connect")) {
                        updateUI();
                        connectToVpn();
                    } else if (STATUS.equals("Disconnect")) {
                        disconnectAlert();
                    }
                }
            });
        } else {
            Log.e(TAG, "onStart: ");
        }

    }

    private void Review(boolean isBackPressed) {
        manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(MainContentActivity.this, reviewInfo);
                    flow.addOnFailureListener(new com.google.android.play.core.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (isBackPressed) {
                                finish();
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void callInAppUpdate() {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(mContext);
        /*AppUpdateManager appUpdateManager = new AppUpdateManagerFactory(mContext);*/

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainContentActivity.this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (resultCode == UPDATE_REQUEST_CODE) {
                Toast.makeText(mContext, "Update Started", Toast.LENGTH_SHORT).show();
                if (resultCode != RESULT_OK) {
                    Log.d("TAG", "Update flow failed! Result code: " + resultCode);
                } else {
                }

            }
        }
    }

    public void shareApp(String subject, String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

        private boolean showAdOnCount(int count) {
        Log.d("lololo", "showAdOnCount: "+count);
        if (count >= 1) {
            MySharePrefs.getInstance(mContext).savePref(Constants.KEY_AD_COUNT, 0);
            return true;
        } else {
            MySharePrefs.getInstance(mContext).savePref(Constants.KEY_AD_COUNT, count+1);
            return false;
        }

    }




}
