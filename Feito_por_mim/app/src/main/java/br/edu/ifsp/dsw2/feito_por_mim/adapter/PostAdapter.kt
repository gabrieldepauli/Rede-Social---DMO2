package br.edu.ifsp.dsw2.feito_por_mim.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.dsw2.feito_por_mim.R
import br.edu.ifsp.dsw2.feito_por_mim.model.Post

class PostAdapter(private val posts: Array<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val usernameCard: TextView = view.findViewById((R.id.usernameCard))
        val imagePost: ImageView = view.findViewById(R.id.imagePost)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val location: TextView = view.findViewById(R.id.location)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imagePost.setImageBitmap(posts[position].getPhoto())
        holder.txtDescription.text = posts[position].getDescription()
        holder.location.text = posts[position].getLocation()
        holder.usernameCard.text = posts[position].getUsername()
    }

}