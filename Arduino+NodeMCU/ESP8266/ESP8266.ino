#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"
// Set these to run example.
// #include <AsyncTimer.h>

#include <SoftwareSerial.h>
#include <Servo.h>
#define API_KEY "AIzaSyDWj1zCoSQIjiwRYABxbg1D5m8g-aOzw9k"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "https://submarine-378b6-default-rtdb.firebaseio.com/"
#define WIFI_SSID "N"
#define WIFI_PASSWORD "12341234"



FirebaseData fbdo;
// AsyncTimer t;
FirebaseAuth auth;
FirebaseConfig config;

SoftwareSerial NodeMcuSoftSerial(D1, D2);

Servo motor1, motor2;



bool signupOK = false;
char c;
String message;
void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(300);
    Serial.print(".");
  }
  /* Assign the api key (required) */
  config.api_key = API_KEY;
  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;
  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")) {
    signupOK = true;
  }
  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback;  //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  // t.setTimeout(functionTOCall, 100);
  NodeMcuSoftSerial.begin(57600);

  //D3 D4
  pinMode(D3, OUTPUT);
  pinMode(D4, OUTPUT);

  //TODO check wiring
  motor1.attach(D5);
  motor2.attach(D6);
  motor1.writeMicroseconds(1000);
  motor2.writeMicroseconds(1000);
}
void functionTOCall() {
  // t.setTimeout(functionTOCall, 100);
  while (NodeMcuSoftSerial.available() > 0) {
    c = NodeMcuSoftSerial.read();
    if (c == '\n') {
      if (Firebase.ready() && signupOK) {
        Serial.print(message + '\n');
        Firebase.RTDB.setInt(&fbdo, "data/gyroscope/x", split(message, 0).toInt());
        Firebase.RTDB.setInt(&fbdo, "data/gyroscope/y", split(message, 1).toInt());
        Firebase.RTDB.setInt(&fbdo, "data/realIndex1", split(message, 2).toInt());
        Firebase.RTDB.setInt(&fbdo, "data/realIndex2", split(message, 3).toInt());
        Firebase.RTDB.setInt(&fbdo, "data/realIndex3", split(message, 4).toInt());
        Firebase.RTDB.setInt(&fbdo, "data/realIndex4", split(message, 5).toInt());
      }

      c = 0;
      message = "";
      break;
    } else {
      message += c;
    }
  }
}
void loop() {
  // t.handle();
  if (Firebase.ready() && signupOK) {
    if (Firebase.RTDB.get(&fbdo, "/data")) {
      FirebaseJson json = fbdo.jsonObject();
      FirebaseJsonData index1, index2, index3, index4, indexAll, rightSpeed, leftSpeed;
      json.get(indexAll, "indexAll");
      json.get(index1, "index1");
      json.get(index2, "index2");
      json.get(index3, "index3");
      json.get(index4, "index4");
      json.get(rightSpeed, "motorRightSpeed");
      json.get(leftSpeed, "motorLeftSpeed");
      Serial.println(String(rightSpeed.intValue) + "," + String(leftSpeed.intValue));




      Serial.print(String(index1.intValue) + ',' + String(index2.intValue) + ','
                   + String(index3.intValue) + ',' + String(index4.intValue) + ','
                   + String(indexAll.intValue) + '\n');
      NodeMcuSoftSerial.print(String(index1.intValue) + ',' + String(index2.intValue)
                              + ',' + String(index3.intValue) + ',' + String(index4.intValue) + ','
                              + String(indexAll.intValue) + '\n');

      int right = rightSpeed.intValue;
      int left = leftSpeed.intValue;
      if (right < 0) {
        right *= -1;
        digitalWrite(D3, HIGH);
      } else {
        digitalWrite(D3, LOW);
      }
      if (left < 0) {
        left *= -1;
        digitalWrite(D4, HIGH);
      } else {
        digitalWrite(D4, LOW);
      }

      int val1 = map(right, 0, 100, 1000, 2000);
      motor1.writeMicroseconds(val1);
      int val2 = map(left, 0, 100, 1000, 2000);
      motor2.writeMicroseconds(val2);



      functionTOCall();
    }
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
  return "";
}
