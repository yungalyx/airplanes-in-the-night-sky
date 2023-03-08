
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.IOException;
import static java.lang.Math.random;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Function that either updates the monitor interval of the user or
// indicates that the monitor interval has ended by deleting the entry from the monitoring list
public class Server implements Runnable {

    private String ipAddress;
    private int port;
    private Invocation invocation_semantics;
    private FlightSystem flightSystem;

    private int LIMIT = 10;

    // ([ip,port,request],reply)
    private List<Object[]> cache; // store the last LIMIT requests and responses for at-most-once invocation semantics

    private boolean simulate_loss_of_packets;

    // list used to keeping track of user who want to monitor seat availability updates of a certain flight
    // [flight id, ip, port, interval]
    private List<String[]> monitoring;
    DatagramSocket mySocket;

    private double time;

    public Server() throws SocketException {
        this.ipAddress = "localhost";
        this.port = 4567;
        this.invocation_semantics = Invocation.AT_MOST_ONCE;
        this.flightSystem = new FlightSystem();

        this.LIMIT = 10;

        // ([ip,port,request],reply)
        this.cache = new ArrayList<Object[]>(); // store the last LIMIT requests and responses for at-most-once invocation semantics

        this.simulate_loss_of_packets = false;

        // list used to keeping track of user who want to monitor seat availability updates of a certain flight
        // [flight id, ip, port, interval]
        this.monitoring = new ArrayList<String[]>();

        this.time = System.currentTimeMillis();

    }

    public void CheckMonitoringTimes() {
        double timeNow = System.currentTimeMillis();
        List<String[]> result = new ArrayList<String[]>();

        for (int i = 0; i < this.monitoring.size(); i++) {

            if (Double.parseDouble(this.monitoring.get(i)[3]) * 1000 > timeNow - this.time) {
                result.add(new String[]{this.monitoring.get(i)[0], this.monitoring.get(i)[1], this.monitoring.get(i)[2],
                    (Double.parseDouble(this.monitoring.get(i)[3]) - (timeNow - this.time)) + ""});

            }
        }

        this.time = System.currentTimeMillis();
        this.monitoring = result;
    }

    public String Execute_Reply_Method(String request, InetAddress address,int port) throws UnknownHostException, IOException {
     
        String[] obj = request.split(",");
        if (obj[0].equalsIgnoreCase("0")) {
            return "Established";
        }
        if (obj[0].equalsIgnoreCase("1")) {
            System.out.println("Service 1; Search for flight from " + obj[4] + " to " + obj[5]);
            ArrayList<Flight> result = this.flightSystem.QueryFlightsBySourceAndDest(obj[4], obj[5]);
            if (result.size() == 0) {
                return obj[0] + ",ERROR";
            }
            String return_result = obj[0] + "," + result.size();
            for (Flight i : result) {

                return_result = return_result + "," + i.getId();
            }

            return return_result;
        }

        if (obj[0].equalsIgnoreCase("2")) {
            System.out.println("Service 2; ID used to query fligths:" + obj[3]);
            Flight result = this.flightSystem.QueryFlightByID(Integer.parseInt(obj[3]));
            if (result == null) {
                return obj[0] + ",ERROR";
            }

            return obj[0] + ",1," + result.Get_printable_string();
        }
        if (obj[0].equalsIgnoreCase("3")) {
            System.out.println("Service 3; User tries to make an reservation of " + obj[5] + " seats on a flight ID:" + obj[4]);
            this.CheckMonitoringTimes();;
            Boolean result = this.flightSystem.MakeAnReservation(Integer.parseInt(obj[4]), Integer.parseInt(obj[5]));
            System.out.println("checking "+result);
            if (result == false) {
                return obj[0] + ",ERROR";
            }
            System.out.println("size: " + monitoring.size() + " id " + (obj[4]));
            for (String[] item : this.monitoring) {
                if (item[0].equalsIgnoreCase(obj[4])) {
                    System.out.println("Service 4; Sending update to a user " + (item[1] + "," + item[2]));

                    byte[] sendData = ("4,1,0,0," + flightSystem.QueryFlightByID(Integer.parseInt(obj[4])).Get_printable_string()).getBytes();
                    InetAddress IPAddress = InetAddress.getByName(item[1]);
                    DatagramPacket sendPacket
                            = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(item[2]));
                    mySocket.send(sendPacket);
                }
            }

            return obj[0] + ",1," + 0;
        }
        if (obj[0].equalsIgnoreCase("4")) {
        
            System.out.println("Service 4; User" + address + " wants to monitor seats availability on flight with ID " + obj[4] + " for " + obj[5].trim() + " seconds");
              Flight result = this.flightSystem.QueryFlightByID(Integer.parseInt(obj[4]));
           
              if(result==null){
                  return obj[0] + ",ERROR";
              }else{
            this.CheckMonitoringTimes();;
            this.monitoring.add(new String[]{obj[4], this.ipAddress, port + "", obj[5].trim()});
           
            return obj[0] + "," + 1 + ",0";
              }
        }
        if (obj[0].equalsIgnoreCase("5")) {
            System.out.println("Service 5; User asked to query number of flights");
            int result = this.flightSystem.QueryNumberOfFlights();
            System.out.println(result);
            return obj[0] + "," + 1 + ",0," + result;
        }

