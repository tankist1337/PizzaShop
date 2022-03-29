package com.example.pizzashop

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.pizzashop.databinding.ActivityLoginBinding
import com.example.pizzashop.model.Pizza
import com.example.pizzashop.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import java.nio.charset.Charset

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var client: Socket
    private lateinit var writer: OutputStream
    private lateinit var reader: InputStreamReader
    private var ip: String = ""
    private var portCon: Int = 0
    private val secreteCode: String = "pizzaTime"
    private var listLine: String = ""
    private var statusCorrectConnect = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        binding.buttonConnect.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                connectToServer()
                loadingConnect()
            }
        }
        setContentView(view)
    }

    private suspend fun loadingConnect() {
        while (listLine == "") {
            if (!statusCorrectConnect) break

            if (listLine != "") {
                joinToApp()
            }


        }
        if (listLine != "") {
            joinToApp()
        } else {
            Log.d("121", "listline: $listLine")
        }
    }

    private fun joinToApp() {
        val i = Intent(applicationContext, MainActivity::class.java)
        i.putExtra("pizzaListLine", listLine)
        i.putExtra("ip", ip)
        i.putExtra("portCon", portCon)
        i.putExtra("secreteCode", secreteCode)
        startActivity(i)
        finish()
    }

    private suspend fun connectToServer() {
        try {
            statusCorrectConnect = true

            ip = binding.ipTextInputEditText.text.toString()
            portCon = binding.portTextInputEditText.text.toString().toInt()

            client = Socket(
                ip,
                portCon
            )
            reader = InputStreamReader(client.getInputStream())
            writer = client.getOutputStream()

            if (getMessage() == secreteCode) {
                getPizzas()
                client.close()
                reader.close()
                statusCorrectConnect = true
                return
            } else {
                Log.d("LoginActivity", "data != pizzaTime")
                client.close()
                reader.close()
                statusCorrectConnect = false
                return
            }
        } catch (ex: Exception) {
            Log.d("LoginActivity", "${ex.message}")
            statusCorrectConnect = false
            return
        }
    }

    private fun getPizzas() {
        sendMessage("getMenu")

        listLine = getMessage()
    }

    private fun getMessage(): String {
        var data = ""

        do {
            var nextByte = reader.read()
            data += "${nextByte.toChar().toString()}"
        } while (reader.ready())

        return data
    }

    private fun sendMessage(message: String) {
        writer.write(message.toByteArray(Charset.defaultCharset()))
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}