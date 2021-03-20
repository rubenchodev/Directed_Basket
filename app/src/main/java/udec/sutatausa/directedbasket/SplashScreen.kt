package udec.sutatausa.directedbasket;

import android.content.Intent
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

internal class SplashScreen : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        // Incializamos la instancia con Firebase
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();
    }

    /**
     * Metodo que se eejcuta al momento de cargar la intancia de firebase
     */
    public override fun onStart() {
        super.onStart();
        // Compruebe si el usuario se inicia sesión (no nulo) y actualiza UI en consecuencia.
        val currentUser = auth.currentUser;

        // realizamos la apertura de la actividad principal
        var intent: Intent? = null;

        // validamos si el usuario ya se logueo
        if(currentUser != null){

            // obtenemos el id del usuario
            val userId: String = auth.currentUser.uid;

            // consultamos si existe información del usuario
            mDatabase.child("users").child(userId).get().addOnSuccessListener {

                //  variable para obtener el objeto
                val response = JSONObject(it.value.toString());

                // validamos si el usuario ya se logueo
                if(response.getBoolean("policy")){

                    // realizamos la apertura de la actividad principal
                    startActivitySplash(Intent(this, MainActivity::class.java));
                } else {

                    // realizamos la apertura de la actividad de login
                    startActivitySplash(Intent(this, LoginActivity::class.java));
                }

            }.addOnFailureListener{

                // cerramos sesión por si la inicio
                Firebase.auth.signOut();

                // realizamos la apertura de la actividad de login
                startActivitySplash(Intent(this, LoginActivity::class.java));
            }
        } else {

            // realizamos la apertura de la actividad de login
            startActivitySplash(Intent(this, LoginActivity::class.java));
        }
    }

    /**
     * Permite iniciar una actividad
     */
    fun startActivitySplash(intent: Intent){

        // Mostramos la actividad
        startActivity(intent);

        // Cerramos esta actividad
        finish();

    }
}