package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ServerThread extends Server implements Runnable{
    Socket socket;
    String socketName;
    public static List<Socket> bools = new Vector<>();
    public static List<String> names = new Vector<>();
    public static Socket[] arr1;
    public static String[] arr2;
    public static boolean matched = false;
    public static String username = "";

    public ServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketName = socket.getRemoteSocketAddress().toString();
            //System.out.println("[email protected]"+socketName+"已加入聊天");
            boolean flag = true;
            while (flag)
            {          	
                String line = reader.readLine();
                if (line == null){
                    flag = false;
                    continue;
                }else if(line.contains("|matching...") && !matched){
                	username = line.substring(0, line.indexOf("|"));
                	bools.add(socket);
                	names.add(username);
                }else if(line.equals("Exit")) {
                    String msg = "Exit\n";
                    print(msg, 0);
                	closeConnect();
                	flag = false;
                }else{
                    String msg = line;
                    System.out.println(msg);
                    if(socket.equals(arr1[0])) {
                    	print(msg, 0);
                    }else if(socket.equals(arr1[1])) {
                    	print(msg, 1);
                    }
                }
                if(bools.size() == 2 && !matched) {
                	arr1 = new Socket[2];
                	bools.toArray(arr1);
                	arr2 = new String[2];
                	names.toArray(arr2);
                    String msg = "match success";
                    matched = true;
                    System.out.println(msg);
                    print(msg, 0);
                }
            }

            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private void print(String msg, int index) throws IOException {
        PrintWriter out = null;
        if(msg.equals("Exit\n")) {
        	out = new PrintWriter(socket.getOutputStream());
        	out.println(msg);
        	out.flush();
        }else {
            synchronized (sockets){
    	        for (Socket sc : sockets){
    	        	if(sc.equals(arr1[0]) || sc.equals(arr1[1])) {
    		            out = new PrintWriter(sc.getOutputStream());
    		            out.println(arr2[index]+": "+msg);
    		            out.flush();
    	        	}else {
    		            out = new PrintWriter(sc.getOutputStream());
    		            out.println("full");
    		            out.flush();
    	        	}
    	        }
            }
        }
    }
    public void closeConnect() throws IOException {
        //System.out.println("[email protected]"+socketName+"已退出聊天");
        synchronized (sockets){
            sockets.remove(socket);
        }
        socket.close();
    }
}
