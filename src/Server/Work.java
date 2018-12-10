import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
 
public class Work {
    public static void main(String[] args) {
         Server aa = new Server();
         aa.work();
         return;
   }
}