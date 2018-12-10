import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
 
public class Server {
    /**
     * the max length of the packet
     */
    private static final int DATAMAXLENGTH = 1024;

    /**
     * the dir of the file
     */
    private static final String DATAPATH = "./Data/";

    public Server() {
        
    }
    
    /**
     * translat long to byte[]
     * @param  res input long number
     * @return     output byte[]
     */
    public byte[] long2byte(long res) { 
        byte[] buffer = new byte[8]; 
        for (int i = 0; i < 8; i++) { 
           int offset = 64 - (i + 1) * 8; 
           buffer[i] = (byte) ((res >> offset) & 0xff); 
        }
        return buffer;
    }
    
    /**
     * translat the first 8 byte of byte[] to long
     * @param  b input array
     * @return   number of long type
     */
    public long byteArrayToLong(byte[] b){ 
        long values = 0; 
        for (int i = 0; i < 8; i++) { 
            values <<= 8; values|= (b[i] & 0xff); 
        } 
        return values; 
    }
    
    /**
     * start listen
     */
    public void work() {
        try {
            //1、创建一个服务器端Socket,即ServerSocket, 指定绑定的端口，并监听此端口
            DatagramSocket socket  = new DatagramSocket(1234);
            

            System.out.println("***服务器即将启动，等待client的链接***");




            //循环监听等待client的链接
            while (true){
                byte[] data = new byte[DATAMAXLENGTH];

                DatagramPacket packet = new DatagramPacket(data, data.length);
                // 3、接收客户端发送的数据(此方法在接受到数据报之前会一直阻塞)
                socket.receive(packet);

                long type = byteArrayToLong(data);
                byte[] tmp = new byte[8];
                for(int i = 0;i<8;i++) {
                    tmp[i] = data[i+8];
                }
                
                long seqNum = byteArrayToLong(tmp);

                String fileName = new String(data, 16, packet.getLength()-16,"UTF-8");
                System.out.println(fileName);

                if(type == -1) {
                    lsend(socket,packet,seqNum,fileName);
                } else {
                    lget(socket,packet,fileName);
                }
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     * lsend 
     * @param soin     the socket from client
     * @param pain     the packet from client
     * @param len1     total number of packet
     * @param fileName the send file name
     */
    public void lsend(DatagramSocket soin,DatagramPacket pain,long len1,String fileName) {
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try {
            fos = new FileOutputStream(DATAPATH + fileName);
            dos = new DataOutputStream(fos);
            InetAddress address = pain.getAddress();
            int port = pain.getPort();
            long i = 0;
            while(i < len1) {
                byte[] tmp = long2byte(i);
                DatagramPacket packet1=new DatagramPacket(tmp, tmp.length, address, port);
                soin.send(packet1);
                System.out.println("请求第" + i + "包，共"+len1+"个包");
                
                byte[] data = new byte[DATAMAXLENGTH];
                DatagramPacket ptmp=new DatagramPacket(data, data.length);
                soin.receive(ptmp);
                address = ptmp.getAddress();
                port = ptmp.getPort();
                long now = byteArrayToLong(data);
                if(now == i) {
                    dos.write(data,8,ptmp.getLength()-8);

                    System.out.println("收到第" + now + "包，共"+len1+"个包");
                    i++;
                    
                }
            }
            byte[] tmp = long2byte(i);
            DatagramPacket packet1=new DatagramPacket(tmp, tmp.length, address, port);
            soin.send(packet1);
            System.out.println("接收完毕");

            fos.close();
            dos.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                
                if (dos!=null) {
                    dos.close();
                }
                if (fos!=null) {
                   fos.close();
                }
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
    }
    
    /**
     * lget
     * @param soin     the socket from client
     * @param pain     the packet from client
     * @param fileName the send file name
     */
    public void lget(DatagramSocket soin,DatagramPacket pain,String fileName) {
        File a = new File(DATAPATH+fileName);
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            InetAddress address = pain.getAddress();
            int port = pain.getPort();
            if(!a.exists()) {
               System.out.println("文件不存在");
               byte[] tmp = long2byte(-1);
               DatagramPacket packet1=new DatagramPacket(tmp, tmp.length, address, port);
               soin.send(packet1);
               return;
            }
            fis = new FileInputStream(a);
            dis = new DataInputStream(fis);
            long totalSeq =  a.length() / (DATAMAXLENGTH - 8) + 1;
            
            byte[] tmp1 = long2byte(totalSeq);
            
            DatagramPacket packet1=new DatagramPacket(tmp1, tmp1.length, address, port);
            soin.send(packet1);
            
            long i = 0;
            byte[] d2 = new byte[DATAMAXLENGTH];
            while(i < totalSeq) {
                byte[] data12 = new byte[DATAMAXLENGTH];
                DatagramPacket packet12=new DatagramPacket(data12, data12.length);
                soin.receive(packet12);
                System.out.println("收到第"+byteArrayToLong(data12)+"个数据包的请求，共"+totalSeq+"个数据包。");
                
                
                if(byteArrayToLong(data12) >= i) {
                    byte[] d1 = long2byte(i);
                    for(int x = 0;x<8;x++) {
                        d2[x] = d1[x];
                    }

                    dis.read(d2,8,DATAMAXLENGTH - 8);
                     System.out.println("正在发送第"+ i + "个数据包，共"+totalSeq+"个包");
               
                    //2.创建数据报，包含发送的数据信息
                    DatagramPacket packet=new DatagramPacket(d2, d2.length, address, port);
                    //3.创建DatagramSocket对象
                    soin.send(packet);
                    if(byteArrayToLong(data12) == i) {
                        i++;
                    }
                    

                }
                d2 = null;
                d2 = new byte[DATAMAXLENGTH];
            }
            System.out.println("发送完毕！");
            
            dis.close();
            fis.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                
                if (dis!=null) {
                    dis.close();
                }
                if (fis!=null) {
                   fis.close();
                }
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }

        
    }
}