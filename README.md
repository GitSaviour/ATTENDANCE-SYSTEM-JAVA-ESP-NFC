# NFC-Based Attendance System using Java, JDBC, and ESP32

This project is a smart login/logout time tracker using an **ESP32 + NFC reader** and a **Java Swing GUI**. It scans NFC cards to record attendance, logging users in at the first scan and logging them out at the second scan of the same day. All records are stored in a MySQL database and displayed via a desktop application.

---

## 🛠 Tech Stack

- Java (Swing) for GUI
- JDBC for database connectivity
- MySQL for storing attendance logs
- ESP32 with RC522 NFC module
- Serial/Wi-Fi communication between ESP32 and Java

---

## 📦 Features

- Detects NFC tag UID on scan
- Identifies user from UID
- Logs login time at first scan
- Logs logout time at second scan (on same day)
- Stores records with date-wise tracking
- Java GUI to view or manage logs

---

