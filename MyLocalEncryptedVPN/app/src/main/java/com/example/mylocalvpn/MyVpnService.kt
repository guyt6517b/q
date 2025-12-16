package com.example.mylocalvpn

  import android.net.VpnService
  import android.os.ParcelFileDescriptor
  import java.io.FileInputStream
  import java.io.FileOutputStream
  import java.security.MessageDigest
  import javax.crypto.Cipher
  import javax.crypto.spec.SecretKeySpec

  class MyVpnService : VpnService(), Runnable {
      private var iface: ParcelFileDescriptor? = null
      private var thread: Thread? = null

      override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
          thread = Thread(this)
          thread!!.start()
          return START_STICKY
      }

      override fun run() {
          iface = Builder()
              .setSession("LocalEncryptedVPN")
              .addAddress("10.0.0.2", 32)
              .addRoute("0.0.0.0", 0)
              .establish()

          val input = FileInputStream(iface!!.fileDescriptor)
          val output = FileOutputStream(iface!!.fileDescriptor)

          val key = MessageDigest.getInstance("SHA-256")
              .digest("local-demo-key".toByteArray())
          val cipher = Cipher.getInstance("AES")
          cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))

          val buffer = ByteArray(32767)
          while (true) {
              val len = input.read(buffer)
              if (len > 0) {
                  val encrypted = cipher.doFinal(buffer.copyOf(len))
                  output.write(encrypted)
              }
          }
      }
  }
  