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
const int debounceDelay = 50; // Tiempo de debounce en milisegundos
int lastDebounceTime = 0; // Último tiempo de cambio del estado

// Declaración de variables
String ServerMessage;
bool flameSensor;
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

void loop() {
  // Bucle principal para enviar datos de sensores siempre
  while (true) {
    // Verificar si hay datos disponibles en el puerto serial
    if (Serial.available()) { 
      ServerMessage = Serial.readStringUntil('\n'); // Leer el mensaje completo del servidor

      // Separar los datos de la cadena
      char *token = strtok(const_cast<char *>(ServerMessage.c_str()), ",");
      while (token != NULL) {
        // Control de LEDs y Servo
        if (strcmp(token, "B1_1") == 0) {
          digitalWrite(LED_BATH1, HIGH); // Enciende el LED del baño
        } else if (strcmp(token, "B1_0") == 0) {
          digitalWrite(LED_BATH1, LOW); // Apaga el LED del baño
        } else if (strcmp(token, "C1_1") == 0) {
          digitalWrite(LED_CUARTO1, HIGH); // Enciende el LED del cuarto 1
        } else if (strcmp(token, "C1_0") == 0) {
          digitalWrite(LED_CUARTO1, LOW); // Apaga el LED del cuarto 1
        } else if (strcmp(token, "C2_1") == 0) {
          digitalWrite(LED_CUARTO2, HIGH); // Enciende el LED del cuarto 2
        } else if (strcmp(token, "C2_0") == 0) {
          digitalWrite(LED_CUARTO2, LOW); // Apaga el LED del cuarto 2
        } else if (strcmp(token, "S1_1") == 0) {
          digitalWrite(LED_SALA, HIGH); // Enciende el LED de la sala
        } else if (strcmp(token, "S1_0") == 0) {
          digitalWrite(LED_SALA, LOW); // Apaga el LED de la sala
        } else if (strcmp(token, "SERVO_1") == 0) {
          // Mover el servo de 90 a 180 grados
          for (int pos = 90; pos <= 180; pos++) {
            myServo.write(pos); // Mover el servo a 'pos'
            delay(15);          // Pausa para permitir el movimiento suave
          }
          delay(1000); // Pausa de 1 segundo en 180 grados
          servoPos = 180; // Actualizar la posición actual
        } else if (strcmp(token, "SERVO_0") == 0) {
          // Mover el servo de regreso de 180 a 90 grados
          for (int pos = 180; pos >= 90; pos--) {
            myServo.write(pos); // Mover el servo a 'pos'
            delay(15);          // Pausa para permitir el movimiento suave
          }
          delay(1000); // Pausa de 1 segundo en 90 grados
          servoPos = 90; // Actualizar la posición actual
        }
        // Obtener el siguiente token
        token = strtok(NULL, ",");
      }
    }

    // --- Lectura de los sensores ---
    // Lectura del sensor de llama
    fireDetected = digitalRead(SENSOR_PIN);

    // Lectura del sensor de movimiento con debounce
    int currentSwitchState = digitalRead(ballSwitchPin);
    if (currentSwitchState != stableState) {
      lastDebounceTime = millis();
    }
    if ((millis() - lastDebounceTime) > debounceDelay) {
      if (currentSwitchState != switchState) {
        switchState = currentSwitchState;
      }
    }

    // Lectura del sensor de humedad
    humidity = dht.readHumidity();
    bool highHumidity = !isnan(humidity) && humidity >= 80;

    // Convertir a string el estado de los sensores
    String sensorStatus = String(highHumidity ? "1" : "0") + "," +
                          String(fireDetected ? "1" : "0") + "," +
                          String(switchState ? "1" : "0");

    // Enviar el mensaje al servidor
    Serial.println(sensorStatus);
    delay(500); // Pequeña espera para evitar saturar la comunicación serial
  }
}