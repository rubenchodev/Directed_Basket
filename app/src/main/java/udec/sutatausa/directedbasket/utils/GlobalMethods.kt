package udec.sutatausa.directedbasket.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.text.format.DateFormat
import android.util.Patterns
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import udec.sutatausa.directedbasket.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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
        val builder = MaterialAlertDialogBuilder(myActivity, R.style.AlertDialogTheme);

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
        val builder = MaterialAlertDialogBuilder(myActivity, R.style.AlertDialogTheme);

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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Permite validar un tipo de dato
     */
    fun verifyField(stringValue: String, fieldName: String, type: String?): String {

        // definimos el mensaje a retornar
        var returnMessage = "";

        // removemos los espacios
        val newStringValue = stringValue.trim();

        // validamos si el campo esta vacio
        if(newStringValue.isEmpty()){
            // retornamos por defecto el mensaje que es obligatorio el campo
            return "Este campo '$fieldName' es obligatorio.";
        }

        // validamos los tipos de datos
        when (type){
            "email" ->
                if(!Patterns.EMAIL_ADDRESS.matcher(newStringValue).matches()) {
                    returnMessage = "Ingresar un correo valido.";
                }
            "number" ->
                if(!"\\d+".toRegex().matches(newStringValue)) {
                    returnMessage = "Ingresar un número valido.";
                }
            "password" ->
                if(newStringValue.length < 6) {
                    returnMessage = "La contraseña debe contener 6 dígitos como mínimo.";
                }
        }

        // Se retorna el mensaje
        return returnMessage;
    }

    /**
     * Permite obtener la información de la fecha de acuerdo a un valor tipo long
     */
    fun longToDateFormatter( dateTime: Long?, isCalculateZone: Boolean ): String? {

        // Obtenemos la fecha actual
        val currentDate = Date(dateTime!!)

        // Obtenemos el valor de la zona horaria
        val timeZone = currentDate.timezoneOffset

        // se valida si se debe de calcular la zona
        if (isCalculateZone && timeZone > 0) {

            // Agregamos el tiempo en milisegundos
            currentDate.time = currentDate.time + currentDate.timezoneOffset * 60 * 1000
        }
        if (isCalculateZone && timeZone < 0) {
            // Restamos el tiempo en milisegundos
            currentDate.time = currentDate.time - currentDate.timezoneOffset * 60 * 1000
        }

        // Se retorna la fecha en el formato establecido
        return DateFormat.format("dd/MM/yyyy", currentDate).toString()
    }

    /**
     * Metodo que permite convertir un string a un Objeto Date
     */
    fun stringToDate(stringDate: String, format: String?): Date? {

        // Retornamos la fecha
        return SimpleDateFormat(format).parse(stringDate);
    }

    /**
     * Metodo que permite agregar un valor que puede ser accedido en cualquier parte de la aplicación
     */
    fun addPropertyValue(activity: Activity, property: String, value: String?) {

        // Referenciamos las propiedades por defecto de la aplicación
        val myPreferences = activity.getSharedPreferences("udec.sutatausa.directedbasket_preferences", Context.MODE_PRIVATE)

        // Habilitamos las preferencias del editor para agregar datos
        val myEditor = myPreferences.edit();

        // agregamos los datos
        myEditor.putString(property, value);

        // Aplicamos los cambios
        myEditor.apply();
    }

    /**
     * Metodo que permite obtener el valor de una propiedad compartida
     */
    fun getPropertyValue(activity: Activity, property: String?): String? {

        // Referenciamos las propiedades por defecto de la aplicación
        val myPreferences = activity.getSharedPreferences("udec.sutatausa.directedbasket_preferences", Context.MODE_PRIVATE)

        // retornamo s el valor
        return myPreferences.getString(property, "");
    }

    /**
     * Metodo que permite eliminar una propiedad de las configuraciones del aplicativo
     */
    fun removeProperty(activity: Activity, property: String) {

        // Referenciamos las propiedades por defecto de la aplicación
        val myPreferences = activity.getSharedPreferences("udec.sutatausa.directedbasket_preferences", Context.MODE_PRIVATE)

        // Habilitamos las preferencias del editor para agregar datos
        val myEditor = myPreferences.edit();

        // removemos los datos
        myEditor.remove(property);

        // Aplicamos los cambios
        myEditor.apply();
    }

    /**
     * Permite obtener la edad
     */
    fun getAge(dobString: String?): Int {
        var date: Date? = null;
        val sdf = SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (e: ParseException) {}
        println(date)
        // validamos si no existe fecha
        if (date == null) return 0;

        // obtenemos 2 instanciaas de fechas actuales
        val dob: Calendar = Calendar.getInstance();
        val today: Calendar = Calendar.getInstance();
        println(dob)
        // agregamos al primer objeto la fecha de nacimiento
        dob.setTime(date);

        // Obtenemos los datos de la fecha
        val year: Int = dob.get(Calendar.YEAR);
        val month: Int = dob.get(Calendar.MONTH);
        val day: Int = dob.get(Calendar.DAY_OF_MONTH);

        // Agregamos el año, mes y día
        dob.set(year, month + 1, day);

        // Obtenemos la edad
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}