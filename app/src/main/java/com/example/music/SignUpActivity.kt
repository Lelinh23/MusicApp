package com.example.music

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupbtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()
            val cfpass = binding.confirmPass.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cfpass)) {
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(applicationContext,"Chưa nhập Email", Toast.LENGTH_SHORT).show()
                }
                if (TextUtils.isEmpty(pass)){
                    Toast.makeText(applicationContext,"Chưa nhập mật khẩu", Toast.LENGTH_SHORT).show()
                }
                if (TextUtils.isEmpty(cfpass)){
                    Toast.makeText(applicationContext,"Chưa nhập mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                if (pass == cfpass){
                    signup(email,pass)
                }else{
                    Toast.makeText(this,"Mật khẩu không khớp nhau !!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.login.setOnClickListener {
            val inte = Intent(this, SiginActivity::class.java)
            startActivity(inte)
        }
    }

    private fun signup(email: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // code chuyen sang đăng nhập
                val inten = Intent(this, SiginActivity::class.java)
                startActivity(inten)
            } else {
                Toast.makeText(this, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}