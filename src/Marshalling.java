
public class Marshalling {
static int INT=23;
   public static String[] Unpack(byte[] item) {
       String reply = new String(item);
       return reply.split("\\,");
    }
    
}

