"""
Este script es un cliente de socket que se conecta al servidor y envía un mensaje en formato JSON.

TESTING ONLY!!!
"""
import socket
import json as js
def cliente_socket():
    # Definir la dirección IP y el puerto del servidor
    host = '192.168.0.207'  # Dirección IP del servidor (puedes cambiarlo según tu servidor)
    port = 8080         # Puerto que está escuchando el servidor

    # Crear un socket
    cliente = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    try:
        # Conectar al servidor
        cliente.connect((host, port))
        print(f"Conectado al servidor {host}:{port}")

        # Enviar datos
        #json = {"action": "sv_house","idPropertyRegister":"54545454", "location":"jdfsdjfds", "typeofHouse":"Rústica", "availabitily": "no se","cantofPeople":"5","amenidades":["algo","algo","algo"],"rules":"nada", "price":"5000"}
        json = {"action": "rq_house"}
        #json = {"action":"a_Banquero", "day": 15, "month": 9, "IVA": 1.5, "comission": 200, "total": 2000}
        
        mensaje = js.dumps(json)  # Convertir el diccionario a cadena JSON
        cliente.sendall(mensaje.encode('utf-8'))  # Enviar mensaje codificado en utf-8
        print(f"Mensaje enviado: {mensaje}")

        # Recibir respuesta del servidor
        respuesta = cliente.recv(4096)  # Recibir hasta 1024 bytes de respuesta
        print(f"Respuesta del servidor: {respuesta.decode('utf-8')}")

    except Exception as e:
        print(f"Error: {e}")

    

if __name__ == "__main__":
    cliente_socket()