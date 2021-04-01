package udec.sutatausa.directedbasket

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog

class StatisticsActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;
    var G_USER_ID: String = "";

    // referenciamos los elementos
    var myStatisticsToolbar: Toolbar? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Obtenemos la información del id del usuario
        val bundle = intent.extras;
        G_USER_ID = bundle?.getString("userId").toString();
        println(G_USER_ID)
        // Incializamos la instancia con Firebase autentication y database realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // inicializaos el toolbar
        myStatisticsToolbar = findViewById(R.id.toolbar_statistic_bar);

        // se agrega el toolbar
        setSupportActionBar(findViewById(R.id.toolbar_statistic_bar));

        //Set Home screen icon
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        // Mostramos la opción de regresar
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_player_detail));
    }

    /**
     * Permite gestionar los eventos del toolbar
     */
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {

        // Se valida si se realizo clic sobre la ventana de la flecha de attras
        if (menuItem.getItemId() === android.R.id.home) {

            // mostramos la ventana de confirmación
            GlobalMethods.showConfirm(getString(R.string.message_confirm_statistic), DialogInterface.OnClickListener { dialogInterface, i ->

                // Cerramos esta actividad
                finish();
            });

        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Permite ejecutar un evento al oprimir la fecha de atras
     */
    override fun onBackPressed() {

        // mostramos la ventana de confirmación
        GlobalMethods.showConfirm(getString(R.string.message_confirm_statistic), DialogInterface.OnClickListener { dialogInterface, i ->

            // Cerramos esta actividad
            finish();
        });
    }
}