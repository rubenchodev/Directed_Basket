package udec.sutatausa.directedbasket

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import udec.sutatausa.directedbasket.BasicClass.UserObject
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class PolicyActivity : AppCompatActivity() {

    // referenciamos los campos del Layaut
    var policyTextView: TextView? = null;
    lateinit var buttonOkElement: Button;
    lateinit var buttonCancelElement: Button;

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        // iniciamos la referencia de los campos
        policyTextView = findViewById(R.id.textViewPolicy);
        buttonCancelElement = findViewById(R.id.btnPolicyCancel);
        buttonOkElement = findViewById(R.id.btnPolicyOk);

        // establecemos el contenido
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            policyTextView?.setText(Html.fromHtml(getPolicyHtml(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            policyTextView?.setText(Html.fromHtml(getPolicyHtml()));
        }

        // asignamos el evento a los botones
        buttonCancelElement.setOnClickListener(closeApp);
        buttonOkElement.setOnClickListener(aprovePolicy);

        // Incializamos la instancia con Firebase autentication y realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this)

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_policy));
    }

    /**
     * Permite aprobar las politicas
     **/
    val aprovePolicy = View.OnClickListener { view ->

        // obtenemos el id del usuario
        val userId: String = auth.currentUser.uid;

        // mostramos el loading
        loadingDialog.startLoadingDialog();

        // consultamos si existe información del usuario
        mDatabase.child("users").child(userId).get().addOnSuccessListener {

            // Obtenemos los datos del usuario
            val userDataPolicy: UserObject? = it.getValue(UserObject::class.java);

            // actualizamos el estado del usuario
            mDatabase.child("users").child(userId).child("policy").setValue(true).addOnSuccessListener {

                // realizamos la apertura de la actividad principal
                var intent: Intent? = null;

                // se valida si el usuario tiene el rol de "coach"
                if(userDataPolicy?.profile == "coach") {
                    // realizamos la apertura de la actividad principal
                    intent = Intent(this, MainActivity::class.java);
                } else {
                    // realizamos la apertura de la actividad principal del jugador
                    intent = Intent(this, PlayerActivity::class.java);
                }

                // Ocultamos el loading
                loadingDialog.dissmissDialog();

                // Mostramos la actividad
                startActivity(intent);

                // Cerramos esta actividad
                finish();

            }.addOnFailureListener{

                // Se muestra el mensaje indicando que el mensaje es incorrecto
                showError("Lo sentimos no fue posible almacenar la aprobación de las políticas por el siguiente motivo: " + it);
            };

        }.addOnFailureListener{

            // Se muestra el mensaje indicando que el mensaje es incorrecto
            showError("Lo sentimos no fue posible almacenar la aprobación de las políticas por el siguiente motivo: " + it);
        }

    }

    /**
     * Metodo que permite mostrar un mensaje
     */
    private fun showError(message: String) {

        // definimos el mensaje a mostrar
        var messageText = message;

        // Ocultamos el loading
        loadingDialog.dissmissDialog();

        // validamos si el mensaje viene vacio
        if (messageText.isEmpty()) {

            // Tomamos un mensaje por defecto
            messageText = getString(R.string.default_message_error)
        }

        // Mostramos la ventana de error
        GlobalMethods.showAlert("Mensaje de error", message, null);
    }

    /**
     * Permite salir del aplicativo
     **/
    val closeApp = View.OnClickListener { view ->

        // cerramos sesión
        Firebase.auth.signOut();

        // ELiminamos la propiedad en la que guarda la contraseña
        GlobalMethods.removeProperty(this, "password_curr");

        // Validamos la versión con el objetivo de cerar por completo la aplicación
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
            finishAffinity();
        } else if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // Salimos del aplicativo
        System.exit(0);
    }

    /**
     * Permite obtener el Html del consentimiento
     */
    fun getPolicyHtml(): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = getAssets().open("policy.html")
            val r =
                BufferedReader(InputStreamReader(inputStream))
            val total = StringBuilder()
            var line: String?
            while (r.readLine().also { line = it } != null) {
                total.append(line).append("\n")
            }
            return total.toString()
        } catch (e: IOException) {}
        return "";
    }
}