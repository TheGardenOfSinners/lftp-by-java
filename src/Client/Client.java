
import java.lang.*;
import java.util.*;
import java.util.Base64;
import java.io.*;
import java.io.File;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
 
public class Client {
   /**
    * the max length of the packet
    */
   private static final int DATAMAXLENGTH = 1024;

   /**
    * the dir of the file
    */
   private static final String DATAPATH = "./Data/";

   public Client() {

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
    * send the file
    * @param address11 the server address
    * @param port      the port of the server
    * @param file      the file to send
    */
   public void sendIt(String address11,int port,File file) {
      long fileLen = 0;
      long totalSeq = 0;
      long nowSeqNum = 0;
      FileInputStream in1 = null;
      DataInputStream data_in = null;
      try {
         if (file.exists() && file.isFile()) {
            in1 =new FileInputStream(file);
            data_in = new DataInputStream(in1);
            fileLen = file.length();
            System.out.println("文件"+file.getName()+"的大小是："+fileLen);

            //第一个数据包发送文件名称和包的总数
            totalSeq = fileLen / (DATAMAXLENGTH - 8) + 1;
            byte[] d11 = long2byte(-1);
            byte[] d1 = long2byte(totalSeq);
            byte[] d2 = file.getName().getBytes("UTF-8");
            byte[] d3 = new byte[d11.length+d1.length+d2.length];
            for(int x=0;x<d1.length;x++){
               d3[x] = d11[x];
            }        
            for(int y=0;y<d1.length;y++){
               d3[d11.length+y]=d1[y];
            }
            for(int y=0;y<d2.length;y++){
               d3[d11.length+d1.length+y]=d2[y];
            }
            InetAddress address = InetAddress.getByName(address11);

            DatagramPacket packet1=new DatagramPacket(d3, d3.length, address, port);
            DatagramSocket socket1=new DatagramSocket();
            socket1.send(packet1);

            byte[] data12 = new byte[DATAMAXLENGTH];
            DatagramPacket packet12=new DatagramPacket(data12, data12.length);
            socket1.receive(packet12);
            if(byteArrayToLong(data12) != 0) {
               socket1.close();
               System.out.println("连接失败");
               data_in.close();
               in1.close();
               return;
            }
            
            
            while(nowSeqNum < totalSeq) {
               //1.定义服务器的地址、端口号、数据
               byte[] data = new byte[DATAMAXLENGTH];
               byte[] head = long2byte(nowSeqNum);
               for(int i = 0;i<8;i++) {
                  data[i] = head[i];
               }
               data_in.read(data, 8, DATAMAXLENGTH - 8);
               System.out.println("正在发送第"+ nowSeqNum + "个数据包，共"+totalSeq+"个包");
               
               //2.创建数据报，包含发送的数据信息
               DatagramPacket packet=new DatagramPacket(data, data.length, address, port);
               //3.创建DatagramSocket对象
               DatagramSocket socket=new DatagramSocket();
               //4.向服务器端发送数据报
               socket.send(packet);
               
               byte[] data2=new byte[DATAMAXLENGTH];
               DatagramPacket packet2=new DatagramPacket(data2, data2.length);
               //2.接收服务器响应的数据
               socket.receive(packet2);
               //3.读取数据
               long reply = byteArrayToLong(data2);
               System.out.println("已收到第"+reply+"个数据包，共"+totalSeq+"个包");
               if(reply == nowSeqNum+1) {
                  nowSeqNum++;
               }
               //4.关闭资源
               socket.close();

            }
            System.out.println("Over!");
            data_in.close();
            in1.close();
            return;

         } else {
             System.out.println("文件" + file + "不存在");
             return;
         }
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            if(data_in != null) {
               data_in.close();
            }
            if(in1 != null) {
               in1.close();
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   /**
    * getit function
    * @param address11 the server address
    * @param port      the port of the server
    * @param file      the file name to send
    */
   public void getIt(String address11,int port,String file)  {
      long totalSeq = 0;
      long nowSeqNum = 0;
      FileOutputStream fos = null;
      DataOutputStream dos = null;

      try {
         byte[] d11 = long2byte(-2);
         byte[] d1 = long2byte(totalSeq);
         byte[] d2 = file.getBytes("UTF-8");
         byte[] d3 = new byte[d11.length+d1.length+d2.length];
         for(int x=0;x<d1.length;x++){
               d3[x] = d11[x];
         }        
         for(int y=0;y<d1.length;y++){
            d3[d11.length+y]=d1[y];
         }
         for(int y=0;y<d2.length;y++){
            d3[d11.length+d1.length+y]=d2[y];
         }
         InetAddress address = InetAddress.getByName(address11);
         DatagramPacket packet1=new DatagramPacket(d3, d3.length, address, port);
         DatagramSocket socket1=new DatagramSocket();
         socket1.send(packet1);

         byte[] tmplen = new byte[8];
         DatagramPacket packet2=new DatagramPacket(tmplen, tmplen.length);
         socket1.receive(packet2);
         totalSeq = byteArrayToLong(tmplen);
         if(totalSeq <= 0) {
            System.out.println("资源不存在！");
            socket1.close();
            return;
         }

         fos = new FileOutputStream(DATAPATH + file);
         dos = new DataOutputStream(fos);

         while(nowSeqNum < totalSeq) {
            byte[] tmpArray1 = long2byte(nowSeqNum);
            byte[] tmpArray = new byte[DATAMAXLENGTH];
            for(int x = 0;x<8;x++) {
               tmpArray[x] = tmpArray1[x];
            }
            DatagramPacket packet12=new DatagramPacket(tmpArray, tmpArray.length, address, port);
            socket1.send(packet12);
            System.out.println("请求第"+nowSeqNum+"个数据包，共"+totalSeq+"个数据包。");

            byte[] data = new byte[DATAMAXLENGTH];
            DatagramPacket ptmp=new DatagramPacket(data, data.length);
            socket1.receive(ptmp);
            long now = byteArrayToLong(data);
            if(now == nowSeqNum) {
               System.out.println("收到第"+nowSeqNum+"个数据包，共"+totalSeq+"个数据包。");
               dos.write(data,8,ptmp.getLength()-8);
               nowSeqNum++;
            }

         }
         System.out.println("传输完毕!");
         dos.close();
         fos.close();
         socket1.close();

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

}