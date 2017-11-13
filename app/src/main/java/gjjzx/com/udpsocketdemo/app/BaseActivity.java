package gjjzx.com.udpsocketdemo.app;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gjjzx.com.udpsocketdemo.util.LogUtil;

/**
 * Created by PC on 2017/11/13.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        LogUtil.e(getClass().getSimpleName());
    }
}
