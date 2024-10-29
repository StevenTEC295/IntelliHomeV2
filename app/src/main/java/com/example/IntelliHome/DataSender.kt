package com.example.IntelliHome

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.Scanner

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

    fun receiveDataFromServer(): String? {
        return try {
            val socket = Socket(serverIp, serverPort)
            val inputStream: InputStream = socket.getInputStream()
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            val receivedData = bufferedReader.readLine()
            bufferedReader.close()
            inputStream.close()
            socket.close()
            println("Se cerró la conexión - recepción")
            receivedData
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al recibir los datos - recepción")
            null
        }
    }


    fun receiveDataFromServerScanner(): String? {
        return try {
            val socket = Socket(serverIp, serverPort)
            val inputStream: InputStream = socket.getInputStream()
            val scanner = Scanner(inputStream)

            val receivedData = if (scanner.hasNextLine()) scanner.nextLine() else null
            scanner.close()
            inputStream.close()
            socket.close()
            println("Se cerró la conexión - recepción")
            receivedData
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al recibir los datos - recepción")
            null
        }
    }
}
