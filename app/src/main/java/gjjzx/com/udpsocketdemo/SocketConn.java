package gjjzx.com.udpsocketdemo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import gjjzx.com.udpsocketdemo.app.MyApplication;
import gjjzx.com.udpsocketdemo.util.LogUtil;

/**
 * Created by PC on 2017/11/9.
 */

public class SocketConn {

    private DatagramSocket sendSocket;

    private DatagramSocket recvSocket;

    private onSocketListener listener;

    public void setListener(onSocketListener listener) {
        this.listener = listener;
    }

    public SocketConn() {
    }

    //发送
    public void postData(final String data) {
        //开线程发送
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openSendSecket();

                    //server ip
                    InetAddress serverAddr = InetAddress.getByName(MyApplication.DSTIP);
                    LogUtil.e("发送数据：" + data);
                    LogUtil.e("发送地址：" + MyApplication.DSTIP + ":" + MyApplication.DSTPORT);
                    //发送数据转换成字符数组
                    byte[] bytes = data.getBytes();
                    DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddr, MyApplication.DSTPORT);
                    //发送
                    sendSocket.send(dp);

                    //接收返回值
                    byte[] buffer = new byte[1024];
                    DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        //设置超时时间,3秒
                        sendSocket.setSoTimeout(5000);
                        sendSocket.receive(inPacket);

                        String result = new String(inPacket.getData(), inPacket.getOffset(),
                                inPacket.getLength());

                        if (listener != null)
                            listener.onReturnDataSuccess(result);
                    } catch (Exception e) {
                        //超时处理
                        LogUtil.e("服务器无应答，连接断开");
                        closeSendSocket();
                        if (listener != null)
                            listener.onReturnDataOutTime();
                    }
                } catch (Exception e) {
                    LogUtil.e("发送数据出错");
                    LogUtil.e(e);
                    if (listener != null) {
                        listener.onSendDataFail();
                    }
                }
            }
        }).start();
    }

    //开启发送socket
    private void openSendSecket() {
        if (sendSocket == null || sendSocket.isClosed())
            try {
                //发送和接收消息的接口
                sendSocket = new DatagramSocket();
                LogUtil.e("新建发送接口成功");
            } catch (SocketException e) {
                LogUtil.e(e);
                LogUtil.e("新建发送接口出错");
                if (listener != null) {
                    listener.onBuildSendSocketFail();
                }
            }
    }

    //关闭发送socket
    private void closeSendSocket() {
        if (sendSocket != null && !sendSocket.isClosed()) {
            sendSocket.close();
        }
    }

//    -------------------------------------------以上是发送消息-------------------------------------
//    -------------------------------------------以下是服务器推送消息监听---------------------------


    private byte[] rdata;

    //接收服务器推送消息监听
    public void ReceiveServerSocketData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                openRecvSecket();

                if (MyApplication.isListening) {
                    while (true) {
                        try {
                            rdata = new byte[1024];
                            //参数一:要接受的data 参数二：data的长度
                            DatagramPacket packet = new DatagramPacket(rdata, rdata.length);
                            LogUtil.e("客户端监听中......");
                            recvSocket.receive(packet);
                            //把接收到的data转换为String字符串
                            String result = new String(packet.getData(), packet.getOffset(),
                                    packet.getLength());
                            if (listener != null)
                                listener.onReceiveDataSuccess(result);
                        } catch (Exception e) {
                            LogUtil.e("接收服务器推送消息出错：" + e);
                            if (listener != null)
                                listener.onReceiveDataFail();
                            break;
                        }
                    }
                    LogUtil.e("客户端接收服务器推送消息socket结束");
                    closeRecvSocket();
                }

            }
        }).start();
    }


    //开启接收推送消息socket
    private void openRecvSecket() {
        if (recvSocket == null || recvSocket.isClosed())
            try {
                //监听服务器推送消息端口
                recvSocket = new DatagramSocket(MyApplication.LOCALPORT);
                LogUtil.e("新建接收服务器推送接口成功");
                MyApplication.isListening = true;
                if (listener != null) {
                    listener.onBuildRecvSocketSuccess();
                }
            } catch (SocketException e) {
                LogUtil.e(e);
                LogUtil.e("新建接收服务器推送接口出错");
                if (listener != null) {
                    listener.onBuildRecvSocketFail();
                }
            }
    }

    //关闭接收推送消息socket
    private void closeRecvSocket() {
        if (recvSocket != null && !recvSocket.isClosed()) {
            MyApplication.isListening = false;
            recvSocket.close();
        }
    }


//  ----------------------------------------------以下是接口--------------------------------------------------

    public interface onSocketListener {
        //建立发送socket失败
        void onBuildSendSocketFail();

        //发送数据失败
        void onSendDataFail();

        //发送数据后，成功获得返回值
        void onReturnDataSuccess(String rd);

        //发送数据后，接收不到服务器返回值，超时
        void onReturnDataOutTime();

//    -------------------------------------------以上是发送消息-------------------------------------
//    -------------------------------------------以下是服务器推送消息监听---------------------------

        //建立接收socket成功
        void onBuildRecvSocketSuccess();

        //建立接收socket失败
        void onBuildRecvSocketFail();

        //接收推送消息成功
        void onReceiveDataSuccess(String rd);

        //接收推送消息失败
        void onReceiveDataFail();

    }
}
