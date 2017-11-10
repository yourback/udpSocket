from threading import *
from socket import *

g_receiveSocket = None

g_sendSocket = None

g_dstAddr = ()


def recvData():
    while True:
        # print('消息监听中...')
        data, addr = g_receiveSocket.recvfrom(1024)
        rd = data.decode('utf-8')
        print('\r>>>%s\n<<<'%rd, end='')
        g_receiveSocket.sendto(data, addr)
        if rd == 'bb':
            break

    print('客户端主动断开连接...')
    try:
        g_receiveSocket.close()
    except Exception as e:
        print(e)


def sendData():
    while True:
        sd = input("<<<")
        if not sd or sd == 'exit':
            break
        g_sendSocket.sendto(sd.encode(), g_dstAddr)
    print('結束推送消息')
    g_sendSocket.close()


def main():
    # 设置收信息
    global g_receiveSocket
    # 定时发送消息设置
    global g_sendSocket

    global g_dstAddr

    # 新建socket对象
    g_receiveSocket = socket(AF_INET, SOCK_DGRAM)
    g_sendSocket = socket(AF_INET, SOCK_DGRAM)

    # 绑定本地监听端口
    g_receiveSocket.bind(("", 5000))

    # 绑定发送服务器端口
    g_dstAddr = ("172.16.253.49", 7777)

    # 开启线程，接收数据
    tr = Thread(target=recvData)

    # 新建线程
    ts = Thread(target=sendData)

    # 线程开启
    tr.start()
    ts.start()

    # 主线程等待
    tr.join()
    ts.join()


if __name__ == "__main__":
    main()
