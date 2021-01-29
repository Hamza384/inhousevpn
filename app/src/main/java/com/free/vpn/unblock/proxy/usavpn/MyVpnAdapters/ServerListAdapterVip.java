package com.free.vpn.unblock.proxy.usavpn.MyVpnAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.free.vpn.R;
import com.free.vpn.unblock.proxy.usavpn.MyVpnActivities.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ServerListAdapterVip extends RecyclerView.Adapter<ServerListAdapterVip.mViewhoder> {

    ArrayList<Country> countryArrayList;
    private final Context mContext;
    RemainingTraffic remainingTraffic;
    public ServerListAdapterVip(ArrayList<Country> countryArrayList, Context ctx) {
        this.countryArrayList = countryArrayList;
        this.mContext =ctx;
    }

    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.vip_server_list_item,parent,false);
        mViewhoder mvh=new mViewhoder(item);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position)
    {
        remainingTraffic =new RemainingTraffic();
        Country data= countryArrayList.get(position);
        Locale locale=new Locale("",data.getCountry());
        holder.flag.setImageResource(mContext.getResources().getIdentifier("drawable/" +data.getCountry().toLowerCase(),null, mContext.getPackageName()));
        holder.app_name.setText(locale.getDisplayCountry());
        holder.limit.setText("VIP");
        holder.limit.setTextColor(mContext.getResources().getColor(R.color.primary));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("c",data.getCountry());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryArrayList.size();
    }

    public static class mViewhoder extends RecyclerView.ViewHolder
    {
        TextView app_name,limit;
        ImageView flag;

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
}
