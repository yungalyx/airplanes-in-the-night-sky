import java.net.*;
import java.io.*;

public class UDPClient {

    InetAddress hostIP;
    int hostPort = 6769;
    Boolean connectionIsOpen = false;
    DatagramSocket socket = null;

    public UDPClient(String ipAdd) throws UnknownHostException {
        this.hostIP = InetAddress.getByName(ipAdd);
    }
    public void Client(byte[] message, int bufferSize){
        try {
            this.socket = new DatagramSocket(); //use a free local port

            DatagramPacket request = new DatagramPacket(message, bufferSize, this.hostIP, this.hostPort);
            this.socket.send(request);

            //send packet using socket method
            byte[] buffer = new byte[1000]; //a buffer for receive
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length); //a different constructor
            this.socket.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));
            connectionIsOpen = true;

        } catch (IOException e) {
            connectionIsOpen = false;
            System.out.println(e);
            this.socket.close();
        } finally {
            if (this.socket != null) {
                connectionIsOpen = false;
                this.socket.close();
            }
        }
    }


}