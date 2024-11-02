#include <Servo.h>

#define LED_CUARTO1 5
#define LED_CUARTO2 6
#define LED_BATH1 10
#define LED_SALA 12
#define SENSOR_PIN 13

// Definición del pin del servo
const int SERVO_PIN = 9;
int servoPos = 0; // Posición inicial del servo

// Declaración de variables
String ServerMessage;
bool flameSensor;
bool fire;

Servo myServo; // Crea un objeto Servo para controlar el servo motor

void setup() {
  Serial.begin(9600); // Iniciar puerto serial a 9600 baud
  pinMode(LED_CUARTO1, OUTPUT);
  pinMode(LED_CUARTO2, OUTPUT);
  pinMode(LED_BATH1, OUTPUT);
  pinMode(LED_SALA, OUTPUT);
  pinMode(SENSOR_PIN, INPUT);

  // Configuración del servo
  myServo.attach(SERVO_PIN); // Conecta el servo al pin definido
  myServo.write(servoPos); // Mueve el servo a la posición inicial
}

void loop() {
  if (Serial.available()) { // Verificar si hay datos disponibles en el puerto serial
    ServerMessage = Serial.readStringUntil('\n'); // Leer el mensaje completo del servidor

    // Control de LEDs
    if (ServerMessage == "B1_1") {
      digitalWrite(LED_BATH1, HIGH);  // Enciende el LED del baño
    } else if (ServerMessage == "B1_0") {
      digitalWrite(LED_BATH1, LOW);   // Apaga el LED del baño
    }
    else if (ServerMessage == "C1_1") {
      digitalWrite(LED_CUARTO1, HIGH);  // Enciende el LED del cuarto 1
    } else if (ServerMessage == "C1_0") {
      digitalWrite(LED_CUARTO1, LOW);   // Apaga el LED del cuarto 1
    }
    else if (ServerMessage == "C2_1") {
      digitalWrite(LED_CUARTO2, HIGH);  // Enciende el LED del cuarto 2
    } else if (ServerMessage == "C2_0") {
      digitalWrite(LED_CUARTO2, LOW);   // Apaga el LED del cuarto 2
    }
    else if (ServerMessage == "S1_1") {
      digitalWrite(LED_SALA, HIGH);  // Enciende el LED de la sala
    } else if (ServerMessage == "S1_0") {
      digitalWrite(LED_SALA, LOW);   // Apaga el LED de la sala
    }

    // Control del servo motor (abrir y cerrar puerta)
    else if (ServerMessage == "SERVO_1") {
      servoPos = 180; // Posición para abrir la puerta
      myServo.write(servoPos);
      delay(1000); // Espera un segundo para permitir que el servo se mueva
    } else if (ServerMessage == "SERVO_0") {
      servoPos = 0; // Posición para cerrar la puerta
      myServo.write(servoPos);
      delay(1000); // Espera un segundo para permitir que el servo se mueva
    }
  }

  // Lectura del pin del sensor flame
  flameSensor = digitalRead(SENSOR_PIN);
  // Comportamiento con base al valor del sensor flame
  if (flameSensor && !fire) {
    Serial.write("Llama detectada!\n");
    fire = true;
  }
  if (!flameSensor && fire) {
    Serial.write("Llama apagada!\n");
    fire = false;
  }
  delay(200);
}


