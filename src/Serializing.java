import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Serializing {

    public int objSize(Object obj) throws IllegalAccessException {
        int curSize = 0;            // to get the object value call f.get(obj)
        Field[] fields = obj.getClass().getDeclaredFields(); //get the declared fields within class

        /*
            Flight Identifier - int = 4 bytes
            Departure Time - *To implement this
            Airfare - Floating Point = 4 bytes
         */

        for (Field f: fields){
            if (f.getType() == int.class) {
                curSize+=4;
            }
            if (f.getType() == String.class) {
                String tmpstr = (String) f.get(obj);
//                curSize += tmpstr.length();
                curSize += 11; //i fix size here
            }
            if (f.getType() == float.class) {
                curSize += 4;
            }
        }
        return curSize;
    }
    public byte[] objToBytes(Object obj, int curSize) throws IllegalAccessException {   //convert the variables into bytes
        int index = 0;
        Field[] fields = obj.getClass().getDeclaredFields();

        byte[] buffer = new byte[curSize];

        for (Field f: fields){      //unsure of byte ordering to use
            if (f.getType() == int.class) {
                byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((Integer) f.get(obj)).array();
                for (byte b: bytes){
                    buffer[index] = b;
                    index++;
                }
            }
            if (f.getType() == String.class) {
                String tmpstr = (String) f.get(obj);
                int pad = 0;
                for (byte b: tmpstr.getBytes()) {
                    buffer[index] = b;
                    index++;
                    pad++;
                }
            }
            if (f.getType() == float.class) {
                byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat((Float) f.get(obj)).array();
                for (byte b: bytes){
                    buffer[index] = b;
                    index++;
                }
                index++;
            }
        }
//        System.out.println(curSize);

        return buffer;
    }


}
