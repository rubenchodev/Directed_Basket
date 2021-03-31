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
import udec.sutatausa.directedbasket.BasicClass.UserObject

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

        // validamos si el usuario ya se logueo
        if(currentUser != null){

            // obtenemos el id del usuario
            val userId: String = auth.currentUser.uid;

            // consultamos si existe información del usuario
            mDatabase.child("users").child(userId).get().addOnSuccessListener {
                try{

                    // Obtenemos los datos del usuario
                    val userDataSplash: UserObject? = it.getValue(UserObject::class.java);

                    // validamos si el usuario ya se logueo
                    if(userDataSplash != null && userDataSplash.policy!!){

                        // se valida si el usuario tiene el rol de "coach", es decir, si es el entrenador
                        if(userDataSplash.profile == "coach") {

                            // realizamos la apertura de la actividad principal
                            startActivitySplash(Intent(this, MainActivity::class.java));
                        } else {

                            // realizamos la apertura de la actividad principal para el estudiante
                            startActivitySplash(Intent(this, PlayerActivity::class.java));
                        }
                    } else {

                        // realizamos la apertura de la actividad de login
                        showMainActivity();
                    }
                } catch (ex: Exception){

                    // realizamos la apertura de la actividad de login
                    showMainActivity();
                }

            }.addOnFailureListener{

                // realizamos la apertura de la actividad de login
                showMainActivity();
            }
        } else {

            // realizamos la apertura de la actividad de login
            showMainActivity();
        }
    }

    /**
     * Permite mostrar la actividad por defecto
     */
    fun showMainActivity(){

        // cerramos sesión por si la inicio
        Firebase.auth.signOut();

        // realizamos la apertura de la actividad de login
        startActivitySplash(Intent(this, LoginActivity::class.java));
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