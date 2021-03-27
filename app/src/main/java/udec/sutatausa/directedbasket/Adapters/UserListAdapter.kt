package udec.sutatausa.directedbasket.Adapters;

import android.content.Context
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_user_home.view.*
import udec.sutatausa.directedbasket.BasicClass.UserDataHome
import udec.sutatausa.directedbasket.R;

class UserListAdapter: RecyclerView.Adapter<UserListAdapter.UserListHolder>() {

    var userList: MutableList<UserDataHome> = ArrayList();
    lateinit var context: Context;

    fun UserListAdapter(userList : MutableList<UserDataHome>, context: Context){
        this.userList = userList;
        this.context = context;
    }

    /**
     * Permite crear una vista por cada item de la lista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {

        // Definimos la vista que se desea tomar como referencia
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_user_home, parent, false)

        // retornamos
        return UserListHolder(view);

    }

    /**
     * Define cuantos elementos debe manejar
     */
    override fun getItemCount(): Int {
        return userList.size;
    }

    /**
     * Permite qeu hacer con cada item recorrido, es decir, permite agregar los valores de cada lista
     */
    override fun onBindViewHolder(holder: UserListHolder, position: Int) {

        val item = userList.get(position)
        holder.bind(item, context)


        // renderizamos cada uno de los elementos
        holder.bind(userList[position], context);
    }

    /**
     * Clase para dar forma al recycler
     */
    class UserListHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // inicamos la referencia de los elementos
        val recyclerUserName = view.findViewById(R.id.recyclerUserName) as TextView;
        val recyclerUserImage = view.findViewById(R.id.recyclerUserImage) as ImageView;
        val recyclerCard = view.findViewById(R.id.recyclerCard) as LinearLayout;

        /**
         * Creamos el metodo para renderizar nustro objeto y datos
          */
        fun bind(userData: UserDataHome, context: Context){

            // cargamos cada uno de los valores
            recyclerUserName.text = userData.userName;
            recyclerUserImage.loadUrl(userData.image);

            // agregamos el evento para que al dar clic muestr el panel de detalles
            recyclerCard.setOnClickListener(){
                Toast.makeText(view.context, "Testing: ${userData.userId}", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Permite agregar la imagen
         */
        fun ImageView.loadUrl(url: String) {
            Picasso.get().load(url).into(this)
        }
    }
}