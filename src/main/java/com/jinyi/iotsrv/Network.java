package com.jinyi.iotsrv;
import java.io.ByteArrayOutputStream;
import com.alibaba.fastjson.*;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.net.Socket;
import java.util.Set;
public class Network {
    ServerSocket srvSocket;
    public Network(int port){
        try{
            srvSocket = new ServerSocket(port);
            System.out.println("开始监听" + port + "端口");
            // 创建ServerSocket,开始监听端口
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
                            MsgHandler(clientSocket);
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
        private void MsgHandler(Socket socket){
            int length;
            String rcevString = "";
            byte[] rcevBuffer = new byte[1024];
            try {
                InputStream clientInputStream = socket.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(64000);
                while((length = clientInputStream.read(rcevBuffer)) != -1){
                    byteArrayOutputStream.write(rcevBuffer, 0, length);
                    rcevString = byteArrayOutputStream.toString("UTF-8");
                    byteArrayOutputStream = new ByteArrayOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(rcevString != ""){
                JSONObject msgJsonObject = JSONObject.parseObject(rcevString);
                // Convert String into JSON Object
                String ackTypeString = msgJsonObject.getString("ackType");
                switch (ackTypeString){
                    // TODO:在此添加消息响应
                    default: System.out.println("非法请求");
                }
            } else {
                System.out.println("非法请求");
            }
        }
    }
}
