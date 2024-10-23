//controlador del leds 
#define LED_CUARTO1 5
#define LED_CUARTO2 6
#define LED_BATH1 10
#define LED_SALA 12
//Declaracion de variables
String ServerMessage;

void setup() {
  Serial.begin(9600);// Iniciar puerto serial a 9600 baud
  pinMode(LED_CUARTO1, OUTPUT); // Definir el pin del cuarto 1 como salida
  pinMode(LED_CUARTO2, OUTPUT); // Definir el pin del cuarto 2 como salida
  pinMode(LED_BATH1, OUTPUT);   // Definir el pin del baño como salida
  pinMode(LED_SALA, OUTPUT);    // Definir el pin de la sala como salida
}

void loop() {
  if (Serial.available()) { // Verificar si hay datos disponibles en el puerto serial
  ServerMessage = Serial.readStringUntil('\n');
  
   //leer strings del puerto serial hasta encontrar el caracter de nueva linea
  // Se usan claves como:
  //Baño: On = B1_1 Off = B1_0
  //Cuarto1 : On = C1_1 Off = C1_0
  //Cuarto2 : On = C2_1 Off = C2_0
  //Sala: On = S1_1 Off = S1_0
  // LED del baño
  if (ServerMessage == "B1_1"){
    digitalWrite(LED_BATH1, HIGH);  // Enciende el led del baño
  }else if(ServerMessage == "B1_0"){
    digitalWrite(LED_BATH1, LOW);  // Apaga el led del baño
  }
  //LED del cuarto 1
  else if (ServerMessage == "C1_1") {
      digitalWrite(LED_CUARTO1, HIGH);  // Enciende el LED del cuarto 1
    } else if (ServerMessage == "C1_0") {
      digitalWrite(LED_CUARTO1, LOW);   // Apaga el LED del cuarto 1
    }

  //LED del cuarto 2
  else if (ServerMessage == "C2_1") {
      digitalWrite(LED_CUARTO2, HIGH);  // Enciende el LED del cuarto 2
    } else if (ServerMessage == "C2_0") {
      digitalWrite(LED_CUARTO2, LOW);   // Apaga el LED del cuarto 2
    }
  //LED de la sala
  else if (ServerMessage == "S1_1") {
      digitalWrite(LED_SALA, HIGH);  // Enciende el LED de la sala
    } else if (ServerMessage == "S1_0") {
      digitalWrite(LED_SALA, LOW);   // Apaga el LED de la sala
    } 
  } 
}