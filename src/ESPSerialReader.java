import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

public class ESPSerialReader {

    public static void main(String[] args) {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println("[" + i + "] " + ports[i].getSystemPortName());
        }

        SerialPort comPort = SerialPort.getCommPort("COM11"); // Change if needed
        comPort.setBaudRate(9600);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0); // scanner mode

        if (!comPort.openPort()) {
            System.out.println("❌ Failed to open port.");
            return;
        }

        System.out.println("✅ Port opened: " + comPort.getSystemPortName());
        System.out.println("⌛ Waiting for data...");

        try (Scanner scanner = new Scanner(comPort.getInputStream())) {
            while (true) {
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println("📥 Received: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            comPort.closePort();
            System.out.println("🔌 Port closed.");
        }
    }
}
