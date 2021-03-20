package udec.sutatausa.directedbasket

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog

class MainActivity : AppCompatActivity() {

    // referenciamos los campos del Layaut
    lateinit var buttonSignOutElement: Button;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        // Inciiamos los campos
        buttonSignOutElement = findViewById(R.id.btnSignOut);

        // asignamos el evento click al boton de cerrar seción
        buttonSignOutElement.setOnClickListener(signOutEvent);

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_login));
    }

    /**
     * Permite Cerrar sesión
     **/
    val signOutEvent = View.OnClickListener { view ->

        // mostramos la ventana de confirmación
        GlobalMethods.showConfirm(getString(R.string.message_confirm_signout), DialogInterface.OnClickListener { dialogInterface, i ->

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
}