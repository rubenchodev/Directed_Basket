package udec.sutatausa.directedbasket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog

class LoginActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // referenciamos los campos del Layaut para el login
    var emailLoginElement: EditText? = null;
    var passwordLoginElement: EditText? = null;
    lateinit var buttonLoginElement: Button;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login);

        // Incializamos la instancia con Firebase autentication y realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // iniciamos la referencia de los campos
        emailLoginElement = findViewById(R.id.txtLoginEmail);
        passwordLoginElement = findViewById(R.id.txtLoginPassword);
        buttonLoginElement = findViewById(R.id.btnLoginApp);

        // asignamos el evento click al boton de login
        buttonLoginElement.setOnClickListener(loginInApp);

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_login));

        // PARA PRUEBAS
        emailLoginElement?.setText("rubencho.dev@gmail.com");
        passwordLoginElement?.setText("123456");
    }

    /**
     * Permite iniciar sesión
     **/
    val loginInApp = View.OnClickListener { view ->

        // obtenemos los datos de login
        val email = emailLoginElement?.text.toString();
        val password = passwordLoginElement?.text.toString();
        println("yesss")
        // validamos si existe un correo y una contraseña
        if(!email.isEmpty() && !password.isEmpty()) {

            // validamos si el correo en realizad es un correo
            if(GlobalMethods.isEmailValid(email)) {

                // mostramos el loading
                loadingDialog.startLoadingDialog();

                // haciendo uso de Firebase Autentication se valida el ingreso del usuario
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        println(task)
                        // si es correcto el login se procede a ingrear al home del aplicativo
                        if (task.isSuccessful) {

                            // verificamos si el usuario esta registrado en la base de datos realtime y si ya autorizo politicas
                            verifyUserState();

                        } else {
                            // Se muestra el mensaje indicando que el mensaje es incorrecto
                            showError("Los datos ingresados no son validos.");
                        }
                    }
            } else {
                // Mostramos la ventana de error de correo
                showError("Por favor ingresa un correo valido");
            }
        } else {
            println("mal")
            // Mostramos la ventana de error
            showError("Por favor ingresa el correo y contraseña para continuar");
        }

    }

    /**
     * Permite verificar el tipo de perfil, y si ya autorizo politicas
     */
    private fun verifyUserState() {

        // obtenemos el id del usuario
        val userId: String = auth.currentUser.uid;

        // consultamos si existe información del usuario
        mDatabase.child("users").child(userId).get().addOnSuccessListener {

            //  variable para obtener el objeto
            val response = JSONObject(it.value.toString());

            // Ocultamos el loading
            loadingDialog.dissmissDialog();

            // realizamos la apertura de la actividad principal
            var intent: Intent? = null;

            // validamos si el usuario ya se logueo
            if(response.getBoolean("policy")){

                // realizamos la apertura de la actividad principal
                intent = Intent(this, MainActivity::class.java);
            } else {

                // realizamos la apertura de la actividad principal
                intent = Intent(this, PolicyActivity::class.java);
            }

            // Mostramos la actividad
            startActivity(intent);

            // Cerramos esta actividad
            finish();

        }.addOnFailureListener{

            // cerramos sesión por si la inicio
            Firebase.auth.signOut();

            // Se muestra el mensaje indicando que el mensaje es incorrecto
            showError("Los datos ingresados no son validos." + it);
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
            messageText = getString(R.string.default_message_error);
        }

        // Mostramos la ventana de error
        GlobalMethods.showAlert("Mensaje de error", message, null);
    }
}