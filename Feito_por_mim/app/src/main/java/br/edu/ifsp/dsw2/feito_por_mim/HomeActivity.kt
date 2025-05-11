package br.edu.ifsp.dsw2.feito_por_mim

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import br.edu.ifsp.dsw2.feito_por_mim.adapter.PostAdapter
import br.edu.ifsp.dsw2.feito_por_mim.databinding.ActivityHomeBinding
import br.edu.ifsp.dsw2.feito_por_mim.model.Post
import br.edu.ifsp.dsw2.feito_por_mim.util.Base64Converter

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val firebaseAuth = FirebaseAuth.getInstance()
    private var ultimoDocumento: DocumentSnapshot? = null
    private val posts = ArrayList<Post>()
    private var paginaAtual = 0
    private val tamanhoPagina = 5
    private val documentosPaginados = mutableListOf<List<DocumentSnapshot>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPosts(0)

        loadData()

        binding.buttonLogoff.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        binding.buttonPost.setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
            finish()
        }

        binding.buttonFilter.setOnClickListener {
            val localizacao = binding.cityFilter.text.toString()

            if (localizacao.isNotEmpty()) {
                loadPostsByCity(localizacao)
            } else {
                Toast.makeText(this, "Por favor, insira uma cidade para realizar a pesquisa", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonNext.setOnClickListener {
            paginaAtual++
            loadPosts(paginaAtual)
        }

        binding.buttonPrevious.setOnClickListener {
            if (paginaAtual > 0) {
                paginaAtual--
                loadPosts(paginaAtual)
            }
        }

        binding.buttonClearFilter.setOnClickListener {
            binding.cityFilter.text.clear()

            loadPosts(paginaAtual)
        }

    }

    private fun loadData(){
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageString = document.getString("fotoPerfil") ?: ""
                    val bitmap = Base64Converter.stringToBitmap(imageString)
                    binding.imageUser.setImageBitmap(bitmap)
                    binding.username.text = document.getString("username")
                    binding.nameComplete.text = document.getString("nomeCompleto")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPosts(pagina: Int) {
        val db = Firebase.firestore
        val query = db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .limit(tamanhoPagina.toLong())

        val baseQuery = if (pagina > 0 && documentosPaginados.size >= pagina) {
            query.startAfter(documentosPaginados[pagina - 1].last())
        } else {
            query
        }

        baseQuery.get().addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val documentos = result.documents
                if (documentosPaginados.size > pagina) {
                    documentosPaginados[pagina] = documentos
                } else {
                    documentosPaginados.add(documentos)
                }

                posts.clear()
                for (document in documentos) {
                    val imageString = document.getString("imageString") ?: ""
                    val descricao = document.getString("descricao") ?: ""
                    val cidade = document.getString("cidade") ?: ""
                    val username = document.getString("username") ?: ""
                    val bitmap = Base64Converter.stringToBitmap(imageString)

                    posts.add(Post(descricao, bitmap, cidade, username))
                }

                binding.recycleView.layoutManager = LinearLayoutManager(this)
                binding.recycleView.adapter = PostAdapter(posts.toTypedArray())
            } else {
                Toast.makeText(this, "Sem mais posts", Toast.LENGTH_SHORT).show()
                if (pagina > 0) paginaAtual--
            }
        }
    }

    private fun loadPostsByCity(localizacao: String) {
        val db = Firebase.firestore

        db.collection("posts")
            .whereEqualTo("cidade", localizacao)
            .get()
            .addOnSuccessListener { result ->
                posts.clear()

                if (!result.isEmpty) {
                    for (document in result.documents) {
                        val imageString = document.getString("imageString") ?: continue
                        val descricao = document.getString("descricao") ?: ""
                        val cidade = document.getString("cidade") ?: ""
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        val username = document.getString("username") ?: ""

                        posts.add(Post(descricao, bitmap, cidade, username))
                    }

                    val adapter = PostAdapter(posts.toTypedArray())
                    binding.recycleView.layoutManager = LinearLayoutManager(this)
                    binding.recycleView.adapter = adapter
                } else {
                    Toast.makeText(this, "Nenhum post encontrado para essa localização", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao realizar a pesquisa", Toast.LENGTH_SHORT).show()
            }
    }

}