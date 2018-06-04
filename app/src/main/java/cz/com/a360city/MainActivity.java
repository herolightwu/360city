package cz.com.a360city;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler{

    LinearLayout lnr_demo, lnr_content, lnr_youtube, lnr_maps, lnr_photo, lnr_community;
    TextView tv_username, tv_email;

    String package_name = "cz.com.a360city";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private BillingProcessor bp;
    Boolean bPaid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        tv_username = (TextView) headerLayout.findViewById(R.id.tv_username);
        tv_email = (TextView) headerLayout.findViewById(R.id.tv_email);

        sharedPreferences = getSharedPreferences(package_name , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initLayout();

        bp = new BillingProcessor(this, Constants.LICENSE_KEY, this);//bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, this);
        try {
            if(bp.loadOwnedPurchasesFromGoogle()) {
                if (bp.isPurchased(Constants.PRODUCT_ID)) {
                    editor.putString("full_content", "1");
                    editor.commit();
                    bPaid =true;
                } else {
                    editor.putString("full_content", "0");
                    editor.commit();
                    bPaid = false;
                }
            } else {
                editor.putString("full_content", "0");
                editor.commit();
                bPaid = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLayout(){
        lnr_demo = (LinearLayout) findViewById(R.id.lnr_demo);
        lnr_content = (LinearLayout) findViewById(R.id.lnr_purchase);
        lnr_youtube = (LinearLayout) findViewById(R.id.lnr_youtube);
        lnr_maps = (LinearLayout) findViewById(R.id.lnr_maps);
        lnr_photo = (LinearLayout) findViewById(R.id.lnr_photo);
        lnr_community = (LinearLayout) findViewById(R.id.lnr_community);

        lnr_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", Constants.DEMO_LINK);
                startActivity(intent);
            }
        });

        lnr_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bPaid){
                    Intent i = new Intent(MainActivity.this, WebActivity.class);
                    i.putExtra("URL", Constants.CONTENT_LINK);
                    startActivity(i);
                } else{
                    Toast.makeText(getApplicationContext(), "You can not access full content. Please purchase product", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, FullContentActivity.class);
                    startActivity(intent);
                }
            }
        });

        lnr_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", Constants.YOUTUBE_LINK);
                startActivity(intent);
            }
        });

        lnr_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", Constants.ADDITION_OPTIONAL_LINK1);
                startActivity(intent);
            }
        });

        lnr_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
                startActivity(intent);
            }
        });

        lnr_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("URL", Constants.COMMUNITY_LINK);
                startActivity(intent);
            }
        });

        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        if(username.length() > 0){
            tv_username.setText(username);
            tv_email.setText(email);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        if(username.length() > 0){
            tv_username.setText(username);
            tv_email.setText(email);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_register) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            sendEmail();

        } else if (id == R.id.nav_linkone) {
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("URL", Constants.ADDITION_OPTIONAL_LINK1);
            startActivity(intent);

        } else if (id == R.id.nav_linktwo) {
            Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_facebook) {
            //openApp(MainActivity.this, "Facebook", "com.facebook.katana");
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("URL", Constants.FACEBOOK_LINK);
            startActivity(intent);

        } else if (id == R.id.nav_instagram) {
            //openApp(MainActivity.this, "Instagram", "com.instagram.android");
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            intent.putExtra("URL", Constants.INSTAGRAM_LINK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendEmail()
    {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("plain/text");//"message/rfc822"

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"westlog.anrodev@gmail.com"});

        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback For App");

        intent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(intent, "Send Email"));

    }

    public static void openApp(Context context, String appName, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName))
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
            else Toast.makeText(context, appName + " app is not enabled.", Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, appName + " app is not installed.", Toast.LENGTH_SHORT).show();
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        if (bp.isPurchased(Constants.PRODUCT_ID)) {
            editor.putString("full_content", "1");
            editor.commit();
        }
    }
    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "onBillingError: " + Integer.toString(errorCode), Toast.LENGTH_LONG);
    }
    @Override
    public void onBillingInitialized() {
        //showToast("onBillingInitialized");
        //readyToPurchase = true;
    }
    @Override
    public void onPurchaseHistoryRestored() {
        //showToast("onPurchaseHistoryRestored");
        //for(String sku : bp.listOwnedProducts())
        //    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
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
