package com.jinyi.iotsrv;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Network {
    ServerSocket srvSocket;
    public Network(int port){
        try{
            srvSocket = new ServerSocket(port);
            System.out.println("开始监听" + port + "端口");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    class ListenThread implements Runnable{
        public void run(){
            Socket clientSocket = null;
            while (true){
                try {
                    clientSocket = srvSocket.accept();
                } catch (IOException e){
                    e.printStackTrace();
                } finally{
                    if (clientSocket != null){
                        try{
                            
                            // Handling Client Connect Event
                            clientSocket.close();
                            clientSocket = null;
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
