import java.nio.ByteBuffer;

// 1. can create classes of airplane representation 
// 2. overwrite the toString methods to preformat marshall (append info)
// 3. convert to byte
// 4. unmarshal will read for certain strings and return an object.. 
// 5. maybe send in a number to figure out what class object to marshall and unmarshall to


/* e.g, Flight Object (id, source, destination, departure_time, airfare, seats available)
 * toString() ==> "Flight: ____, Source: _____, Destination: ______, Airfare: ____,"
 * bytes = 2j18hru1by21 id 1
 * 
 * conversion: string.split(","), 
 * 
 * might need to include function indicator to specify what function to invoke 
 */ 

public class Marshaller {

  public static byte[] marshal(Object obj) {
    // Convert the object to a byte array
    // For example, you could use JSON or XML serialization
    // In this example, we'll use Java's ByteBuffer class
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.put(obj.toString().getBytes());
    return buffer.array();
  }

  public static Object unmarshal(byte[] bytes) {
    // Convert the byte array back into an object
    // For example, you could use JSON or XML deserialization
    // In this example, we'll use Java's ByteBuffer class
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    byte[] objBytes = new byte[buffer.remaining()];
    buffer.get(objBytes);
    String objString = new String(objBytes);
    return objString;
  }

}