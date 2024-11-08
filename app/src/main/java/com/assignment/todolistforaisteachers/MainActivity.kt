package com.assignment.todolistforaisteachers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.assignment.todolistforaisteachers.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.btnlogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()){
                try{
                    loginDataBase(username, password)
                }
                catch( e : Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show()
            }


        }

        //Redirects the user to the Sign Up page when sign up button is clicked
        binding.btnsignUp.setOnClickListener{

            try{
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            }

            catch( e : Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun loginDataBase(username: String, password: String){
        val validUsernamePassword = databaseHelper.checkUsernamePassword(username, password)

        try {
            if (validUsernamePassword) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
                finish()
            }

            else {
                Toast.makeText(this, "Login Failed. Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        catch(e : Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

}