        if (obj[0].equalsIgnoreCase("6")) {
            int result = this.flightSystem.GiveUsALike();
            System.out.println("Service 6; User gave us a like number " + result);
            return obj[0] + "," + 1 + ",0," + result;
        }
        return "";
    }

    public void Reply_To_Request(String request, InetAddress addres, int port) throws IOException {
        String[] obj = request.split(",");

        if (obj.length > 1 && obj[0].equals("0") && obj[1].trim().equalsIgnoreCase("AT_MOST_ONCE")) {
            this.invocation_semantics = Invocation.AT_MOST_ONCE;
            System.out.println("Starting using At-most-once invocation semantics");
        } else if (obj.length > 1 && obj[0].equals("0") && obj[1].trim().equalsIgnoreCase("AT_LEAST_ONCE")) {

            this.invocation_semantics = Invocation.AT_LEAST_ONCE;
            System.out.println("Starting using At-least-once invocation semantics");
        }

        if (invocation_semantics == Invocation.AT_LEAST_ONCE) {

            this.Reply_To_Request_At_Least_Once(request, addres, port);
        } else {
            Reply_To_Request_At_Most_Once(request, addres, port);
        }

    }

    public void Reply_To_Request_At_Most_Once(String request, InetAddress address, int port) throws IOException {
        // Check for duplicates
        Random random = new Random();
        DatagramPacket sendPacket = new DatagramPacket(request.getBytes(), request.getBytes().length, address, port);

        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i)[0].equals(address.getHostAddress() + ":" + port + ":" + request)) {
                if (simulate_loss_of_packets && random.nextInt(2) == 0) {
                    mySocket.send(sendPacket);
                } else if (!simulate_loss_of_packets) {
                    mySocket.send(sendPacket);
                }
                return;
            }
        }

        String reply = Execute_Reply_Method(request, address,port);

        // Keep the last LIMIT number of request from the client in the cache
        if (cache.size() == LIMIT) {
            cache.remove(0);
        }
        cache.add(new String[]{address.getHostAddress() + ":" + port + ":" + request, reply});
        DatagramPacket sendPacket1 = new DatagramPacket(reply.getBytes(), reply.getBytes().length, address, port);

        if (simulate_loss_of_packets && random.nextInt(2) == 0) {
            mySocket.send(sendPacket1);
        } else if (!simulate_loss_of_packets) {

            mySocket.send(sendPacket1);
        }
    }

    // At-least-once invocation semantics
    public void Reply_To_Request_At_Least_Once(String request, InetAddress address, int port) throws IOException {
        String reply = Execute_Reply_Method(request, address,port);

        //System.out.println(reply);
        Random random = new Random();
        DatagramPacket sendPacket = new DatagramPacket(reply.getBytes(), reply.getBytes().length, address, port);

        // Send data
        if (simulate_loss_of_packets && random.nextInt(2) == 0) {
            mySocket.send(sendPacket);
        } else if (simulate_loss_of_packets == false) {

            mySocket.send(sendPacket);
        }
    }

    public void Communicate() throws IOException {
        while (true) {
            // Wait for a data from a client
            System.out.println("waiting for data");
            byte[] data = new byte[4096];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            mySocket.receive(packet);
            System.out.println("Received data from address:" + packet.getAddress());
            // System.out.println("Received " + Marshalling.Unpack(data)[1].trim());
            Reply_To_Request(new String(packet.getData()), packet.getAddress(), packet.getPort());
        }
    }

    public void run() {
        try {
            // AF_INET refers to addresses from the internet, IP addresses specifically. SOCK_DGRAM states that we will use UDP
            mySocket = new DatagramSocket(null);

            // Bind the socket to the port
            InetSocketAddress server_address = new InetSocketAddress(ipAddress, port);
            System.out.println("starting up on " + server_address.getHostName() + " port " + server_address.getPort());
            mySocket.bind(server_address);

            Communicate();
        } catch (SocketException e) {
            e.printStackTrace();
            mySocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            mySocket.close();
        }
        mySocket.close();
    }

    public static void main(String args[]) throws Exception {

        String ipAddress = "localhost";
        int port = 4567;

        Server server = new Server();
        server.setIpAddress(ipAddress);
        server.setPort(port);

        server.run();

    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}
