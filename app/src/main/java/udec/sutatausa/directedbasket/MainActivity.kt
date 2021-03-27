package udec.sutatausa.directedbasket

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import udec.sutatausa.directedbasket.Adapters.UserListAdapter
import udec.sutatausa.directedbasket.BasicClass.UserDataHome
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog
import java.util.*


class MainActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    // referenciamos los elementos
    var myHomeToolbar: Toolbar? = null;
    var recyclerViewUserList: RecyclerView? = null;
    lateinit var swipeRefreshLayout: SwipeRefreshLayout;
    lateinit var btnShowPanelNewStudent: View;

    // variable que determina la lista que vamos a usar
    val mAdapter : UserListAdapter = UserListAdapter();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        // Incializamos la instancia con Firebase autentication y database realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // inicializaos el toolbar
        myHomeToolbar = findViewById(R.id.toolbar_home_bar);

        // se agrega el toolbar
        setSupportActionBar(findViewById(R.id.toolbar_home_bar));

        // establecemos el tiulo del app
        this.setTitle("");

        // inicializamos los elementos de la vista
        recyclerViewUserList = findViewById(R.id.recyclerViewUserList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutHome);
        btnShowPanelNewStudent = findViewById(R.id.btnShowPanelNewStudent);

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_login));

        // obtenemos la lista de usuarios registrados
        getUserList();

        // Se agrega el evento para que recargue el recycler
        swipeRefreshLayout.setOnRefreshListener { // Esto se ejecuta cada vez que se realiza el gesto
            getUserList();
        }

        // Mostramos por defecto el loading
        swipeRefreshLayout.isRefreshing = true;

        // Personalizamos el loading
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorWhite);

        // agregamos el ebvento al panel de crear usuario
        btnShowPanelNewStudent.setOnClickListener { view ->

            // Mostramos la actividad
            startActivity(Intent(this, NewStudentActivity::class.java));

            // Cerramos esta actividad
            finish();
        }

    }

    /**
     * Permite verificar el tipo de perfil, y si ya autorizo politicas
     */
    private fun getUserList() {

        // obtenemos el id del usuario
        val userId: String = auth.currentUser.uid;

        // consultamos si existe información del usuario
        val filterQuery: Query = mDatabase.child("users").orderByChild("coach").equalTo(userId);

        // creamos una lista muteable para permitir agregar items
        var userList: MutableList<UserDataHome>? = ArrayList();

        // Aplicamos el filtro
        filterQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    // retornamos cada unos de los resultados
                    for (issue in dataSnapshot.children) {

                        // creamos un objeto de la clase de userDataHome
                        val userDataCurr = UserDataHome(
                            issue.child("name").value as String,
                            issue.child("image").value as String,
                            issue.key as String
                        );

                        userList?.add(userDataCurr);
                    }

                    // inicamos la lista
                    if (userList != null) {
                        initRecyclerHome(userList);
                    };

                    // Ocultamos el loading
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

                // Ocultamos el loading
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Permite iniciar el recycler
     */
    fun initRecyclerHome(list: MutableList<UserDataHome>){

        // Definimos una configuración basica
        recyclerViewUserList?.setHasFixedSize(true)
        recyclerViewUserList?.layoutManager = LinearLayoutManager(this)
        mAdapter.UserListAdapter(list, this)
        recyclerViewUserList?.adapter = mAdapter
    }

    /**
     * Permite establecer las opciones personalizadas
     **/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater;
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Permite definir el evento a las opciones del menu superios
     **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Se define que hacer al hacer clic
        return when (item.itemId) {
            R.id.iconLogout -> {
                signOutEvent();
                true;
            }
            else -> super.onOptionsItemSelected(item);
        }
    }

    /**
     * Permite Cerrar sesión
     **/
    fun signOutEvent (){

        // mostramos la ventana de confirmación
        GlobalMethods.showConfirm(getString(R.string.message_confirm_signout), DialogInterface.OnClickListener { dialogInterface, i ->

            // ELiminamos la propiedad en la que guarda la contraseña
            GlobalMethods.removeProperty(this, "password_curr");

            // cerramos seción
            Firebase.auth.signOut();

            // realizamos la apertura de la actividad de login
            var intent: Intent? = Intent(this, LoginActivity::class.java);

            // Mostramos la actividad
            startActivity(intent);

            // Cerramos esta actividad
            finish();
        });

    }

    /**
     * Permite ejecutar un evento al oprimir la fecha de atras
     */
    override fun onBackPressed() {

        // Se llama el metodo de cerar sesión
        signOutEvent();
    }
}