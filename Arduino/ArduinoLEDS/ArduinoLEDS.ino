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

int switchState = 0; // Estado actual del sensor de movimiento
int lastSwitchState = 0; // Último estado del sensor de movimiento
int dhtPin = 3; // Pin para el sensor DHT11
DHT dht(dhtPin, DHT_TYPE); // Crear instancia del DHT
int humidity; // Variable para almacenar la humedad

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
        servoPos = 180; // Posición para abrir la puerta
        myServo.write(servoPos);
        delay(1000); // Espera un segundo para permitir que el servo se mueva
      } else if (strcmp(token, "SERVO_0") == 0) {
        servoPos = 0; // Posición para cerrar la puerta
        myServo.write(servoPos);
        delay(1000); // Espera un segundo para permitir que el servo se mueva
      }
      // Obtener el siguiente token
      token = strtok(NULL, ",");
    }
  }

  // --- Lectura de los sensores ---
  bool updated = false; // Bandera para enviar el mensaje solo si algún sensor cambia

  // Lectura del sensor de llama
  bool currentFlameState = digitalRead(SENSOR_PIN);
  if (currentFlameState != fireDetected) {
    fireDetected = currentFlameState;
    updated = true; // Marca que hubo un cambio

    // Mensaje de alerta de fuego
    if (fireDetected) {
      Serial.println("Alerta fuego!");
    } else {
      Serial.println("Fuego apagado");
    }
  }

  // Lectura del sensor de movimiento
  switchState = digitalRead(ballSwitchPin);
  if (switchState != lastSwitchState) {
    lastSwitchState = switchState;
    updated = true; // Marca que hubo un cambio

    // Mensaje de alerta de sismo/movimiento
    if (switchState == HIGH) {
      Serial.println("Alerta sismo!");
    } else {
      Serial.println("Movimiento detenido");
    }
  }

  // Lectura del sensor de humedad
  humidity = dht.readHumidity();
  bool highHumidity = !isnan(humidity) && humidity >= 80;
  
  // Si la humedad pasa el umbral y no estaba activado antes
  static bool humidityAlert = false;
  if (highHumidity != humidityAlert) {
    humidityAlert = highHumidity;
    updated = true; // Marca que hubo un cambio

    // Mensaje de alerta de humedad
    if (humidityAlert) {
      Serial.println("Alerta humedad!");
    } else {
      Serial.println("Humedad normal");
    }
  }

  // Si hay algún cambio, construir y enviar el mensaje
  if (updated) {
    String sensorStatus = String(humidityAlert ? "1" : "0") + "," +
                          String(fireDetected ? "1" : "0") + "," +
                          String(switchState ? "1" : "0");

    // Enviar el mensaje al servidor
    Serial.println(sensorStatus);
  }

  delay(200);
}
