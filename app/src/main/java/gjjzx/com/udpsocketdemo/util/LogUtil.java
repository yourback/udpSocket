package gjjzx.com.udpsocketdemo.util;

import android.util.Log;

import gjjzx.com.udpsocketdemo.app.MyApplication;

/**
 * Created by PC on 2017/11/9.
 */

public class LogUtil {

    private static final String TAG = "客户端：";

    public static void e(Object o) {
        if (MyApplication.isLog)
            Log.e(TAG, o.toString());
    }
}
