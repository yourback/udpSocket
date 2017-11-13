package gjjzx.com.udpsocketdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.lemonsoft.lemonbubble.LemonBubble;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gjjzx.com.udpsocketdemo.app.ActivityCollector;
import gjjzx.com.udpsocketdemo.app.BaseActivity;
import gjjzx.com.udpsocketdemo.app.MyApplication;
import gjjzx.com.udpsocketdemo.bean.Msg;
import gjjzx.com.udpsocketdemo.diy.MsgAdapter;
import gjjzx.com.udpsocketdemo.util.LocalSQL;
import gjjzx.com.udpsocketdemo.util.LogUtil;
import gjjzx.com.udpsocketdemo.util.ToastUtil;


public class MainActivity extends BaseActivity {


    private SocketConn sc;

    private EditText etdata;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private List<Msg> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtil.e("oncreate");

        initMsg();

        findView();

        initSocket();

        sc.ReceiveServerSocketData();
    }

    private void initMsg() {
        Message msg = new Message();
        msg.obj = "正在拉取消息列表\n请稍后...";
        msg.what = WAITING;
        UIhandler.sendMessage(msg);

        msgList = LocalSQL.getLastMsgList();


    }

    private void findView() {
        etdata = findViewById(R.id.et_data);

        msgRecyclerView = findViewById(R.id.msg_recyclerview);
        LinearLayoutManager ll = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(ll);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        refreshRecyclerview();
    }

    private void initSocket() {
        sc = new SocketConn();

        sc.setListener(new SocketConn.onSocketListener() {
            @Override
            public void onBuildSendSocketFail() {
                UIhandler.sendEmptyMessageDelayed(FAIL_SENDSOCKETBUILD, 1000);
            }

            @Override
            public void onSendDataFail() {
                UIhandler.sendEmptyMessageDelayed(FAIL_SENDDATA, 1000);
            }

            @Override
            public void onReturnDataOutTime() {
                UIhandler.sendEmptyMessageDelayed(FAIL_RETURNDATAOUTTIME, 1000);
            }


            @Override
            public void onReturnDataSuccess(String rd) {
                Message msg = new Message();
                msg.what = SUCCESS_RETURNDATA;
                msg.obj = rd;
                UIhandler.sendMessageDelayed(msg, 1000);
            }


//      ------------------------------------上面是发送数据-------------------------------------------------------
//      ------------------------------------下面是接收服务器推送的消息-------------------------------------------

            @Override
            public void onBuildRecvSocketSuccess() {
                UIhandler.sendEmptyMessageDelayed(SUCCESS_RECVSOCKETBUILD, 1000);
            }


            @Override
            public void onBuildRecvSocketFail() {
                UIhandler.sendEmptyMessageDelayed(FAIL_RECVSOCKETBUILD, 1000);
            }


            @Override
            public void onReceiveDataSuccess(String rd) {
                Message msg = new Message();
                msg.what = SUCCESS_RECEIVEDATA;
                msg.obj = rd;
                UIhandler.sendMessageDelayed(msg, 1000);
            }

            @Override
            public void onReceiveDataFail() {
                UIhandler.sendEmptyMessageDelayed(FAIL_RECEIVEDATA, 1000);
            }


        });

    }

    public void postdata(View view) {
        String trim = etdata.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.show("消息不能为空");
            etdata.requestFocus();
            return;
        }

        Msg smsg = new Msg(trim, Msg.TYPE_SEDT, System.currentTimeMillis());
        msgList = LocalSQL.addMsg(smsg);

        refreshRecyclerview();

        sc.postData(trim);
        //发送消息后，则等待中..
        Message msg = new Message();
        msg.obj = "消息发送成功\n等待服务器响应中...";
        msg.what = WAITING;
        UIhandler.sendMessage(msg);
    }

    //开启监听
    public void startListener(View view) {
        if (!MyApplication.isListening) {
            //等待中..
            Message msg = new Message();
            msg.obj = "监听开启中...";
            msg.what = WAITING;
            UIhandler.sendMessage(msg);
            sc.ReceiveServerSocketData();
        } else {
            ToastUtil.show("已经开启");
        }
    }

    private static final int WAITING = 10000;
    private static final int FAIL_SENDSOCKETBUILD = 10001;
    private static final int FAIL_SENDDATA = 10002;
    private static final int FAIL_RETURNDATAOUTTIME = 10003;
    private static final int SUCCESS_RETURNDATA = 10004;


    private static final int SUCCESS_RECVSOCKETBUILD = 10008;
    private static final int FAIL_RECVSOCKETBUILD = 10005;
    private static final int SUCCESS_RECEIVEDATA = 10006;
    private static final int FAIL_RECEIVEDATA = 10007;


    private Handler UIhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case WAITING:
                    String title = (String) message.obj;
                    showWaiting(title);
                    break;
                case FAIL_SENDSOCKETBUILD:
                    showFail("建立发送socket出错");
                    break;
                case FAIL_SENDDATA:
                    showFail("发送数据出错");
                    break;
                case FAIL_RETURNDATAOUTTIME:
                    showFail("服务器无响应");
                    break;
                case SUCCESS_RETURNDATA:
                    String data = (String) message.obj;

                    Msg msg = new Msg(data, Msg.TYPE_RECEIVED, System.currentTimeMillis());

                    msgList = LocalSQL.addMsg(msg);

                    refreshRecyclerview();

                    etdata.setText("");
                    showSuccess(data.equals("bb") ? "成功断开连接" : "刷新完毕");
                    break;

//      ------------------------------------上面是发送数据-------------------------------------------------------
//      ------------------------------------下面是接收服务器推送的消息-------------------------------------------

                case SUCCESS_RECVSOCKETBUILD:
                    showSuccess("建立接收socket成功\n推送消息监听中...");
                    break;

                case FAIL_RECVSOCKETBUILD:
                    showFail("建立接收socket出错");
                    break;

                case SUCCESS_RECEIVEDATA:
                    String str = (String) message.obj;

                    Msg rmsg = new Msg(str, Msg.TYPE_RECEIVED, System.currentTimeMillis());

                    msgList = LocalSQL.addMsg(rmsg);

                    refreshRecyclerview();


//                    tvdata.setText("服务器推送消息：" + str);
                    ToastUtil.show("收到服务器推送消息！");
                    break;

                case FAIL_RECEIVEDATA:
                    ToastUtil.show("收到服务器推送消息出错");
                    showFail("收到服务器推送消息出错");
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private void refreshRecyclerview() {

        if (msgList.size() != 0) {
            adapter.refreshList(msgList);
            adapter.notifyItemInserted(msgList.size() - 1);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
        }
    }

    private void showFail(String s) {
        LemonBubble.showError(this, s, 1000);
    }

    private void showSuccess(String s) {
        LemonBubble.showRight(this, s, 1000);
    }

    private void showWaiting(String s) {
        LemonBubble.showRoundProgress(this, s);
    }


    //双击返回键退出
    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            ActivityCollector.finishAll();
        }
    }

}
