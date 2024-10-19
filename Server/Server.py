import socket
import threading
import tkinter as tk
from tkinter import scrolledtext
from Crypto.Cipher import ChaCha20_Poly1305
from Crypto.Random import get_random_bytes
import base64
import json
import ArduinoConnection as arduino

class Server:
    def __init__(self, host='0.0.0.0', port=8080):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []
        self.accept_connections()

    def accept_connections(self):
        while True:  # Este while es para siempre escuchar nuevos clientes
            client_socket, addr = self.server_socket.accept()
            self.clients.append(client_socket)
            print(f"Conexión de {addr}")
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()  # Para que sea en hilo separado

    def handle_client(self, client_socket):
        while True:  # Siempre estar atento a recibir mensajes de cualquier cliente
            try:
                message = client_socket.recv(1024).decode('utf-8')  # recibe los mensajes

                if message:  # Si no hay mensaje
                    #Convierte el texto en formato json
                    data = json.loads(message)
            
                
                    
                    if data["action"] == "registro":
                        self.register(message, client_socket)  # mandar mensaje a todo mundo 
                    elif data["action"] == "login":
                        self.login(data, client_socket)
                    elif data["action"] == "arduino":
                        self.arduino(data, client_socket)  
                    
            except Exception as e:
                print(f"Surgió un Error: {e}")
                break  # Salir del bucle en caso de error

        client_socket.close()
        self.clients.remove(client_socket)  # elimina clientes cuando ya no están

    def login(self, data, sender_socket):
        self.travelFile(sender_socket, data)

    def arduino(self, data, sender_socket):
        arduino_connection = arduino.ArduinoConnection()
        arduino_connection.send(data["command"])
        #response = arduino_connection.receive()
        response = "Comando enviado"
        sender_socket.send(response.encode('utf-8'))
        #arduino_connection.close()
    

    def register(self, message, sender_socket):
        nonce, ciphertext, tag = self.encrypt_message(message)
        encrypted_message = self.compose_message(nonce, ciphertext, tag)
        if encrypted_message:
            print("Registro exitoso")
            self.save_encrypted_message(encrypted_message)
            self.travelFile()  # Leer y desencriptar el archivo

    def save_encrypted_message(self, encrypted_message):
        with open("register_encrypted.txt", "a") as f:  # Guardar mensajes cifrados
            f.write(base64.b64encode(encrypted_message).decode() + "\n")

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

    def travelFile(self,client_socket, data):
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
        
        # Si recorremos todo el archivo y no encontramos coincidencias
        
if __name__ == "__main__":
    Server()
