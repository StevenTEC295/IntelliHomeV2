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
import ast
class ChatServer:
    def __init__(self, host='0.0.0.0', port=8080):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen(5)
        self.clients = []

        # Configuración de la interfaz gráfica 
        self.root = tk.Tk()
        self.root.title("Servidor de Chat")

        self.chat_display = scrolledtext.ScrolledText(self.root, state='disabled', width=50, height=20)
        self.chat_display.pack(pady=10)

        self.message_entry = tk.Entry(self.root, width=40)
        self.message_entry.pack(pady=5)

        self.send_button = tk.Button(self.root, text="Enviar", command=self.send_message_thread)
        self.send_button.pack(pady=5)

        self.quit_button = tk.Button(self.root, text="Salir", command=self.close_server)
        self.quit_button.pack(pady=5)

        # Hilo para manejar el servidor con el fin de que sea en hilos separados
        self.thread = threading.Thread(target=self.accept_connections)
        self.thread.start()

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
        while True: #Siempre estar atento a recibir mensajes de cualquier cliente
            try:
                message = client_socket.recv(1024) #recibe los mensajes
                #print(message)
                if message:
                    threading.Thread(target=self.manage_message, args=(message,client_socket,)).start()
                    #self.broadcast(message, client_socket) # mandar mensaje a todo mundo 
                else:
                    break
            except:
                break
        client_socket.close()
        self.clients.remove(client_socket) # elimina clientes cuando ya no están

    def manage_message(self, message, sender_socket):
        try:
            data = ast.literal_eval(message)
            print(data)
            if data['action'] == 'rq_housing':
                self.rq_housing(data, sender_socket)
            elif data['action'] == 'sv_house':
                self.sv_house(data, sender_socket)
            elif data['action'] == 'login':
                self.login(data, sender_socket)
            elif data['action'] == 'arduino':
                self.arduino(data, sender_socket)
            elif data['action'] == 'register':
                self.register(data, sender_socket)
        except Exception as e:
            pass
    def broadcast(self, message, sender_socket):
        self.chat_display.config(state='normal')
        self.chat_display.insert(tk.END, f"Cliente: {message}\n")
        self.chat_display.config(state='disabled')

        with open("chat_messages.txt", "a") as f:  # Abrir en modo append
            f.write(f"Cliente: {message}\n")


        # try:
        #     json_message = json.loads(message)
        #     # Verificar si el JSON contiene el campo "image" codificado en base64
        #     if 'image' in json_message:
        #         # Decodificar la imagen del base64
        #         image_data = base64.b64decode(json_message['image'])
                
        #         # Guardar la imagen como un archivo, por ejemplo "image_received.jpg"
        #         with open("image_received.jpg", "wb") as img_file:
        #             img_file.write(image_data)
                
        #         self.chat_display.config(state='normal')
        #         self.chat_display.insert(tk.END, "Imagen recibida y guardada como image_received.jpg\n")
        #         self.chat_display.config(state='disabled')
        # except json.JSONDecodeError:
        #     pass    
        
        for client in self.clients: # para cada cliente que haya
            if client != sender_socket:  # No enviar al remitente
                try:
                    client.send(message.encode('utf-8')) # envía el mensaje
                except:
                    client.close()
                    self.clients.remove(client)
                    
    def broadcast1(self, message, sender_socket): # Esto es para que sirva el boton
        self.chat_display.config(state='normal')
        self.chat_display.insert(tk.END, f"Servidor: {message}\n")
        self.chat_display.config(state='disabled')


        for client in self.clients:
                try:
                    client.send(message.encode('utf-8'))
                except:
                    client.close()
                    self.clients.remove(client)

    def send_message_thread(self):
        #Se debe agregar \n para que termine la cadena que se requiere enviar
        threading.Thread(target=self.broadcast1(self.message_entry.get()+"\n",None)).start()
        self.message_entry.delete(0, tk.END)  # Limpiar la entrada
    def send_message_to_clients(self):
        message = self.message_entry.get()
        if message:
            self.broadcast(f"Servidor: {message}", None)  # Enviar sin remitente
            
            self.message_entry.delete(0, tk.END)  # Limpiar la entrada

    def close_server(self):
        for client in self.clients:
            client.close()
        self.server_socket.close()
        self.root.destroy()

    def rq_housing(self, data, sender_socket):
        pass

    def sv_house(self, data, sender_socket):
        nonce, ciphertext, tag = self.encrypt_message(data)
        encrypted_message = self.compose_message(nonce, ciphertext, tag)
        if encrypted_message:
            print("Registro exitoso")
            self.save_encrypted_message(data["idPropertyRegister"],encrypted_message)
            #self.travelFile()  # Leer y desencriptar el archivo

    def login(self, data, sender_socket):
        self.travelFile(sender_socket, data)

    def arduino(self, data, sender_socket):
        print("Comando recibido")
        arduino_connection = arduino.ArduinoConnection(port="COM5")
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
            self.save_encrypted_message("register",encrypted_message)
            self.travelFile()  # Leer y desencriptar el archivo

    def save_encrypted_message(self, namefile,encrypted_message):
        with open(f"{namefile}.txt", "a") as f:  # Guardar mensajes cifrados
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
if __name__ == "__main__":
    ChatServer()
