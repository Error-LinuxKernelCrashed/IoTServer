package com.jinyi.iotsrv;
import java.io.UnsupportedEncodingException;
public class LockAuth {
    boolean authToken(String Token){return false;}  // TODO:在此处添加Token验证
    class RC4Crypt{
        String encrypt(String Data, String Key, String Charset){
            if(Data == null || Key == null){
                return null;
            }
            return bytesToHex(RC4ByteEncrypt(Data, Key, Charset));
        }
        byte[] RC4ByteEncrypt(String Data, String Key, String Charset){
            if(Data == null || Key == null){
                return null;
            }
            if(Charset == null || Charset.isEmpty()){
                byte bData[] = Data.getBytes();
                return RC4Base(bData, Key);
            } else {
                byte[] bData;
                try {
                    bData = Data.getBytes(Charset);
                    return RC4Base(bData, Key);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        String RC4Decrypt(String Data, String Key, String Charset){
            if(Data == null || Key == null){
                return null;
            }
            try {
                return new String(RC4Base(hexToByte(Data), Key), Charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                return new String(RC4Base(hexToByte(Data), Key), Charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        private byte[] initKey(String RawKey){
            byte[] Key = RawKey.getBytes();
            byte [] State = new byte[256];
            for (int i = 0;i < 256; i++){
                State[i] = (byte)i;
            }
            int index[] = new int[2];
            index[0] = 0;
            index[1] = 0;
            if (Key.length == 0){
                return null;
            }
            for (int i = 0; i < 256; i++){
                index[1] = ((Key[index[0]] & 0xFF) + (State[i] & 0xFF) + index[1]) & 0xFF;
                byte tmp = State[i];
                State[i] = State[index[1]];
                State[index[1]] = tmp;
                index[0] = (index[0] + 1) % Key.length;
            }
            return State; 
        }
        String bytesToHex(byte[] Bytes){
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < Bytes.length; i++){
                String Hex = Integer.toHexString(Bytes[i] & 0xFF);
                if(Hex.length() < 2){
                    buf.append(0);
                }
                buf.append(Hex);
            }
            return buf.toString();
        }
        byte[] hexToByte(String Hex){
            byte[] Bytes;
            int HexLen = Hex.length();
            if(HexLen % 2 == 1){
                HexLen++;
                Bytes = new byte[(HexLen / 2)];
                Hex = "0" + Hex;
            } else {
                Bytes = new byte[(HexLen / 2)];
            }
            int j = 0;
            for (int i = 0; i < HexLen; i+=2){
                Bytes[j] = (byte)Integer.parseInt(Hex.substring(i, i+2), 16);
                j++;
            }
            return Bytes;
        }
        byte[] RC4Base(byte[] input, String mKkey){
            int xorIndex;
            int x = 0, y = 0;
            byte[] Key = initKey(mKkey);
            byte[] Result = new byte[input.length];
            for (int i = 0; i < input.length; i++){
                x = (x + 1) & 0xFF;
                y = ((Key[x] & 0xFF) + y) & 0xFF;
                byte tmp = Key[x];
                Key[x] = Key[y];
                Key[y] = tmp;
                xorIndex = ((Key[x] & 0xFF) + (Key[y] & 0xFF)) & 0xFF;
                Result[i] = (byte)(input[i] ^ Key[xorIndex]);
            }
            return Result;
        }
    }
}
