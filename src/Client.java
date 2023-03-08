
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client implements Runnable {

    private String UDP_SERVER_IP_ADDRESS;
    private int UDP_SERVER_PORT_NUMBER;
    private int number_of_likes_given;
    private Invocation semantics_invocation;

    public Client() {
        this.UDP_SERVER_IP_ADDRESS = "localhost";
        this.UDP_SERVER_PORT_NUMBER = 4567;
        this.number_of_likes_given = 0;
        this.semantics_invocation = Invocation.AT_LEAST_ONCE;
    }

    private  boolean EstablishConnection(Communication_UDP communication, DatagramSocket my_socket) throws IOException {
        boolean success = communication.EstablishConnection();

        if (!success) {
            System.out.println("Connection not established");
            my_socket.close();
        }

        return success;
    }
    public void run() {
        if (semantics_invocation == Invocation.AT_LEAST_ONCE) {
            System.out.println("Invocation semantics used: at least once");
        } else {
            System.out.println("Invocation semantics used: at most once");
        }

        // AF_INET refers to addresses from the internet, IP addresses specifically. SOCK_DGRAM states that we will use UDP
        DatagramSocket my_socket = null;
          InetAddress IPAddress = null;
        try {
            my_socket = new DatagramSocket();
            
            IPAddress = InetAddress.getByName(this.UDP_SERVER_IP_ADDRESS);
             Communication_UDP communication = new Communication_UDP(my_socket, IPAddress, UDP_SERVER_PORT_NUMBER, semantics_invocation);

        boolean success = EstablishConnection(communication, my_socket);
        if (success) {
            System.out.println("Communication successfully established");
        }
         System.out.println("Welcome to flight information system.");
         while (true) {

            System.out.println("Press:\n1 - query flights by source and destination of the flight\n2 - query flights by ID\n"
                    + "3 - make a reservation\n4 - monitor flight updates\n5 - Check number of flights\n6 - Give a like\n9 - Exit");

            Scanner scanner = new Scanner(System.in);
            String user_choice = scanner.nextLine();
            if (user_choice.equals("1")) {
                System.out.println("Source:");
                String source = scanner.nextLine();
                System.out.println("Destination:");
                String dest = scanner.nextLine();
                communication.QueryBySourceAndDest(source, dest);
            } else if (user_choice.equals("2")) {
                System.out.println("Choose your flight ID:");
                String user_id = scanner.nextLine();
                communication.QueryByID(Integer.parseInt(user_id));
            } else if (user_choice.equals("3")) {
                System.out.println("Input Flight ID:");
                String flight_id = scanner.nextLine();
                System.out.println("Number of seats you want to reserve:");
                String n_of_seats = scanner.nextLine();
                communication.MakeAReservation(Integer.parseInt(flight_id), Integer.parseInt(n_of_seats));
            } else if (user_choice.equals("4")) {
                System.out.println("Input Flight ID:");
                String flight_id = scanner.nextLine();
                System.out.println("Input listening interval in seconds:");
                String seconds = scanner.nextLine();
                communication.MonitorFlightUpdate(Integer.parseInt(flight_id), Integer.parseInt(seconds));
            } else if (user_choice.equals("5")) {
                communication.QueryNumberOfFlights();
            } else if (user_choice.equals("6")) {
                communication.GiveLike(number_of_likes_given);
                number_of_likes_given += 1;
            } else if (user_choice.equals("9")) {
                return;
            } else {
                System.out.println("You have entered incorrect request, please write a number between 1 and 6 or number 9");
            }
        }

       
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        

       

       
    }

    /* public boolean EstablishConnection(Communication communication,Socket my_socket) throws IOException{
        boolean success = communication.EstablishConnection()

        if(!success){
            System.out.println("Connection not established");
            my_socket.close()
                    }
        return success
                }*/
   /* @Override
    public void run() {
        BufferedReader inFromUser
                = new BufferedReader(new InputStreamReader(System.in));
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(this.UDP_SERVER_IP_ADDRESS);

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            if (this.semantics_invocation == Invocation.AT_LEAST_ONCE) {
                System.out.println("Invocation semantics used: at least once");
            } else {
                System.out.println("Invocation semantics used: at most once");
            }
            System.out.println("Welcome to flight information system.");

            while (true) {

                System.out.println("Press:\n1 - query flights by source and destination of the flight\n2 - query flights by ID\n"
                        + "3 - make a reservation\n4 - monitor flight updates\n5 - Check number of flights\n6 - Give a like\n9 - Exit");
                String user_choice = inFromUser.readLine();

                if (user_choice.equalsIgnoreCase("1")) {
                    System.out.println("Source:");
                    String source = inFromUser.readLine();
                    System.out.println("Destination:");
                    String dest = inFromUser.readLine();
                    String sentence = "1," + source + "," + dest;
                    sendData = sentence.getBytes();
                } else if (user_choice.equalsIgnoreCase("2")) {
                    System.out.println("Input Flight ID::");
                    String user_id = inFromUser.readLine();
                    String sentence = "2," + user_id;
                    sendData = sentence.getBytes();
                } else if (user_choice.equalsIgnoreCase("3")) {
                    System.out.println("Choose your flight ID:");
                    String flight_id = inFromUser.readLine();

                    System.out.println("Number of seats you want to reserve:");
                    String n_of_seats = inFromUser.readLine();
                    String sentence = "3," + flight_id + "," + n_of_seats;
                    sendData = sentence.getBytes();
                } else if (user_choice.equalsIgnoreCase("4")) {
                    System.out.println("Input your flight ID:");
                    String flight_id = inFromUser.readLine();
                    System.out.println("Input listening interval in seconds:");
                    String seconds = inFromUser.readLine();
                    String sentence = "4," + flight_id + "," + seconds;
                    sendData = sentence.getBytes();
                } else if (user_choice.equalsIgnoreCase("5")) {
                    String sentence = "5";
                    sendData = sentence.getBytes();
                } else if (user_choice.equalsIgnoreCase("6")) {
                    String sentence = "6";
                    sendData = sentence.getBytes();
                    this.number_of_likes_given += 1;
                } else if (user_choice.equalsIgnoreCase("9")) {
                    break;
                } else {
                    System.out.println("You have entered incorrect request, please write a number between 1 and 6 or number 9");

                }

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.UDP_SERVER_PORT_NUMBER);
                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);

            }
            clientSocket.close();

        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
*/
    public static void main(String args[]) throws Exception {
        Client m1 = new Client();
         System.out.println("Press the option:\n1: At least once\n2: At most once");
         Scanner s=new Scanner(System.in);
         int opt=s.nextInt();
         s.nextLine();
        if(opt==1){
            m1.semantics_invocation=Invocation.AT_LEAST_ONCE;
        }else{
             m1.semantics_invocation=Invocation.AT_MOST_ONCE;
        }
        Thread t1 = new Thread(m1);   // Using the constructor Thread(Runnable r)  
        t1.start();

    }

}
