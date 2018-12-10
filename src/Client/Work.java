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
 
public class Work {
   public static void main(String[] args) {
      String type = args[0];
      String ip = args[1];
      String filename = args[2];
//      File file = new File("./Data/0001.jpg");
      
      Client aa = new Client();
  
      int port = 1234;
      if(type.charAt(1) == 's') {
         File file = new File(filename);
         aa.sendIt(ip,port,file);
      } else {
         aa.getIt(ip,port,filename);
      }
      
      return;
   }
}