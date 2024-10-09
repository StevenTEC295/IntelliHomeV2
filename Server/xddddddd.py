import socket
import threading
import tkinter as tk
from tkinter import scrolledtext
from Crypto.Cipher import ChaCha20_Poly1305
from Crypto.Random import get_random_bytes
import base64
import json  # Importamos json para trabajar con los registros de usuarios en formato JSON

class ChatServer:
    def __init__(self, host='0.0.0.0', port=8080):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []
        self.accept_connections()

    def accept_connections(self):
        while True:  # Siempre escuchar nuevos clientes
            client_socket, addr = self.server_socket.accept()
            self.clients.append(client_socket)
            print(f"Conexión de {addr}")
            threading.Thread(target=self.handle_client, args=(client_socket,)).start()  # Hilo separado por cliente

    def handle_client(self, client_socket):
        while True:  # Siempre estar atento a recibir mensajes de cualquier cliente
            try:
                message = client_socket.recv(1024).decode('utf-8')  # Recibe el mensaje del cliente (datos de login)
                if message:
                    login_data = json.loads(message)  # Suponemos que el cliente manda los datos en JSON
                    # Vamos a verificar el login con la función travelFile
                    self.travelFile(client_socket, login_data)
            except Exception as e:
                print(f"Error: {e}")
                break  # Salir del bucle en caso de error

        client_socket.close()
        self.clients.remove(client_socket)  # Elimina cliente cuando se desconecta

    def broadcast(self, message, sender_socket):
        nonce, ciphertext, tag = self.encrypt_message(message)
        encrypted_message = self.compose_message(nonce, ciphertext, tag)
        if encrypted_message:
            print("Mensaje cifrado:", encrypted_message)
            self.save_encrypted_message(encrypted_message)

    def save_encrypted_message(self, encrypted_message):
        with open("chat_messages_encrypted.txt", "a") as f:  # Guardar mensajes cifrados
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

    # Modificamos travelFile para verificar login con username, correo y contraseña
    def travelFile(self, client_socket, data):
        """
        Verifica si el nombre de usuario, correo electrónico o contraseña existen en el archivo cifrado de registros.
        """
        with open("user_records_encrypted.txt", "r") as file:
            for line in file:
                nonce, ciphertext, tag = self.extract_message(line.strip())  # Decodificar línea del archivo
                try:
                    # Desencriptar el mensaje
                    plaintext = self.decrypt_message(nonce, ciphertext, tag)
                    registro = json.loads(plaintext)  # Convertir el texto desencriptado en un diccionario

                    # Comparamos los datos recibidos (username, email o password) con los del registro
                    if (registro["username"] == data["username"] or
                        registro["email"] == data["email"] or
                        registro["password"] == data["password"]):
                        client_socket.send("Login exitoso".encode('utf-8'))
                        return  # Salimos si el login fue exitoso
                except Exception as e:
                    print(f"Error al desencriptar el mensaje: {e}")
        
        # Si recorremos todo el archivo y no encontramos coincidencias
        client_socket.send("Login fallido".encode('utf-8'))

if __name__ == "__main__":
    ChatServer()
