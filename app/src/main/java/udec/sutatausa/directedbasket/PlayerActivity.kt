package udec.sutatausa.directedbasket

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import im.dacer.androidcharts.LineView
import udec.sutatausa.directedbasket.BasicClass.UserDataHome
import udec.sutatausa.directedbasket.BasicClass.UserObject
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.LoadingDialog
import java.util.*


class PlayerActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    // referenciamos los elementos
    var myPlayerHomeToolbar: Toolbar? = null;
    var playerImage: ImageView? = null;
    var playerName: TextView? = null;
    var playerAge: TextView? = null;
    var playerHeight: TextView? = null;
    var playerWeight: TextView? = null;
    var playerRangeDate: EditText? = null;
    var playerChart: LineView? = null; // https://androidexample365.com/an-easy-to-use-android-charts-library-with-animation/

    // Obtenemos los datos del usuario
    var userDataPlayer: UserObject? = null;

    /**
     * Metodo principal de la actividad
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Incializamos la instancia con Firebase autentication y database realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();

        // inicializaos el toolbar y los elementos
        myPlayerHomeToolbar = findViewById(R.id.toolbar_player_home_bar);
        playerImage = findViewById(R.id.imgPlayerHome);
        playerName = findViewById(R.id.lblPlayerName);
        playerAge = findViewById(R.id.lblPlayerAge);
        playerHeight = findViewById(R.id.lblPlayerHeight);
        playerWeight = findViewById(R.id.lblPlayerWeight);
        playerRangeDate = findViewById(R.id.txtPlayerRange);
        playerChart = findViewById(R.id.line_view);

        // se agrega el toolbar
        setSupportActionBar(findViewById(R.id.toolbar_player_home_bar));

        // establecemos el tiulo del app
        this.setTitle("");

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Agregamos los datos
        addGeneralData();

        // Se configura el campo de fecha
        settingDate();

        val test = ArrayList<String>()
        for (i in 0 until 29) {
            test.add((i + 1).toString())
        }
        playerChart?.setBottomTextList(test)
        playerChart?.setColorArray(
            intArrayOf(
                Color.parseColor("#F44336"), Color.parseColor("#9C27B0"),
                Color.parseColor("#2196F3"), Color.parseColor("#009688")
            )
        )
        playerChart?.setDrawDotLine(true)
        playerChart?.setShowPopup(LineView.SHOW_POPUPS_NONE)

        val dataList: ArrayList<Int> = ArrayList()
        for (i in 0 until 29) {
            dataList.add((i*Math.random()).toInt())
        }

        val dataList2: ArrayList<Int> = ArrayList()
        for (i in 0 until 9) {
            dataList2.add((i*Math.random()).toInt())
        }

        val dataList3: ArrayList<Int> = ArrayList()
        for (i in 0 until 9) {
            dataList3.add((i*Math.random()).toInt())
        }

        val dataLists: ArrayList<ArrayList<Int>> = ArrayList()
        dataLists.add(dataList)
        //dataLists.add(dataList2)
        //dataLists.add(dataList3)

        playerChart?.setDataList(dataLists)

    }

    /**
     * Permite consultar los registros del usuario actual por cada estadistica
     */
    private fun consultStatistics(stringDate: String) {

        // obtenemos el id del usuario
        val userId: String = auth.currentUser.uid;

        // consultamos si existe información del usuario
        /*val filterQuery: Query = mDatabase.child("users").orderByChild("coach").equalTo(userId);

        // creamos una lista muteable para permitir agregar items
        var userList: MutableList<UserDataHome>? = ArrayList();

        // Aplicamos el filtro
        filterQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    // retornamos cada unos de los resultados
                    for (issue in dataSnapshot.children) {

                        // creamos un objeto de la clase de userDataHome
                        val userDataCurr = UserDataHome(
                            issue.child("name").value as String,
                            issue.child("image").value as String,
                            issue.key as String
                        );

                        userList?.add(userDataCurr);
                    }

                    // inicamos la lista
                    if (userList != null) {
                        initRecyclerHome(userList);
                    };

                    // Ocultamos el loading
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

                // Ocultamos el loading
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
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
     * Permite agregar los datos generales del usuario
     */
    fun addGeneralData(){

        // obtenemos el id del usuario
        val userId: String = auth.currentUser.uid;

        // consultamos si existe información del usuario
        mDatabase.child("users").child(userId).get().addOnSuccessListener {

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
            messageText = getString(R.string.default_message_error)
        }

        // Mostramos la ventana de error
        GlobalMethods.showAlert("Mensaje de error", message, null);
    }

    /**
     * Permite establecer las opciones personalizadas
     **/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater;
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Permite definir el evento a las opciones del menu superios
     **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Se define que hacer al hacer clic
        return when (item.itemId) {
            R.id.iconLogout -> {
                signOutEvent();
                true;
            }
            else -> super.onOptionsItemSelected(item);
        }
    }

    /**
     * Permite Cerrar sesión
     **/
    fun signOutEvent (){

        // mostramos la ventana de confirmación
        GlobalMethods.showConfirm(getString(R.string.message_confirm_signout), DialogInterface.OnClickListener { dialogInterface, i ->

            // ELiminamos la propiedad en la que guarda la contraseña
            GlobalMethods.removeProperty(this, "password_curr");

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

    /**
     * Permite ejecutar un evento al oprimir la fecha de atras
     */
    override fun onBackPressed() {

        // Se llama el metodo de cerar sesión
        signOutEvent();
    }
}