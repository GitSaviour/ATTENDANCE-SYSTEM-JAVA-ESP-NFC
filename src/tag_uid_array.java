import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
public class tag_uid_array {
    public static void main(String[] args) {
                SerialPort port = SerialPort.getCommPort("COM03"); // Change to your actual port
        port.setBaudRate(115200);

        if (!port.openPort()) {
            System.out.println("Failed to open port.");
            return;
        }
        String []arr =new String[22];
        int i = 0;
        System.out.println("Waiting for UID...");

        try {
            InputStream in = port.getInputStream();
            StringBuilder sb = new StringBuilder();

            while (i<22) {
                while (in.available() > 0) {
                    char c = (char) in.read();
                    if (c == '\n' || c == '\r') {
                        if (sb.length() > 0) {
                            String line = sb.toString().trim();
                            sb.setLength(0);
                            // System.out.println(">> Debug line: " + line);
                            String uid = line.substring(0).toUpperCase(); 
                            arr[i]=uid;
                            i++;
                            System.out.println("Received UID: " + uid);         
                        }
                    } else {
                        sb.append(c);
                    }
                }
                Thread.sleep(10); 
            }
            for (String String : arr) {
                if (String != null) {
                    System.out.println("Stored UID: " + String);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            port.closePort();
        }

    }
}
