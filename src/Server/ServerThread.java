import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerThread extends Thread {
	DatagramSocket socket = null;


	public ServerThread(DatagramSocket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            byte[] data = packet.getData();
        // 4、读取数据
            String info = new String(data, 8, packet.getLength());
            System.out.println("我是服务端，客户端说：" + info);
        /*
         * 向客户端响应数据
         */
        // 1、定义客户端的地址、端口号、数据
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            byte[] data2 = "欢迎您！".getBytes();
        // 2、创建数据报，包含响应的数据信息
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
            socket.send(packet2);

        } catch (IOException e) {
           // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}