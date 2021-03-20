package udec.sutatausa.directedbasket.utils

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import udec.sutatausa.directedbasket.R

class LoadingDialog(  activity: Activity, myMessage: String) {

    // definimos la variable que referencia la acatividad donde se inicio la clase
    lateinit var myActivity: Activity;

    private val dialog: AlertDialog?;
    private var loadingMessage = "Loading...";
    var messageView: TextView? = null;

    /**
     * metodo que permite agregar el texto
     */
    fun setText(text: String?) {
        messageView!!.text = text;
    }

    /**
     * Creamos el metodo que muestra la actividad con el loading personalizado
     */
    fun startLoadingDialog() {

        // Se muestra el loading
        dialog!!.show();
    }

    /**
     * Creamos el metodo que permite ocultar el loading
     */
    fun dissmissDialog() {

        // Se valida si au existe  la actividad para ocultar la ventana
        if (myActivity != null && !myActivity.isFinishing && dialog != null && dialog.isShowing) {

            // Se oculta la ventana de dialog
            dialog.dismiss();
        }
    }

    /**
     * Iniciamos el constructor
     */
    init {

        // definimos cual es la actividad a trabajar
        myActivity = activity;

        // Se valida si existe un mensaje personalizado
        if (myMessage != null) {

            // Se establece el mensaje personalizado
            loadingMessage = myMessage
        }

        // se configura para que el dialog se muestre sobre la actividad donde se llama
        val builder = AlertDialog.Builder(activity);
        val inflater = activity!!.layoutInflater;
        val customView: View = inflater.inflate(R.layout.custom_dialog, null);
        messageView = customView.findViewById<View>(R.id.loadingText) as TextView;

        // Agregamos el texto
        messageView!!.text = loadingMessage;
        builder.setView(customView);
        builder.setCancelable(false);
        dialog = builder.create();
    }
}