package cz.com.a360city;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

    public static MyApp myApp = null;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MyApp getInstance(){
        if(myApp == null)
        {
            myApp = new MyApp();
        }
        return myApp;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);

    }
}
