
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

public class Communication_UDP {

    private DatagramSocket my_socket;
    private InetAddress ipAddress;
    private int port;
    private Invocation invocation_semantics;
    private boolean simulate_loss_of_packets;

    public Communication_UDP(DatagramSocket socket, InetAddress ip, int port, Invocation invocation_semantics) {
        this.my_socket = socket;
        this.ipAddress = ip;
        this.port = port;
        this.invocation_semantics = invocation_semantics;
        try {
            this.my_socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public boolean EstablishConnection() throws IOException {
        if (this.Send("0," + this.invocation_semantics.toString()) != null) {
            return true;
        }
        return false;
    }

    public void QueryByID(int id) throws IOException {
        byte[] item = this.Send("2,1,0," + id+",");

        if (item != null ) {
           
            String[] item1 = Marshalling.Unpack(item);
             if(!item1[1].trim().equalsIgnoreCase("ERROR")){
            System.out.println(item1[2].trim());
             }else{
                  System.out.println("ERROR MESSAGE: There is no flight with ID: " + id);
                 
             }
        } else {
            System.out.println("ERROR MESSAGE: There is no flight with ID: " + id);
        }
    }

    public void QueryNumberOfFlights() throws IOException {
        byte[] item = this.Send("5,0,");
        if (item != null) {
            String[] item1 = Marshalling.Unpack(item);
            System.out.println("Number of flight currently in the system: " + item1[3].trim());
        } else {
            System.out.println("ERROR MESSAGE: There are curently no flights");
        }
    }

    public void GiveLike(int n_of_likes_user_has_given) throws IOException {
        byte[] item = this.Send("6,1,0,0,0," + n_of_likes_user_has_given+",");
        if (item != null) {
            String[] item1 = Marshalling.Unpack(item);
            System.out.println("You have given a like number " + item1[3].trim());
        }
    }

    public void QueryBySourceAndDest(String source, String dest) throws IOException {
        byte[] item = this.Send("1,2,0,0," + source + "," + dest+",");
        if (item != null) {
            String[] item1 = Marshalling.Unpack(item);
            if (item1[1].trim().equalsIgnoreCase("ERROR")) {
                System.out.println("There are no flight from " + source + " to " + dest);
                return;
            }
            System.out.println("There are " + item1[1] + " flights from " + source + " to " + dest);
            String printable_result = "These flights have ID numbers:";
            for (int i = 2; i < item1.length; i++) {
                printable_result += " " + item1[i].trim();
            }
            System.out.println(printable_result);
        }

    }

    public void MakeAReservation(int flightID, int numberOfSeatsToReserve) throws IOException {
        byte[] item = this.Send("3, 2, 0, 0," + flightID + "," + numberOfSeatsToReserve+",");
        if (item != null) {
            String[] item1 = Marshalling.Unpack(item);
            if (item1[1].trim().equalsIgnoreCase("ERROR")) {
                System.out.println("Unable to perform reservation");
                return;
            }
            System.out.println("Reservation successfully made");
        }
    }

    public void MonitorFlightUpdate(int flightID, int interval) throws IOException {
        this.CallBack("4, 2, 0, 0,"+ flightID+","+ interval, interval);
    }

    public boolean CheckResponse(String request, byte[] reply) {
        String reply1 = new String(reply);

        if (reply1.split(",").length >= 3 && reply1.split(",")[2].equalsIgnoreCase("ERROR")) {
            return false;
        }
        return true;
    }

    public boolean CheckSendingMessage(String message) {
        return true;
    }

    public void CallBack(String message, int interval) throws IOException {
        // First, register the callback on the server
        byte[] start = this.Send(message);
        interval=interval*1000;
        long start_time = System.currentTimeMillis();
       
        if(Marshalling.Unpack(start)[1].trim().equalsIgnoreCase("ERROR")){
             System.out.println("No flight with this id is available ");
             return;
        }
        System.out.println("Waiting for update ");
     

        // Wait for the updates for the interval duration
        while (true) {
            try {
                 
                this.my_socket.setSoTimeout(interval);
                byte[] data = new byte[4096];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                this.my_socket.receive(packet);
                String reply =new String(packet.getData());
                System.out.println("Update recieve: \n"+reply.split(",")[4].trim());
                // String[] item1 = Marshalling.Unpack(packet.getData());
              //  System.out.println("New seat availability:\n" +item1[2]);

                // If we receive update, reduce the interval timeout for the socket
                // Continue receiving updates from the server until the time interval passed
                long time_now = System.currentTimeMillis();
                interval -= (time_now - start_time);
            } catch (SocketTimeoutException timeout) {
                
                this.my_socket.setSoTimeout(1); // set the interval back after monitoring is done
                break;
            }
        }
        System.out.println("Stopped listening for flight updates");
    }

    // Function that sending the request and waits for response from the server
    // If it doesn't receive any data for a duration used as a socket timeout,
    // it retransmits the request again
    public byte[] Send(String message) throws IOException {
        if (!this.CheckSendingMessage(message)) {
            return null;
        }

  
            try {
                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, ipAddress, port);
                // Send data
                if (this.simulate_loss_of_packets && new Random().nextInt(2) == 0) {
                    this.my_socket.send(sendPacket);
                } else if (!this.simulate_loss_of_packets) {
                    this.my_socket.send(sendPacket);
                }
              
                // Receive response
                 byte[] data = new byte[4096];
            DatagramPacket packet = new DatagramPacket(data, data.length);
           
            my_socket.receive(packet);
                if (this.CheckResponse(message, packet.getData())) {
                    return data;
                } else {
                    return null;
                }

            } catch (SocketTimeoutException timeout) {
                System.out.println("timeout");
            }
         return null;
    }
}
