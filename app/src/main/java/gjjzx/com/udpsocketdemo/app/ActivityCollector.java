package gjjzx.com.udpsocketdemo.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2017/11/13.
 */

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing())
                activity.finish();
        }
        activities.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
