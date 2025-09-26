#include <Adafruit_PN532.h>
#include <Wire.h>
#include <LiquidCrystal.h>

// NFC (I2C)
Adafruit_PN532 nfc(-1, -1);

// LCD in 4-bit mode (RS, EN, D4, D5, D6, D7)
LiquidCrystal lcd(14, 27, 26, 25, 33, 32);

// Buzzer pin
const int buzzerPin = 4;

void setup(void) {
  Serial.begin(115200);
  lcd.begin(16, 2);
  lcd.print("Initializing...");

  pinMode(buzzerPin, OUTPUT);
  digitalWrite(buzzerPin, LOW); // keep buzzer off at start

  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (!versiondata) {
    lcd.clear();
    lcd.print("PN532 Not Found");
    while (1);
  }

  nfc.SAMConfig();

  lcd.clear();
  lcd.print("Ready for Tag");
}

void loop(void) {
  uint8_t uid[7];
  uint8_t uidLength;

  if (nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength)) {
    // ✅ Buzz for 100 ms
    digitalWrite(buzzerPin, HIGH);
    delay(300);
    digitalWrite(buzzerPin, LOW);

    // Show UID on LCD
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Tag UID:");

    lcd.setCursor(0, 1);
    for (uint8_t i = 0; i < uidLength; i++) {
      if (uid[i] < 0x10) lcd.print("0"); // padding
      lcd.print(uid[i], HEX);
      lcd.print(" ");
    }

    // Print UID on Serial
//    Serial.print("UID: ");
//    for (uint8_t i = 0; i < uidLength; i++) {
//      Serial.print(uid[i], HEX);
//      Serial.print(" ");
//    }
    for (uint8_t i = 0; i < uidLength; i++) {
      if (uid[i] < 0x10) Serial.print("0");
      Serial.print(uid[i], HEX);
    }
    Serial.println();

    delay(2500);
    lcd.clear();
    lcd.print("Ready for Tag");
  }

  delay(100);
}
