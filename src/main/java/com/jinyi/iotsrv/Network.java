package com.jinyi.iotsrv;
import java.io.ByteArrayOutputStream;
import com.alibaba.fastjson.*;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
                String deviceTypeString = msgJsonObject.getString("DeviceType");
                switch (deviceTypeString){
                    case "Lock": {
                        LockMsgHandler msgHandler = new LockMsgHandler(socket, msgJsonObject);
                        msgHandler.ProcessRequest();
                    }
                    default: System.out.println("未知设备类型");
                }
            } else {
                System.out.println("非法请求");
            }
        }
        class LockMsgHandler{
            String msgString;
            Socket clientSocket;
            public LockMsgHandler(Socket client, JSONObject msgJsonObj){
                JSONObject srvMsgObject = new JSONObject();
                srvMsgObject.put("DeviceType", "Server");
                String msgType = msgJsonObj.getString("AckType");
                switch(msgType){
                    case "HandShake":{
                        // 握手处理
                        srvMsgObject.put("AckType", "Handshake");
                        srvMsgObject.put("Msg", "SrvHandShake");
                    }
                    default: System.out.println("来自智能门锁的未知请求，请尝试更新本程序");
                }
                msgString = srvMsgObject.toJSONString();
            }
            public void ProcessRequest(){

            }
        }
    }
}
