from threading import *
from socket import *

g_udpSocket = None


def recvData():
    while True:
        print('消息监听中...')
        data, addr = g_udpSocket.recvfrom(1024)
        rd = data.decode('utf-8')
        print(addr, '  发来消息：', rd)
        g_udpSocket.sendto(data, addr)
        if rd == 'bb':
            break

    print('客户端主动断开连接...')
    try:
        g_udpSocket.close()
    except Exception as e:
        print(e)


def main():
    # 设置收信息
    global g_udpSocket

    # 新建socket对象
    g_udpSocket = socket(AF_INET, SOCK_DGRAM)

    # 绑定本地监听端口
    g_udpSocket.bind(("", 5000))

    # 开启线程，接收数据
    tr = Thread(target=recvData)

    # 线程开启
    tr.start()

    # 主线程等待
    tr.join()


if __name__ == "__main__":
    main()
