package gjjzx.com.udpsocketdemo.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by PC on 2017/11/9.
 */

public class MyApplication extends Application {

    private static Context context;

//    public static final String DSTIP = "192.168.1.1";
    public static final String DSTIP = "10.1.75.252";
    public static final int DSTPORT = 5000;

//    public static boolean isListening = false;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
