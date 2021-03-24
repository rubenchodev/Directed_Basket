package udec.sutatausa.directedbasket.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.util.Patterns
import android.widget.Button
import udec.sutatausa.directedbasket.R


/**
 * Se crea la clase que vamos a usar en todas las actividades existentes
 * */
class GlobalMethods( activity: Activity){

    // definimos la variable que referencia la acatividad donde se inicio la clase
    lateinit var myActivity: Activity;

    // initializer block
    init {
        println("Clase Iniciada")
        myActivity = activity;
    }

    /**
     * Permite mostrar mensaje en la interfaz
     */
    @SuppressLint("ResourceAsColor")
    fun showAlert(title: String?, message: String?, listener: DialogInterface.OnClickListener? ) {

        // Creamos una interfaz de dialogo para mostrar el mensa
        val builder = AlertDialog.Builder(myActivity, R.style.AlertDialogCustom);

        // definimos la variable para guardar el titulo
        var titleAlert = title;

        // valido si el titulo es null
        if(titleAlert == null) titleAlert = R.string.title_alert.toString();

        // agregamos las propiedades
        builder.setTitle(titleAlert)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_ok, listener);

        // Se crea el alerta y se visualiza en la interfaz
        builder.show();
    }

    /**
     * Permite mostrar mensaje de confirmación
     */
    @SuppressLint("ResourceAsColor")
    fun showConfirm(message: String?, listener: DialogInterface.OnClickListener? ) {

        // Creamos una interfaz de dialogo para mostrar el mensa
        val builder = AlertDialog.Builder(myActivity, R.style.AlertDialogCustom);

        // agregamos las propiedades
        builder.setTitle(R.string.title_alert)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_ok, listener)
            .setNegativeButton(R.string.alert_cancel, null);

        // Se crea el alerta y se visualiza en la interfaz
        builder.show();
    }

    /**
     * Se valida si es un correo
     */
    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}