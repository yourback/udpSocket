package gjjzx.com.udpsocketdemo.app;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by PC on 2017/11/9.
 */

public class MyApplication extends Application {

    public static final boolean isLog = true;

    public static final String DSTIP = "10.1.75.252";
    public static final int DSTPORT = 5000;

    public static final int LOCALPORT = 7777;

    public static boolean isListening = false;


    private static Context context;
    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();
    }
}
