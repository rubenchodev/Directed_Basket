package udec.sutatausa.directedbasket

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import im.dacer.androidcharts.LineView
import udec.sutatausa.directedbasket.BasicClass.UserObject
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog
import java.util.*

class PlayerDetailActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;
    var G_USER_ID: String = "";

    // referenciamos los elementos
    var myPlayerDetailToolbar: Toolbar? = null;
    var playerImage: ImageView? = null;
    var playerName: TextView? = null;
    var playerAge: TextView? = null;
    var playerHeight: TextView? = null;
    var playerWeight: TextView? = null;
    var playerRangeDate: EditText? = null;
    var btnAddStatistic: Button? = null;
    var playerChart: LineView? = null; // https://androidexample365.com/an-easy-to-use-android-charts-library-with-animation/

    // Obtenemos los datos del usuario
    var userDataPlayer: UserObject? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        // Obtenemos la información del id del usuario
        val bundle = intent.extras;
        G_USER_ID = bundle?.getString("userId").toString();

        // Incializamos la instancia con Firebase autentication y database realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // inicializaos el toolbar
        myPlayerDetailToolbar = findViewById(R.id.toolbar_player_detail_bar);
        playerImage = findViewById(R.id.imgPlayerDetail);
        playerName = findViewById(R.id.lblPlayerDetailName);
        playerAge = findViewById(R.id.lblPlayerDetailAge);
        playerHeight = findViewById(R.id.lblPlayerDetailHeight);
        playerWeight = findViewById(R.id.lblPlayerDetailWeight);
        playerRangeDate = findViewById(R.id.txtPlayerDetailRange);
        btnAddStatistic = findViewById(R.id.btnAddStatistic);
        playerChart = findViewById(R.id.line_view);

        // se agrega el toolbar
        setSupportActionBar(findViewById(R.id.toolbar_player_detail_bar));

        //Set Home screen icon
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        // Mostramos la opción de regresar
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        // agregamos el ebvento al panel de crear usuario
        btnAddStatistic?.setOnClickListener { view ->

            // definimos cual es la actividad a mostrar
            var intentStatistic = Intent(this, StatisticsActivity::class.java);

            // agregamos el id del usuario que deseamo procesar
            intentStatistic.putExtra("userId", G_USER_ID);

            // Mostramos la actividad
            startActivity(intentStatistic);

            // Cerramos esta actividad
            finish();
        }

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(this, getString(R.string.loading_text_player_detail));

        // establecemos el tiulo del app
        this.setTitle("");

        // Agregamos los datos
        addGeneralData();

        // Configuramos el campo de fecha
        settingDate();

        // Inciamos la grafica
        initChart();
    }

    /**
     * Permite inicializar la grafica
     */
    fun initChart(){

        // establecemos las propiedads base
        playerChart?.setDrawDotLine(true);
        playerChart?.setShowPopup(LineView.SHOW_POPUPS_NONE);
    }

    /**
     * Permite agregar los datos generales del usuario
     */
    fun addGeneralData(){

        // consultamos si existe información del usuario
        mDatabase.child("users").child(G_USER_ID).get().addOnSuccessListener {

            // Obtenemos los datos del usuario
            userDataPlayer = it.getValue(UserObject::class.java);
            println(userDataPlayer?.image)
            userDataPlayer?.image?.let { it1 -> playerImage?.loadUrl(it1) };
            playerName?.setText(userDataPlayer?.name);
            playerAge?.setText(GlobalMethods.getAge(userDataPlayer?.birthDay).toString());
            playerHeight?.setText(userDataPlayer?.height);
            playerWeight?.setText(userDataPlayer?.weight);

        }.addOnFailureListener{

            // Se muestra el mensaje indicando que el mensaje es incorrecto
            showError("Lo sentimos no fue posible almacenar la aprobación de las políticas por el siguiente motivo: " + it);
        }
    }

    /**
     * Permite agregar la imagen
     */
    fun ImageView.loadUrl(url: String) {
        Picasso.get().load(url).into(this)
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

    /**
     * Configuramos el campos de fecha
     */
    private fun settingDate() {

        // Obtenemos la fecha actual en formato claro para el usuario
        val currentDateStr: String? = GlobalMethods.longToDateFormatter( Calendar.getInstance().timeInMillis, false);

        // Agregamos la fecha
        playerRangeDate?.setText("$currentDateStr - $currentDateStr")
        //fieldDate.setText("29/12/2020 - 29/01/2021");

        // Se crea la instancia del selector de fecha
        val builder = MaterialDatePicker.Builder.dateRangePicker();

        // Se establece el titulo y el tema
        builder.setTitleText(R.string.title_date_picker)
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)

        // Se compila el selector
        val materialDatePicker = builder.build();

        // Agregamos la acción para cuando el usuarios
        materialDatePicker.addOnPositiveButtonClickListener {
            // Convertimos la fecha en un string para mostrarlo en el campo
            val startDateSelected = GlobalMethods.longToDateFormatter(it.first, true);
            val endDateSelected = GlobalMethods.longToDateFormatter(it.second, true);

            // Agregamos la fecha
            playerRangeDate?.setText("$startDateSelected - $endDateSelected");
        }

        // Agregamos el evento para cuando seleccione el valor
        playerRangeDate?.setOnClickListener { // Obtenemos el valor actual en el campo
            val currentDateText = playerRangeDate?.text.toString().split(" - ");

            // Establecemos como selección la fecha actual
            val currentTimeInMillis = Calendar.getInstance().timeInMillis;

            // se define que fecha se va agregar
            var setStartTime = currentTimeInMillis;
            var setEndTime = currentTimeInMillis;

            // se valida si existe un valor en el campos
            if (!currentDateText.isEmpty()) {
                setStartTime = GlobalMethods.stringToDate(currentDateText[0], "dd/MM/yyyy")?.time!!;
                setEndTime = GlobalMethods.stringToDate(currentDateText[1], "dd/MM/yyyy")?.time!!;
            }
            // creamos un objeto de tipo Pair
            val newDate = Pair(setStartTime, setEndTime);

            // Agregamos la fecha actual o la que existe en el campo
            builder.setSelection(newDate);

            // Volvemos a compilar para que tome la fecha seleccionada
            builder.build();

            // Mostramso el selector
            materialDatePicker.show(supportFragmentManager, materialDatePicker.toString());
        }
    }

    /**
     * Permite gestionar los eventos del toolbar
     */
    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {

        // Se valida si se realizo clic sobre la ventana de la flecha de attras
        if (menuItem.getItemId() === android.R.id.home) {

            // Mostramos la actividad
            startActivity(Intent(this, MainActivity::class.java));

            // Cerramos esta actividad
            finish();

        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Permite ejecutar un evento al oprimir la fecha de atras
     */
    override fun onBackPressed() {

        // Mostramos la actividad
        startActivity(Intent(this, MainActivity::class.java));

        // Cerramos esta actividad
        finish();
    }
}