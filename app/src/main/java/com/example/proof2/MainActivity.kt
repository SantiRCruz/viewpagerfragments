package com.example.proof2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.JsonWriter
import android.util.Log
import android.view.View
import androidx.core.util.PatternsCompat
import androidx.core.widget.addTextChangedListener
import com.example.proof2.core.Constants
import com.example.proof2.core.networkInfo
import com.example.proof2.data.post
import com.example.proof2.data.signIn
import com.example.proof2.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var countErrors = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        clicks()
        writing()

    }

    private fun writing() {
        emailValidateWriting()
    }
    private fun errorNotification(){
        var time:Long = 6000
        countErrors += 1
        if (countErrors==3){
            binding.btnSignIn.visibility = View.GONE
            binding.txtCountDown.visibility = View.VISIBLE
            binding.txtCountDown.text = time.toString()
            object : CountDownTimer(time,100) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.txtCountDown.text = millisUntilFinished.toString()

                }

                override fun onFinish() {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.txtCountDown.visibility = View.GONE
                }
            }.start()
            countErrors = 0
        }
    }

    private fun emailValidateWriting() {
        val regex = Pattern.compile("^([a-zA-Z0-9]{1,}@[a-z]{3,}[.]{1,1}[a-z]{2,})")
        binding.edtLogin.addTextChangedListener {
            if (!regex.matcher(it.toString()).matches()) {
                binding.btnSignIn.isEnabled = false
                binding.edtLogin.error = "Error en la forma del email"
            } else {
                binding.btnSignIn.isEnabled = true
                binding.edtLogin.error = null
            }
        }
    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val result = arrayOf(validateEmail(), validatePassword())
        if (false in result) {
            errorNotification()
            return
        }

        if (!networkInfo(this)){
            alertMessage("You don't have internet connection")
            return
        }

        sendSignIn()

    }

    private fun sendSignIn() {
        Constants.okhttp.newCall(post("signin/", signIn(binding.edtLogin.text.toString(),binding.edtPassword.text.toString()))).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ",e.message.toString() )
                runOnUiThread {
                    alertMessage("Error from server!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")){
                    val data = json.getJSONObject("data")
                    val sharedPreferences = getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()){
                        putString("id",data.getString("id"))
                        putString("name",data.getString("name"))
                        putString("token",data.getString("token"))
                        apply()
                    }
                    val i = Intent(this@MainActivity,HomeActivity::class.java)
                    startActivity(i)
                }else{
                    runOnUiThread {
                        alertMessage("Wrong credentials")
                    }
                }
            }
        })
    }

    private fun validatePassword(): Boolean {
        return if (binding.edtPassword.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else {
            binding.edtPassword.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        return if (binding.edtLogin.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(binding.edtLogin.text.toString())
                .matches()
        ) {
            alertMessage("The Email field must have an email format")
            false
        } else {
            binding.edtLogin.error = null
            true
        }
    }

    private fun alertMessage(s: String) {
        binding.txtProblem.text = s
        binding.btnSignIn.animate().translationY(300f).setDuration(200).withEndAction {
            binding.bgAlert.animate().alpha(1f).setDuration(200).withEndAction {
                binding.bgAlert.animate().alpha(1f).setDuration(800).withEndAction {
                    binding.bgAlert.animate().alpha(0f).setDuration(200)
                    binding.btnSignIn.animate().translationY(0f).setDuration(200)
                }
            }
        }
    }
}