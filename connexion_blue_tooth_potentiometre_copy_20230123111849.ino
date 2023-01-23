#include "Arduino.h"
#include "BluetoothSerial.h"


// Le potentiomètre est connecté au GPIO 35 (Pin VP)
const int potPin = 35;
int valeurMapee;

BluetoothSerial SerialBT;

// Valeur du potentiomètre
int potValue = 0.0;

void setup() {
SerialBT.begin("PONGACCESS");
SerialBT.println("module bluetooth initialisé");

Serial.begin(115200);
delay(1000);
pinMode(potPin,INPUT_PULLUP);
}
void loop() {
// Mesure la valeur du potentiomètre
static uint16_t i = 0;
i++;
potValue = analogRead(potPin);
valeurMapee = map(potValue,0,4095,-20,20);
//Serial.println(potValue);
Serial.println(valeurMapee);
SerialBT.println(valeurMapee);

delay(250);
}