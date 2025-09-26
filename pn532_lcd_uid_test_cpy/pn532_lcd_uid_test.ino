#include <Adafruit_PN532.h>
#include <Wire.h>
#include <LiquidCrystal.h>

// NFC (I2C)
Adafruit_PN532 nfc(-1, -1);

// LCD in 4-bit mode (RS, EN, D4, D5, D6, D7)
LiquidCrystal lcd(14, 27, 26, 25, 33, 32);

void setup(void) {
  Serial.begin(115200);
  lcd.begin(16, 2);
  lcd.print("Initializing...");

  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (!versiondata) {
    lcd.clear();
    lcd.print("PN532 Not Found");
    Serial.println("Didn't find PN532 :(");
    while (1);
  }

  nfc.SAMConfig();

  lcd.clear();
  lcd.print("Ready for Tag");
  Serial.println("Waiting for NFC Tag...");
}

void loop(void) {
  uint8_t uid[7];
  uint8_t uidLength;

  if (nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength)) {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Tag UID:");

    lcd.setCursor(0, 1);
    for (uint8_t i = 0; i < uidLength; i++) {
      if (uid[i] < 0x10) lcd.print("0"); // padding
      lcd.print(uid[i], HEX);
      lcd.print(" ");
    }

    Serial.print("UID: ");
    for (uint8_t i = 0; i < uidLength; i++) {
      Serial.print(uid[i], HEX);
      Serial.print(" ");
    }
//    for (uint8_t i = 0; i < uidLength; i++) {
//      Serial.print(uid[i], HEX);
//    }
    Serial.println();

    delay(1200);
    lcd.clear();
    lcd.print("Ready for Tag");
  }

  delay(500);
}
