import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception {

        DatagramSocket aSocket = null;
        int port = 6769;

        try{

            aSocket = new DatagramSocket(port);
            System.out.println("Server Listening on Port:" + port);
            //bound to host and port
            byte[] buffer = new byte[50];
            boolean running = true;

            InetAddress ia = InetAddress.getByName(null);
            System.out.println(ia);


            while(running){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request); //blocked if no input

                InetAddress address = request.getAddress();
                int requestPort = request.getPort();
                System.out.println(requestPort);
                System.out.println(address);


                String received = new String(request.getData(), 0, request.getLength());
                System.out.println(received);

                if (received.equals("end")) {
                    running = false;
                    continue;
                }

                DatagramPacket reply = new DatagramPacket(
                        request.getData(),
                        request.getLength(),
                        request.getAddress(),
                        request.getPort());
                //to reply, send back to the same
                aSocket.send(reply);


                //Printing out what we receive here to see if it goes throug
                for (byte b : buffer) {
                    System.out.format("0x%x ", b);
                    System.out.println(b);
                  }
            }

        } catch(Exception e){
            aSocket.close();
        }

        finally {
            System.out.println("Close Requested");
            aSocket.close();
        }

    }
}