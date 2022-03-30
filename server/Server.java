package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
  public static List<Socket> sockets = new Vector<>();
  
  public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        while (flag){
            try {
	            Socket accept = server.accept();	//waiting...
	            synchronized (sockets){
	                sockets.add(accept);
	            }
	            Thread thread = new Thread(new ServerThread(accept));
	            thread.start();
            }catch (Exception e){
                flag = false;
                e.printStackTrace();
            }
        }
        server.close();
    }

}
