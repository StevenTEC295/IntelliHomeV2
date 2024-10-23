package com.example.IntelliHome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

object ImageController {
    fun selectPhotoFromGallery(activity: Activity, code: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, code)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, code)
    }

    /*fun saveImage(context: Context, id: Long, uri: Uri): String {
        val file = File(context.filesDir, id.toString())

        // Leer bytes de la imagen
        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!

        // Guardar la imagen en un archivo
        file.writeBytes(bytes)

        // Convertir los bytes de la imagen a Base64
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }*/

    //PARA CONVERTIR UNA IMAGEN A BASE 64
    fun convertImageToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    fun multiplephotos(activity: Activity, code: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, code)
    }
    //PARA CONVETIR VARIAS IMAGENES A BASE64
    fun multipleconvertImagesToBase64(context: Context, uris: List<Uri>): List<String> {
        val base64Images = mutableListOf<String>()
        uris.forEach { uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            base64Images.add(base64Image)
        }
        return base64Images
    }
    fun deleteImage(context: Context, id: Long) {
        val file = File(context.filesDir, id.toString())
        file.delete()
    }
}