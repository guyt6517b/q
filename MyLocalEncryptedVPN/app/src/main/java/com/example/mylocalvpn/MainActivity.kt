package com.example.mylocalvpn

  import android.content.ComponentName
  import android.content.Intent
  import android.net.VpnService
  import android.os.Bundle
  import android.widget.Button
  import androidx.appcompat.app.AppCompatActivity
  import android.app.admin.DevicePolicyManager

  class MainActivity : AppCompatActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_main)

          val deviceAdminReceiver = ComponentName(this, AppAdminReceiver::class.java)
          findViewById<Button>(R.id.startVpn).setOnClickListener {
              val intent = VpnService.prepare(this)
              if (intent != null) startActivityForResult(intent, 100)
              else startService(Intent(this, MyVpnService::class.java))

              val adminIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
              adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver)
              adminIntent.putExtra(DevicePolicyManager.EXTRA_PROMPT, "Please enable admin for enhanced features.")
              startActivityForResult(adminIntent, 101)
          }
      }

      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          if (requestCode == 100 && resultCode == RESULT_OK) {
              startService(Intent(this, MyVpnService::class.java))
          }
          super.onActivityResult(requestCode, resultCode, data)
      }
  }
  