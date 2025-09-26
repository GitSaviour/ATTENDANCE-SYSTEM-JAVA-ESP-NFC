# NFC-Based Attendance System using Java, JDBC, and ESP32
## Getting Started

This project is a smart login/logout time tracker using an **ESP32 + NFC reader** and a **Java Swing GUI**. It scans NFC cards to record attendance, logging users in at the first scan and logging them out at the second scan of the same day. All records are stored in a MySQL database and displayed via a desktop application.
Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

---
## Folder Structure

## 🛠 Tech Stack
The workspace contains two folders by default, where:

- Java (Swing) for GUI
- JDBC for database connectivity
- MySQL for storing attendance logs
- ESP32 with RC522 NFC module
- Serial/Wi-Fi communication between ESP32 and Java
- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

---
Meanwhile, the compiled output files will be generated in the `bin` folder by default.

## 📦 Features
> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.
- Detects NFC tag UID on scan
- Identifies user from UID
- Logs login time at first scan
- Logs logout time at second scan (on same day)
- Stores records with date-wise tracking
- Java GUI to view or manage logs

---
## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).