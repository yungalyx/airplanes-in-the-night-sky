import java.net.*;
import java.io.*;

public class App {
    public static void main(String[] args) throws Exception {

        DatagramSocket aSocket = null;
        int port = 6789;
        
        try{
          
            aSocket = new DatagramSocket(port);
            System.out.println("Server Listening on Port:" + port);
            //bound to host and port
            byte[] buffer = new byte[1000];
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
                //to reply, send back to the same portingapore 
                aSocket.send(reply);
            }
               
        } finally {
            System.out.println("Close Requested");
            aSocket.close();
        }
        
    }
}
