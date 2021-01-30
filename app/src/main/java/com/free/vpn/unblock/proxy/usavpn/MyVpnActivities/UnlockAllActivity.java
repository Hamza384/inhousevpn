package com.free.vpn.unblock.proxy.usavpn.MyVpnActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.free.vpn.unblock.proxy.usavpn.Config;
import com.free.vpn.unblock.proxy.usavpn.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UnlockAllActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private final MutableLiveData<Integer> all_check = new MutableLiveData<>();
    @BindView(R.id.one_month)
    RadioButton oneMonth;
    @BindView(R.id.three_month)
    RadioButton threeMonth;
    @BindView(R.id.six_month)
    RadioButton sixMonth;
    @BindView(R.id.one_year)
    RadioButton oneYear;
    Toolbar mToolbar;
    FirebaseAnalytics mFirebaseAnalytics;
    Context mContext;
    androidx.appcompat.app.AlertDialog alertDialog;
    private BillingProcessor billingProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_all);
        mContext = UnlockAllActivity.this;
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        Constants.sendAnalytics(mFirebaseAnalytics, "UnlockAll Activity");


        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
            mToolbar.setTitle("Premium Activity");
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        all_check.setValue(-1);
        all_check.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 0:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 1:
                        oneMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 2:
                        threeMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 3:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        break;

                }
            }
        });
        billingProcessor = new BillingProcessor(this, Config.IAP_LISENCE_KEY, this);
        billingProcessor.initialize();
        oneMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(0);
            }
        });
        threeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(1);
            }
        });
        sixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(2);
            }
        });
        oneYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(3);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
    }


    private void unlock_all(int i) {
        switch (i) {
            case 0:
                billingProcessor.subscribe(UnlockAllActivity.this, Config.all_month_id);
                break;
            case 1:
                billingProcessor.subscribe(UnlockAllActivity.this, Config.all_threemonths_id);
                break;
            case 2:
                billingProcessor.subscribe(UnlockAllActivity.this, Config.all_sixmonths_id);
                break;
            case 3:
                billingProcessor.subscribe(UnlockAllActivity.this, Config.all_yearly_id);
                break;
        }
    }

    @OnClick(R.id.all_pur)
    void unlockAll() {
        /*if(all_check.getValue() != null)unlock_all(all_check.getValue());*/
        rateUsDialog(R.layout.dialog_premium);

    }

    private void rateUsDialog(int layout) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(layout, null);
        Button exitButton = layoutView.findViewById(R.id.btnExit);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ServersActivity.class);
                startActivity(intent);
            }
        });
    }
}
