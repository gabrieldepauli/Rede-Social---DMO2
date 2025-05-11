package br.edu.ifsp.dsw2.feito_por_mim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dsw2.feito_por_mim.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val senha = binding.senhaEditText.text.toString()

            firebaseAuth
                .signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

    }
}