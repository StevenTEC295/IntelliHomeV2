package com.example.IntelliHome;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class SocketConnection {
    private  Socket socket;
    private PrintWriter out;
    private Scanner in;
    public  void startConnection(){

        new Thread(() -> {
            try {

                // Cambiar a la dirección IP de su servidor
                socket = new Socket("192.168.0.207", 8000);
                System.out.println("Conectado");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void sendMessage(String message) {
        new Thread(() -> { // un hilo por a parte
            try {
                if (out != null) {
                    out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
