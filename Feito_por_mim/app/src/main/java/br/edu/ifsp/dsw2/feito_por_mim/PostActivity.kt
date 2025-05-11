package br.edu.ifsp.dsw2.feito_por_mim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dsw2.feito_por_mim.databinding.ActivityPostBinding
import br.edu.ifsp.dsw2.feito_por_mim.util.Base64Converter
import br.edu.ifsp.dsw2.feito_por_mim.util.LocationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class PostActivity : AppCompatActivity(), LocationHelper.Callback {

    private lateinit var binding: ActivityPostBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var imagePost: String = ""
    private var descricaoPost: String = ""
    private var username: String = ""
    private var email: String = ""

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imagePost.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonChangeImagePost.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSavePost.setOnClickListener {
            prepararDados()
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }

    private fun prepararDados() {
        if (firebaseAuth.currentUser != null) {
            email = firebaseAuth.currentUser!!.email.toString()
            imagePost = Base64Converter.drawableToString(binding.imagePost.drawable)
            descricaoPost = binding.editTextPost.text.toString()

            if (descricaoPost.isNotEmpty() && imagePost.isNotEmpty()) {
                buscarUsernameDoUsuario()
            } else {
                Toast.makeText(this, "Por favor, insira uma descrição e uma imagem.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarUsernameDoUsuario() {
        firestore.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username").toString()
                    obterLocalizacaoDoUsuario()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar usuário.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obterLocalizacaoDoUsuario() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val localizacaoHelper = LocationHelper(applicationContext)
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
        salvarPost(endereco, latitude, longitude)
    }

    override fun onErro(mensagem: String) {
        Toast.makeText(this, "Erro ao obter localização: $mensagem", Toast.LENGTH_SHORT).show()
    }

    private fun salvarPost(endereco: Address, latitude: Double, longitude: Double) {
        val dados = hashMapOf(
            "username" to username,
            "email" to email,
            "descricao" to descricaoPost,
            "imageString" to imagePost,
            "data" to Timestamp.now(),
            "cidade" to endereco.subAdminArea,
            "latitude" to latitude,
            "longitude" to longitude
        )

        firestore.collection("posts")
            .add(dados)
            .addOnCompleteListener {
                Toast.makeText(this, "Post salvo com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar o post.", Toast.LENGTH_SHORT).show()
            }

    }
}
