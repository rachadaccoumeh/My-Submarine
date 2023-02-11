#include <Wire.h>
#include <AsyncTimer.h>
#include <SoftwareSerial.h>
SoftwareSerial ArduinoSoftSerial(10, 11);
const int MPU = 0x68;
AsyncTimer t;
void setup() {
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  Wire.begin();
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  Serial.begin(115200);
  ArduinoSoftSerial.begin(57600);
  t.setTimeout(functionTOCall, 1000);
}

void functionTOCall() {
  t.setTimeout(functionTOCall, 1000);
  Wire.beginTransmission(MPU);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 14, true);
  int16_t AcX = Wire.read() << 8 | Wire.read();
  int16_t AcY = Wire.read() << 8 | Wire.read();
  int16_t AcZ = Wire.read() << 8 | Wire.read();
  int minVal = 265;
  int maxVal = 402;
  int xAng = map(AcX, minVal, maxVal, -90, 90);
  int yAng = map(AcY, minVal, maxVal, -90, 90);
  int zAng = map(AcZ, minVal, maxVal, -90, 90);
  int x = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
  int y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
  int z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);
  ArduinoSoftSerial.print(String(x) + ',' + String(y) + ',' + String(analogRead(A0)) 
  + ',' + String(analogRead(A1)) + ',' + String(analogRead(A2)) + ',' 
  + String(analogRead(A3)) + '\n');
  Serial.print(String(x) + ',' + String(y) + ',' + String(analogRead(A0)) + ',' 
  + String(analogRead(A1)) + ',' + String(analogRead(A2)) + ',' 
  + String(analogRead(A3)) + '\n');
}


// 2 right top forward
// 3 right top backward
char c;
String message;
void loop() {
  t.handle();
  while (ArduinoSoftSerial.available() > 0) {
    c = ArduinoSoftSerial.read();
    if (c == '\n') {
      Serial.print(message + '\n');
      //int indexAll = split(message, 4).toInt();
      int index1 = split(message, 0).toInt();
      int index2 = split(message, 1).toInt();
      int index3 = split(message, 2).toInt();
      int index4 = split(message, 3).toInt();
      updateMotor(map(index1, 0, 30, 0, 1024), 2, 3);
      updateMotor(map(index2, 0, 30, 0, 1024), 4, 5);
      updateMotor(map(index3, 0, 30, 0, 1024), 6, 7);
      updateMotor(map(index4, 0, 30, 0, 1024), 8, 9);
      c = 0;
      message = "";
      break;
    } else {
      message += c;
    }
  }
}
void updateMotor(int index, int forward, int backward) {
  int theIndex = 0;
  if (forward == 2) {
    theIndex = analogRead(A0);
  } else if (forward == 4) {
    theIndex = analogRead(A1);
  } else if (forward == 6) {
    theIndex = analogRead(A2);
  } else if (forward == 8) {
    theIndex = analogRead(A3);
  }
  const int margin = 200;
  if (theIndex >= index - margin && theIndex <= index + margin) {
    digitalWrite(forward, LOW);
    digitalWrite(backward, LOW);
  } else if (theIndex > index) {
    digitalWrite(forward, HIGH);
    digitalWrite(backward, LOW);
  } else if (theIndex < index) {
    digitalWrite(forward, LOW);
    digitalWrite(backward, HIGH);
  }
}

String split(String text, int index) {
  String result = "";
  for (int x = 0; x < text.length(); x++) {
    if (text.charAt(x) != ',' && x != text.length() - 1) {
      result += text.charAt(x);
    } else {
      if (index == 0) {
        result += text.charAt(x);
        return result;
      } else {
        result = "";
        index--;
      }
    }
  }
}