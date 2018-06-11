package cz.com.a360city;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private WebView mywebview;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        url = getIntent().getStringExtra("URL");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mywebview = (WebView) findViewById(R.id.mywebview);
        //===== webview setting ======================
        mywebview.getSettings().setLoadsImagesAutomatically(true);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setUseWideViewPort(true);
        mywebview.getSettings().setLoadWithOverviewMode(true);
        mywebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        mywebview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        mywebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebview.setWebChromeClient( new WebChromeClient());
        mywebview.setWebViewClient(new WebViewClient());
        mywebview.getSettings().setBuiltInZoomControls(true);
        mywebview.loadUrl(url);
        //String frameVideo = "<html><body>Youtube video .. <br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        //veryVeryVery important for playing the videos!
        //mywebview.loadDataWithBaseURL(url, frameVideo, "text/html", "UTF-8", null);


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
}
