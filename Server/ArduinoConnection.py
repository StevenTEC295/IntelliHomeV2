import serial
import time

class ArduinoConnection:
    def __init__(self, port, baudrate=9600):
        
        self.arduino = serial.Serial(port, baudrate, timeout=1)
        time.sleep(2)
        

    def send(self, data):
        self.arduino.write(data.encode())

    def receive(self):
        print( self.arduino.readline().decode())
    
    def read(self):
        return str(self.arduino.readline().decode('utf-8').strip())
        
        
    def close(self):
        self.arduino.close()
    