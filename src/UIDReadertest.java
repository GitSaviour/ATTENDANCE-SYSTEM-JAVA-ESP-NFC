import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
public class UIDReadertest {
    public static void main(String[] args) {
        SerialPort port = SerialPort.getCommPort("COM3"); // Change to your actual port
        port.setBaudRate(115200);

        if (!port.openPort()) {
            System.out.println("Failed to open port.");
            return;
        }

        System.out.println("Waiting for UID...");

        try {
            InputStream in = port.getInputStream();
            StringBuilder sb = new StringBuilder();

            while (true) {
                while (in.available() > 0) {
                    char c = (char) in.read();
                    if (c == '\n' || c == '\r') {
                        if (sb.length() > 0) {
                            String line = sb.toString().trim();
                            sb.setLength(0);
                            // System.out.println(">> Debug line: " + line);
                            if (line.startsWith("UID:")) {
                                String uid = line.substring(4).toUpperCase();
                                System.out.println("Received UID: " + uid);
                                if (uid.equals("81407A05")) {
                                    System.out.println("Authorised");
                                } else {
                                    System.out.println("Unauthorised");
                                }
                            }
                            else{
                                  String uid = line.substring(0).toUpperCase(); 
                                System.out.println("Received UID: " + uid);
                                if (uid.equals("81407A05")) {
                                    System.out.println("Authorised , access granted");
                                    System.out.println("welcome");
                                    System.out.println();
                                    System.out.println("x-x-x-x-x-x--x-x-x-x-x-x-x-x-x");
                                } else {
                                    System.out.println("Unauthorised , access denied");
                                    System.out.println();
                                    System.out.println("x-x-x-x-x-x--x-x-x-x-x-x-x-x-x");
                                }  
                            }
                        }
                    } else {
                        sb.append(c);
                    }
                }
                Thread.sleep(10); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
        }
    }
}
