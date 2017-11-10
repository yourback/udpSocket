package gjjzx.com.udpsocketdemo.util;

import org.litepal.crud.DataSupport;

import java.util.List;

import gjjzx.com.udpsocketdemo.bean.Msg;

/**
 * Created by PC on 2017/11/10.
 */

public class LocalSQL {
    public static List<Msg> getLastMsgList() {
        return DataSupport.order("currentTime").find(Msg.class);
    }

    public static List<Msg> addMsg(Msg msg) {
        msg.save();
        return getLastMsgList();
    }
}
