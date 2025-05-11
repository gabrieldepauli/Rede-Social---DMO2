package br.edu.ifsp.dsw2.feito_por_mim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dsw2.feito_por_mim.databinding.ActivityProfileBinding
import br.edu.ifsp.dsw2.feito_por_mim.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()

        val email = firebaseAuth.currentUser?.email

        if (email != null) {
            val db = Firebase.firestore
            db.collection("usuarios").document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeCompleto = document.getString("nomeCompleto") ?: ""
                        val username = document.getString("username") ?: ""
                        val fotoPerfil = document.getString("fotoPerfil")

                        binding.nameEditText.setText(nomeCompleto)
                        binding.usernameEditText.setText(username)

                        if (!fotoPerfil.isNullOrEmpty()) {
                            val drawable = Base64Converter.stringToDrawable(fotoPerfil)
                            binding.userimage.setImageDrawable(drawable)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar dados do perfil.", Toast.LENGTH_LONG).show()
                }
        }

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri ->
            if (uri != null) {
                binding.userimage.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }
        binding.alterarFotoButton.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.salvarButton.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                val email = firebaseAuth.currentUser!!.email.toString()
                val username = binding.usernameEditText.text.toString()
                val nomeCompleto = binding.nameEditText.text.toString()
                val fotoPerfilString = Base64Converter.drawableToString(binding.userimage.drawable)

                val db = Firebase.firestore

                val dados = hashMapOf(
                    "nomeCompleto" to nomeCompleto,
                    "username" to username,
                    "fotoPerfil" to fotoPerfilString
                )

                db.collection("usuarios").document(email)
                    .set(dados)
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }

            }
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}