package cz.com.a360city;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class FullContentActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    final String LOG_TAG = "360 Moscow";



    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    LinearLayout lnr_purchase, lnr_feedback;

    String package_name = "cz.com.a360city";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_content);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = getSharedPreferences(package_name , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initLayout();

        bp = new BillingProcessor(this, Constants.LICENSE_KEY, this);//bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, this);
        try {
            if(bp.loadOwnedPurchasesFromGoogle()) {
                if (bp.isPurchased(Constants.PRODUCT_ID)) {
                    editor.putString("full_content", "1");
                    editor.commit();
                } else {
                    editor.putString("full_content", "0");
                    editor.commit();
                }
            } else {
                editor.putString("full_content", "0");
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initLayout(){
        lnr_purchase = (LinearLayout) findViewById(R.id.lnr_purchase);
        lnr_feedback = (LinearLayout) findViewById(R.id.lnr_feedback);

        lnr_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        lnr_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sb_str = sharedPreferences.getString("full_content", "");
                if(!sb_str.equals("1"))
                    buySubscribe();
            }
        });
    }

    public void sendEmail()
    {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("plain/text");//"message/rfc822"

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"westlog.anrodev@gmail.com"});

        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback For Full Content");

        intent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(intent, "Send Email"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void buySubscribe(){
        if (!readyToPurchase) {
            Snackbar.make(getWindow().getDecorView(),"Billing not initialized.", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }
        bp.purchase(this, Constants.PRODUCT_ID);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        if (bp.isPurchased(Constants.PRODUCT_ID)) {
            editor.putString("full_content", "1");
            editor.commit();
            Intent i = new Intent(FullContentActivity.this, WebActivity.class);
            i.putExtra("URL", Constants.CONTENT_LINK);
            startActivity(i);
        }
    }
    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "onBillingError: " + Integer.toString(errorCode), Toast.LENGTH_LONG);
    }
    @Override
    public void onBillingInitialized() {
        //showToast("onBillingInitialized");
        readyToPurchase = true;
    }
    @Override
    public void onPurchaseHistoryRestored() {
        //showToast("onPurchaseHistoryRestored");
        for(String sku : bp.listOwnedProducts())
            Log.d(LOG_TAG, "Owned Managed Product: " + sku);
        //for(String sku : bp.listOwnedSubscriptions())
        //    Log.d(LOG_TAG, "Owned Subscription: " + sku);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
