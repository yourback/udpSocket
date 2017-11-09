package gjjzx.com.udpsocketdemo.util;

import android.widget.Toast;

import gjjzx.com.udpsocketdemo.app.MyApplication;


/**
 * Created by PC on 2017/11/9.
 */

public class ToastUtil {

    private static Toast toast;
    public static void show(String str) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getContext(), str, Toast.LENGTH_LONG);

        } else {
            toast.setText(str);
        }
        toast.show();
    }
}
