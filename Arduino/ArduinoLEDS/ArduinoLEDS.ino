#include <Servo.h>
#include <DHT.h>
#include <DHT_U.h>

#define LED_CUARTO1 5
#define LED_CUARTO2 6
#define LED_BATH1 10
#define LED_SALA 12
#define SENSOR_PIN 13
#define DHT_TYPE DHT11

// Definición del pin del servo
const int SERVO_PIN = 9;
int servoPos = 0; // Posición inicial del servo
const int ballSwitchPin = 2; // Pin para el sensor de movimiento
int stableState = 0; // Estado estable del sensor de movimiento
int switchState = 0; // Estado actual del sensor de movimiento
int lastSwitchState = 0; // Último estado del sensor de movimiento
int dhtPin = 3; // Pin para el sensor DHT11
DHT dht(dhtPin, DHT_TYPE); // Crear instancia del DHT
int humidity; // Variable para almacenar la humedad
const int debounceDelay = 100; // Tiempo de debounce en milisegundos
int lastDebounceTime = 0; // Último tiempo de cambio del estado

// Declaración de variables
String ServerMessage;
bool fireDetected = false; // Indica si hay llama detectada

Servo myServo; // Crea un objeto Servo para controlar el servo motor

void setup() {
  Serial.begin(9600); // Iniciar puerto serial a 9600 baud
  pinMode(LED_CUARTO1, OUTPUT);
  pinMode(LED_CUARTO2, OUTPUT);
  pinMode(LED_BATH1, OUTPUT);
  pinMode(LED_SALA, OUTPUT);
  pinMode(SENSOR_PIN, INPUT);
  pinMode(ballSwitchPin, INPUT);

  // Configuración del servo
  myServo.attach(SERVO_PIN); // Conecta el servo al pin definido
  myServo.write(servoPos); // Mueve el servo a la posición inicial
  dht.begin();
}

void controlDevices() {
  if (Serial.available()) {
    ServerMessage = Serial.readStringUntil('\n'); // Leer el mensaje completo del servidor
    char *token = strtok(const_cast<char *>(ServerMessage.c_str()), ",");
    while (token != NULL) {
      if (strcmp(token, "B1_1") == 0) {
        digitalWrite(LED_BATH1, HIGH);
      } else if (strcmp(token, "B1_0") == 0) {
        digitalWrite(LED_BATH1, LOW);
      } else if (strcmp(token, "C1_1") == 0) {
        digitalWrite(LED_CUARTO1, HIGH);
      } else if (strcmp(token, "C1_0") == 0) {
        digitalWrite(LED_CUARTO1, LOW);
      } else if (strcmp(token, "C2_1") == 0) {
        digitalWrite(LED_CUARTO2, HIGH);
      } else if (strcmp(token, "C2_0") == 0) {
        digitalWrite(LED_CUARTO2, LOW);
      } else if (strcmp(token, "S1_1") == 0) {
        digitalWrite(LED_SALA, HIGH);
      } else if (strcmp(token, "S1_0") == 0) {
        digitalWrite(LED_SALA, LOW);
      } else if (strcmp(token, "SERVO_1") == 0) {
        moveServo(90, 180);
      } else if (strcmp(token, "SERVO_0") == 0) {
        moveServo(180, 90);
      }
      token = strtok(NULL, ",");
    }
  }
}

void moveServo(int start, int end) {
  if (start < end) {
    for (int pos = start; pos <= end; pos++) {
      myServo.write(pos);
      delay(15);
    }
  } else {
    for (int pos = start; pos >= end; pos--) {
      myServo.write(pos);
      delay(15);
    }
  }
  delay(1000);
  servoPos = end;
}

void readFireSensor() {
  fireDetected = digitalRead(SENSOR_PIN);
}

void readMotionSensor() {
  int currentSwitchState = digitalRead(ballSwitchPin);
  if (currentSwitchState != stableState) {
    lastDebounceTime = millis();
  }
  if ((millis() - lastDebounceTime) > debounceDelay) {
    if (currentSwitchState != switchState) {
      switchState = currentSwitchState;
    }
  }
}

void readHumiditySensor() {
  humidity = dht.readHumidity();
  if (isnan(humidity)) {
    Serial.println("Error en la lectura del sensor DHT11");
  }
}

void loop() {
  controlDevices();

  // Leer sensores
  readFireSensor();
  readMotionSensor();
  readHumiditySensor();

  // Verificar condiciones de sensores
  bool highHumidity = !isnan(humidity) && humidity >= 90;

  // Enviar el estado de los sensores al servidor
  String sensorStatus = String(highHumidity ? "1" : "0") + "," +
                        String(fireDetected ? "1" : "0") + "," +
                        String(switchState ? "1" : "0");

  Serial.println(sensorStatus);
  delay(500); // Pequeña espera para evitar saturar la comunicación serial
}
