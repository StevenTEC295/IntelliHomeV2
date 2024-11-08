import socket
import threading
import tkinter as tk
from tkinter import scrolledtext
from Crypto.Cipher import ChaCha20_Poly1305
from Crypto.Random import get_random_bytes
import base64
import json
import time
import ArduinoConnection as arduino
import AlgoritmoBanquero
import os
class Server:
    def __init__(self, host='0.0.0.0', port=8080):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []
        self.arduino_connection = arduino.ArduinoConnection(port="COM6")


        self.flameDetection = 0
        self.humidity = 0
        self.sismo = 0

        # Configuración de la interfaz gráfica 
        self.root = tk.Tk()
        self.root.title("Servidor de Chat")

        self.chat_display = scrolledtext.ScrolledText(self.root, state='disabled', width=50, height=20)
        self.chat_display.pack(pady=10)

        self.message_entry = tk.Entry(self.root, width=40)
        self.message_entry.pack(pady=5)

        self.send_button = tk.Button(self.root, text="Enviar" )
        self.send_button.pack(pady=5)

        self.quit_button = tk.Button(self.root, text="Salir")
        self.quit_button.pack(pady=5)

        # Hilo para manejar el servidor con el fin de que sea en hilos separados
        self.thread = threading.Thread(target=self.accept_connections)
        self.thread.start()

        # Hilo para leer los mensajes del Arduino
        self.threadArduino = threading.Thread(target=self.read_arduino_msg)
        self.threadArduino.start()
        '''
        self.threadsendMessages = threading.Thread(target=self.send_message)
        self.threadsendMessages.start()'''

        self.root.protocol("WM_DELETE_WINDOW", self.close_server)
        self.root.mainloop()

    def accept_connections(self):
        
        while True: # Este while es para siempre escuchar nuevos clientes
            client_socket, addr = self.server_socket.accept()
            self.clients.append(client_socket)
            self.chat_display.config(state='normal')
            self.chat_display.insert(tk.END, f"Conexión de {addr}\n")
            self.chat_display.config(state='disabled')
            threading.Thread(target=self.handle_client, args=(client_socket,)).start() # Para que sea en hilo separado
    def handle_client(self, client_socket):
    
        try:
            
            message = client_socket.recv(4096).decode("utf-8")  # recibe los mensajes
            
            print(type(message))
            if message:  # Si no hay mensaje
                #Convierte el texto en formato json
                data = json.loads(message)
                #data = dict(message)
                print(data["action"])
        
            
                
                if data["action"] == "registro":
                    self.register(message, client_socket)  # mandar mensaje a todo mundo 
                elif data["action"] == "login":
                    self.login(data, client_socket)
                elif data["action"] == "arduino":
                    self.arduino(data, client_socket)  
                elif data["action"] == "rq_house":
                    self.rq_housing(client_socket)
                elif data["action"] == "sv_house":
                    self.sv_house(data, client_socket)
                elif data["action"] == "rq_sensors":
                    client_socket.send(f"{self.flameDetection},{self.humidity},{self.sismo}".encode('utf-8'))
                elif data["action"] == "a_Banquero":
                    self.sendABanquero(client_socket, data["day"], data["month"], data["IVA"], data["comission"], data["total"])
                elif data["action"] == "noti_casa_alquilada":
                    self.sendNotification(data["message"]);
                
        except Exception as e:
            print(f"Surgió un Error: {e}")
            

        client_socket.close()
        self.clients.remove(client_socket)  # elimina clientes cuando ya no están

    def sendNotification(self, mensaje):
        from twilio.rest import Client

        account_sid = 'AC40acaf58f18829153297016d9034da97'
        auth_token = '17a18fc3d2939eeac6692b273fccad08'
        client = Client(account_sid, auth_token)

        message = client.messages.create(
        from_='whatsapp:+14155238886',
        body=mensaje,
        to='whatsapp:+50661370491'
        )

        print(message.sid)

        
    def sendABanquero(self,socket, day, month, IVA, comission, total):
        aBanquero = AlgoritmoBanquero.AlgoritmoBanquero()
        socket.send(str(aBanquero.calculateNewQuantity(day, month, IVA, comission, total)).encode('utf-8')   )
    def rq_housing(self, sender_socket):
        self.iterarDirectorio(sender_socket)  # Leer y desencriptar el archivo
    def sv_house(self, data, sender_socket):
        data_str = str(data)
        nonce, ciphertext, tag = self.encrypt_message(data_str)
        encrypted_message = self.compose_message(nonce, ciphertext, tag)
        print(encrypted_message)
        if encrypted_message:
            print("Registro Casa Exitoso")
            self.create_encrypted_message(data["idPropertyRegister"],encrypted_message)
            
    def login(self, data, sender_socket):
        self.eval_login(sender_socket, data)

    def arduino(self, data, sender_socket):
        print("Comando recibido")
        print(data)
        self.arduino_connection.send(data["commands"])
        #response = arduino_connection.receive(
        #arduino_connection.close()
    

    def register(self, message, sender_socket):
        nonce, ciphertext, tag = self.encrypt_message(message)
        encrypted_message = self.compose_message(nonce, ciphertext, tag)
        if encrypted_message:
            print("Registro exitoso")
            self.save_encrypted_message("register",encrypted_message)
            self.eval_login()  # Leer y desencriptar el archivo

    def save_encrypted_message(self, namefile,encrypted_message):
        with open(f"{namefile}.txt", "a") as f:  # Guardar mensajes cifrados
            f.write(base64.b64encode(encrypted_message).decode() + "\n")
    def create_encrypted_message(self, namefile,encrypted_message):
        with open(f".//registroCasas//{namefile}.txt", "w") as f:  # Guardar mensajes cifrados
            f.write(base64.b64encode(encrypted_message).decode() + "\n")
            f.close()

    def encrypt_message(self, message):
        key = b'32_byte_secret_key_for_demo_use_only_'[:32]  # Clave fija de 32 bytes
        nonce = get_random_bytes(12)  # Generar un nonce único
        cipher = ChaCha20_Poly1305.new(key=key, nonce=nonce)
        ciphertext, tag = cipher.encrypt_and_digest(message.encode())
        return nonce, ciphertext, tag

    def decrypt_message(self, nonce, ciphertext, tag):
        key = b'32_byte_secret_key_for_demo_use_only_'[:32]  # Clave fija de 32 bytes
        cipher = ChaCha20_Poly1305.new(key=key, nonce=nonce)
        plaintext = cipher.decrypt_and_verify(ciphertext, tag)
        return plaintext.decode()

    def compose_message(self, nonce, ciphertext, tag):
        # Combina nonce, ciphertext y tag en un solo mensaje codificado
        return nonce + ciphertext + tag

    def extract_message(self, encoded_message):
        # Decodificar el mensaje
        decoded_message = base64.b64decode(encoded_message)
        nonce = decoded_message[:12]  # Los primeros 12 bytes son el nonce
        tag = decoded_message[-16:]  # Los últimos 16 bytes son el tag
        ciphertext = decoded_message[12:-16]  # El resto es el ciphertext
        return nonce, ciphertext, tag

    def eval_login(self,client_socket, data):
        with open("register_encrypted.txt", "r") as file:
            for line in file:
                nonce, ciphertext, tag = self.extract_message(line.strip())  # Decodificar línea del archivo
                try:
                    
                    # Desencriptar el mensaje
                    plaintext = self.decrypt_message(nonce, ciphertext, tag)
                    registro = json.loads(plaintext)  # Convertir el texto desencriptado en un diccionario
                    # Comparamos los datos recibidos (username, email o password) con los del registro
                    if (registro["username"] == data["usuario"] or registro['email'] == data["usuario"] or 
                        registro["phone"].split(" ")[1] == data["usuario"] and registro["password"] == data["password"]):
                        client_socket.send("1\n".encode('utf-8'))
                        print("Login exitoso")
                        return  # Salimos si el login fue exitoso
                        
    
    
                except Exception as e:
                    print(f"Error al desencriptar el mensaje: {e}")
                    client_socket.send("Login fallido".encode('utf-8'))
    

    def iterarDirectorio(self, client_socket):
        directorio = ".//registroCasas"
        all_data = []
        for file in os.listdir(directorio):
            filename = os.path.join(directorio, file)
            print(filename)
            data = self.returnHouse(filename)

            all_data.append(data.encode().decode('utf-8'))
        #print(all_data)
        clean_data = str(all_data).encode().decode('utf-8')
        print(clean_data)
        #client_socket.sendall(clean_data.encode("utf-8"))
        #msj = "Hola"
        client_socket.sendall((clean_data+"\n").encode("utf-8"))
    def returnHouse(self,namefile):
        with open(f"{namefile}", "r") as file:
            for line in file:
                nonce, ciphertext, tag = self.extract_message(line.strip())  # Decodificar línea del archivo
                try:
                    # Desencriptar el mensaje
                    plaintext = self.decrypt_message(nonce, ciphertext, tag)
                    """registro = json.dumps(plaintext)  # Convertir el texto desencriptado en un diccionario
                    print(registro)"""
                    return plaintext
                except Exception as e:
                    print(f"Error al desencriptar el mensaje: {e}")
        

        # Si recorremos todo el archivo y no encontramos coincidencias
    def close_server(self):
        for client in self.clients:
            client.close()
        self.server_socket.close()
        self.root.destroy()
        
    def read_arduino_msg(self):# Recibe el mensaje del flame, enviado desde el Arduino
        linea_anterior = ""
        while True:
            line = self.arduino_connection.read()
            line_split = line.split(",")
            print(line)
            if line_split[0] != "":
                
                self.humidity = line_split[0]
                self.flameDetection = line_split[1]
                self.sismo = line_split[2]

                if linea_anterior != line:
                    self.send_message()
                linea_anterior = line
                
                
        
                
    def send_message(self):
        while True:

            if self.sismo == "1":
                return self.sendNotification("Sismo detectado")
            if self.humidity == "1":
                return self.sendNotification("Humedad detectada")
            if self.flameDetection == "1":
                return self.sendNotification("Fuego detectado")
            #time.sleep(5)
             
            
if __name__ == "__main__":
    Server()