package com.free.vpn.unblock.proxy.usavpn.MyVpnAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.facebook.ads.Ad;
import com.free.vpn.unblock.proxy.usavpn.App;
import com.free.vpn.unblock.proxy.usavpn.Config;
import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.Constants;
import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.MainContentActivity;
import com.free.vpn.unblock.proxy.usavpn.MyVpnUtils.MySharePrefs;
import com.free.vpn.unblock.proxy.usavpn.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.MainActivity;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class ServerListAdapterFree extends RecyclerView.Adapter<ServerListAdapterFree.mViewhoder> {

    ArrayList<Country> datalist = new ArrayList<>();
    private final Context context;
    RemainingTraffic remainingTrafficResponse;
    private final int AD_TYPE = 0;
    private final int CONTENT_TYPE = 1;
    InterstitialAd mInterstitialAd;
    public ServerListAdapterFree( Context ctx) {
        this.context=ctx;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        AdView adview;
        if (viewType == AD_TYPE) {
            adview = new AdView(context);
            adview.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adview.setAdUnitId(context.getString(R.string.banner_ads_id));
            adview.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_layout_banner));
            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.MEDIUM_RECTANGLE.getHeight() * density);
//            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,height + 50);
            adview.setLayoutParams(params);

            AdRequest request = new AdRequest.Builder().build();
            adview.loadAd(request);
            return new mViewhoder(adview);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.server_list_free, parent, false);
            return new mViewhoder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position) {

        MobileAds.initialize(context, context.getString(R.string.admob_appid));

        //interstitial
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.admob_intersitail));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(getItemViewType(position) == CONTENT_TYPE){
            remainingTrafficResponse=new RemainingTraffic();
            Country data=datalist.get(position);
            Locale locale=new Locale("",data.getCountry());
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/"+data.getCountry().toLowerCase(),null,context.getPackageName()));
            holder.app_name.setText(locale.getDisplayCountry());
            holder.limit.setImageResource(R.drawable.ic_signals);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                            Intent intent=new Intent(context, MainActivity.class);
                            intent.putExtra("c",data.getCountry());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);

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
                            Intent intent=new Intent(context, MainActivity.class);
                            intent.putExtra("c",data.getCountry());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    if (context.getResources().getBoolean(R.bool.ads_switch) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();

                        } else {
                            AdRequest request = new AdRequest.Builder()
                                    .addTestDevice("91b511f6-d4ab-4a6b-94fa-e538dfbee85f")
                                    .build();
                            mInterstitialAd.loadAd(request);

                        }

                    } else {
                        Intent intent=new Intent(context, MainActivity.class);
                        intent.putExtra("c",data.getCountry());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public int getItemViewType(int position) {
        return datalist.get(position) ==null? AD_TYPE:CONTENT_TYPE;
    }

    public static class mViewhoder extends RecyclerView.ViewHolder
    {
        TextView app_name;
        ImageView flag,limit;


        public mViewhoder(View itemView) {
            super(itemView);
            app_name=itemView.findViewById(R.id.region_title);
             limit=itemView.findViewById(R.id.region_limit);
             flag=itemView.findViewById(R.id.country_flag);
        }
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Country item);
    }
    public void setData(List<Country> servers) {
        datalist.clear();
        datalist.addAll(servers);
        notifyDataSetChanged();
    }


}
