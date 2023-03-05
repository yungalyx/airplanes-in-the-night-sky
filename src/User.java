import java.io.IOException;
import java.util.Scanner;

public class User {
    public static void main(String args[]) throws IOException, IllegalAccessException {
        Serializing serial = new Serializing();
        TestObject testObj = new TestObject();
        int bufferSize = serial.objSize(testObj);
        byte[] byteArray = serial.objToBytes(testObj,bufferSize);


        int i;
        boolean loop = true;
        UDPClient client = new UDPClient("127.0.0.1");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("1. Query flights by source and destination\n" +
                           "2. Query flight information\n" +
                           "3. Make flight reservation\n" +
                           "4. Monitor flight updates\n" +
                           "5. Idempotent operation\n" +
                           "6. Non Idempotent operation\n" +
                           "7. Exit");

        while (loop) {
            i = myObj.nextInt();  // Read user input
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    client.Client(byteArray,bufferSize);
                    break;
                default:
//                    client.Client(Integer.toString(i));
                    loop = false;
                    break;
            }
        }

    }
}
