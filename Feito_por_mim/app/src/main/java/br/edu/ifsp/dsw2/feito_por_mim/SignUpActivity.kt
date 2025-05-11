package br.edu.ifsp.dsw2.feito_por_mim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dsw2.feito_por_mim.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val senha = binding.senhaEditText.text.toString()
            val senhaAgain = binding.senhaAgainEditText.text.toString()


            if(senha != senhaAgain){
                Toast.makeText(this, "AS SENHAS INSERIDAS ESTÃO DIFERENTES!", Toast.LENGTH_LONG).show()
            }else{
                firebaseAuth
                    .createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}