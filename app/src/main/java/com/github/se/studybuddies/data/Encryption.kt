package com.github.se.studybuddies.data

import android.app.Application
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encryption(studyBuddies: Application) {

  private val sharedPref: SharedPreferences =
      studyBuddies.getSharedPreferences("my_prefs", Application.MODE_PRIVATE)

  private val secretKeyPref = "SECRET_KEY_PREF"

  @Throws(Exception::class)
  fun generateSecretKey(): SecretKey? {
    val secureRandom = SecureRandom()
    val keyGenerator = KeyGenerator.getInstance("AES")
    // generate a key with secure random
    keyGenerator?.init(128, secureRandom)
    return keyGenerator?.generateKey()
  }

  private fun saveSecretKey(sharedPref: SharedPreferences, secretKey: SecretKey): String {
    val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
    sharedPref.edit().putString(secretKeyPref, encodedKey).apply()
    return encodedKey
  }

  private fun getSecretKey(sharedPref: SharedPreferences): SecretKey {

    val key = sharedPref.getString(secretKeyPref, null)

    if (key == null) {
      // generate secure random
      val secretKey = generateSecretKey()
      saveSecretKey(sharedPref, secretKey!!)
      return secretKey
    }

    val decodedKey = Base64.decode(key, Base64.NO_WRAP)
    val originalKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

    return originalKey
  }

  @Throws(Exception::class)
  fun readFile(filePath: String): ByteArray {
    val file = File(filePath)
    val fileContents = file.readBytes()
    val inputBuffer = BufferedInputStream(FileInputStream(file))

    inputBuffer.read(fileContents)
    inputBuffer.close()

    return fileContents
  }

  @Throws(Exception::class)
  fun saveFile(fileData: ByteArray, path: String) {
    val file = File(path)
    val bos = BufferedOutputStream(FileOutputStream(file, false))
    bos.write(fileData)
    bos.flush()
    bos.close()
  }

  @Throws(Exception::class)
  private fun encrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
    val data = yourKey.getEncoded()
    val keySpec = SecretKeySpec(data, 0, data.size, "AES")
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
    return cipher.doFinal(fileData)
  }

  fun encryptAndSaveFile(file: File) {
    Log.d("Encryption", "Trying to encrypt error")
    try {
      val fileData = readFile(file.path)

      // get secret key
      val secretKey = getSecretKey(sharedPref)
      // encrypt file
      val encodedData = secretKey?.let { encrypt(it, fileData) }

      if (encodedData != null) {
        saveFile(encodedData, file.path)
      }
    } catch (e: Exception) {
      Log.e("Encryption", "Encryption error")
    }
  }

  @Throws(Exception::class)
  fun decrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
    val decrypted: ByteArray
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
    decrypted = cipher.doFinal(fileData)
    return decrypted
  }

  fun decryptFile(file: File): ByteArray {
    val fileData = readFile(file.path)
    val secretKey = getSecretKey(sharedPref)
    return decrypt(secretKey, fileData)
  }
}
