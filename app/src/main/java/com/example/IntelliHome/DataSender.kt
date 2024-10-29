package com.example.IntelliHome

import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

class DataSender(private val serverIp: String, private val serverPort: Int) {
    //Esta funcion es para mandar los datos al servidor en formato JSON
    fun sendDataToServer(jsonData: String) {
        try {
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            val printWriter = PrintWriter(outputStream, true)

            printWriter.println(jsonData)
            outputStream.close()
            printWriter.close()
            socket.close()
            println("Se cerró la conexión - envío")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al enviar los datos - envío")
        }
    }
}
