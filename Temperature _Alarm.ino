#include <dht.h>
#define temp_sensor A0
#define led_pin 9
#define buzzer_pin 10
#define fire_point 25
dht DHT;
  //int fire_point=0;
void setup() {
  pinMode(temp_sensor, INPUT);
  pinMode(led_pin, OUTPUT);
  pinMode(buzzer_pin, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  DHT.read11(temp_sensor);
  int temp_value =  DHT.temperature;

  if (temp_value > fire_point)
  {
    digitalWrite(led_pin, HIGH);
    digitalWrite(buzzer_pin, HIGH);
  }
  else
  {
    digitalWrite(led_pin, LOW);
    digitalWrite(buzzer_pin, LOW);
  }
  Serial.print(temp_value);
  delay(1000);
}
