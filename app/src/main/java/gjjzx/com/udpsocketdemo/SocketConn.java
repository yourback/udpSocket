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

//    private DatagramPacket recvSocket;

    private onSocketListener listener;

    public void setListener(onSocketListener listener) {
        this.listener = listener;
    }

    public SocketConn() {
    }

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
                            listener.onReceiveDataSuccess(result);
                    } catch (Exception e) {
                        //超时处理
                        LogUtil.e("服务器无应答，连接断开");
                        closeSendSocket();
                        if (listener != null)
                            listener.onReceiveDataFail();
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

    private void openSendSecket() {
        if (sendSocket == null || sendSocket.isClosed())
            try {
                //发送和接收消息的接口
                sendSocket = new DatagramSocket();
                LogUtil.e("新建接口成功");
            } catch (SocketException e) {
                LogUtil.e(e);
                LogUtil.e("新建socket出错");
                if (listener != null) {
                    listener.onBuildSocketFail();
                }
            }
    }

    private void closeSendSocket() {
        if (sendSocket != null && !sendSocket.isClosed()) {
            sendSocket.close();
        }
    }

//    -------------------------------------------以上发送消息监听-------------------------------------
//    -------------------------------------------以下是消息监听-------------------------------------

//
//    public void ReceiveServerSocketData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        MyApplication.isListening = true;
//                        byte[] data = new byte[4 * 1024];
//                        //参数一:要接受的data 参数二：data的长度
//                        packet = new DatagramPacket(data, data.length);
//                        LogUtil.e("客户端监听中......");
//                        socket.receive(packet);
//                        //把接收到的data转换为String字符串
//                        String result = new String(packet.getData(), packet.getOffset(),
//                                packet.getLength());
//                        if (result.equals("bb")) break;
//                        if (listener != null)
//                            listener.onReceiveDataSuccess(result);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (listener != null)
//                            listener.onReceiveDataFail();
//                        break;
//                    }
//                }
//                LogUtil.e("客户端监听结束");
//                MyApplication.isListening = false;
//            }
//        }).start();
//    }


    public interface onSocketListener {
        void onBuildSocketFail();

        void onSendDataFail();

        void onReceiveDataFail();

        void onReceiveDataSuccess(String rd);
    }
}
