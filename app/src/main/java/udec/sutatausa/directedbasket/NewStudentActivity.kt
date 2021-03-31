package udec.sutatausa.directedbasket

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import udec.sutatausa.directedbasket.BasicClass.NewUserObject;
import udec.sutatausa.directedbasket.utils.GlobalMethods
import udec.sutatausa.directedbasket.utils.HttpTask
import udec.sutatausa.directedbasket.utils.LoadingDialog
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.util.*


class NewStudentActivity : AppCompatActivity() {

    // instancia para verificar si el usuario ya esta autenticado y subir archivos a storage
    private lateinit var auth: FirebaseAuth;
    private lateinit var mDatabase: DatabaseReference;
    private lateinit var mStorage : FirebaseStorage;

    // referenciamos los elementos
    var myHomeToolbar: Toolbar? = null;

    // referenciamos los campos del Layaut para la creación de un estudiante nuevo
    var nameStudentElement: EditText? = null;
    var emailStudentElement: EditText? = null;
    var passwordStudentElement: EditText? = null;
    var birthDayStudentElement: EditText? = null;
    var heightStudentElement: EditText? = null;
    var weightStudentElement: EditText? = null;
    lateinit var buttonStudentElement: Button;
    lateinit var buttonImageStudentElement: ImageButton;
    lateinit var imageViewStudentElement: ImageView;

    // instanciamos la clase de funciones generales
    lateinit var GlobalMethods: GlobalMethods;
    lateinit var loadingDialog: LoadingDialog;

    // Variable para controlar permisos
    private val MY_PERMISSIONS: Int = 100;
    private val PHOTO_CODE = 200;
    private val SELECT_PICTURE = 300;
    private val MEDIA_DIRECTORY = "/DirectedBaskedApp";
    private var mPath: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        // Incializamos la instancia con Firebase autentication y database realtime
        auth = Firebase.auth;
        mDatabase = Firebase.database.getReference();
        mStorage = Firebase.storage;

        // inicializaos el toolbar
        myHomeToolbar = findViewById(R.id.toolbar_student_bar);

        // se agrega el toolbar
        setSupportActionBar(findViewById(R.id.toolbar_student_bar));

        //Set Home screen icon
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        // Mostramos la opción de regresar
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        // establecemos el tiulo del app
        this.setTitle("");

        // iniciamos la clase
        GlobalMethods = GlobalMethods(this);

        // Se inicializa el dialog
        loadingDialog = LoadingDialog(
            this,
            getString(R.string.loading_text_new_user)
        );

        // iniciamos la referencia de los campos
        nameStudentElement = findViewById(R.id.txtStudentName);
        emailStudentElement = findViewById(R.id.txtStudentEmail);
        passwordStudentElement = findViewById(R.id.txtStudentPassword);
        birthDayStudentElement = findViewById(R.id.txtStudentBithDay);
        heightStudentElement = findViewById(R.id.txtStudentHeight);
        weightStudentElement = findViewById(R.id.txtStudentWeight);
        buttonImageStudentElement = findViewById(R.id.btnImageStudent);
        buttonStudentElement = findViewById(R.id.btnStudentSubmit);
        imageViewStudentElement = findViewById(R.id.imageViewStudent);
        imageViewStudentElement.setVisibility(View.GONE);

        // asignamos el evento click al boton de seleccionar archivo
        buttonImageStudentElement.setOnClickListener(showSelectorImage);

        // asignamos el evento click al boton de registar
        buttonStudentElement.setOnClickListener(registerNewStudent);

        // verificamos los permisos necesarioss
        if(verifyStoragePermission()) {
            buttonImageStudentElement.setEnabled(true);
            buttonStudentElement.setEnabled(true);
        } else {
            buttonImageStudentElement.setEnabled(false);
            buttonStudentElement.setEnabled(false);
        }

        // Establcemos la configuración de la fecha
        settingDate();
    }

    /**
     * Permite registrar un estudiante nuevo
     **/
    val registerNewStudent = View.OnClickListener { view ->

        // obtenemos el valor de cada campo
        val nameValue = nameStudentElement?.text.toString().trim();
        val emailValue = emailStudentElement?.text.toString().trim();
        val passValue = passwordStudentElement?.text.toString().trim();
        val dateValue = birthDayStudentElement?.text.toString().trim();
        val heightValue = heightStudentElement?.text.toString().trim();
        val weightValue = weightStudentElement?.text.toString().trim();

        // Se obtiene la información de validación de cada uno de los valores
        val validedName = GlobalMethods.verifyField(nameValue, "Nombre del estudiante", "text");
        val validedEmail = GlobalMethods.verifyField(emailValue, "Correo del estudiante", "email");
        val validedPass = GlobalMethods.verifyField(passValue, "Contraseña", "password");
        val validedDate = GlobalMethods.verifyField(dateValue, "Fecha de nacimiento", "date");
        val validedHeight = GlobalMethods.verifyField(heightValue, "Altura", "number");
        val validedWeight = GlobalMethods.verifyField(weightValue, "Peso", "number");

        // variable que referencia el valor del mensaje a mostrar
        var showMessage = "";

        // Se valida que todos los campos esten diligenciados
        if(validedName.isEmpty() == false){
            showMessage = validedName;
        } else if(validedEmail.isEmpty() == false){
            showMessage = validedEmail;
        } else if(validedPass.isEmpty() == false){
            showMessage = validedPass;
        } else if(validedDate.isEmpty() == false){
            showMessage = validedDate;
        } else if(validedHeight.isEmpty() == false){
            showMessage = validedHeight;
        } else if(validedWeight.isEmpty() == false){
            showMessage = validedWeight;
        }

        // Se valida que todos los campos esten diligenciados
        if(showMessage.isEmpty() == false){

            // mostramos mensaje
            GlobalMethods.showAlert("¡Aviso!", showMessage, null);

        } else if(null == imageViewStudentElement.getDrawable()) {

            // mostramos mensaje
            GlobalMethods.showAlert("¡Aviso!", "Por favor seleccione una imagen para continuar.", null);

        } else { // se procede a guardar la información del usuario

            // mostramos la ventana de confirmación
            GlobalMethods.showConfirm(getString(R.string.message_confirm_new_user), DialogInterface.OnClickListener { dialogInterface, i ->

                // mostramos el loading
                loadingDialog.startLoadingDialog();

                // referenciamos la instancia del usuario activo en el momento, ya que con el metodo de crear usuario se autentica automaticamente con ese usuario
                var currUserEmail = auth.currentUser.email;
                var currUserPassword = GlobalMethods.getPropertyValue(this, "password_curr");

                // se procede a crear un nuevo usuario
                auth.createUserWithEmailAndPassword(emailValue, passValue)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // obtenemos la información del nuevo usuario
                            val newUserID = auth.currentUser.uid;

                            // Cerramos el inicio de sesión automatico del nuevo usuario
                            Firebase.auth.signOut();

                            // volvemos a iniciar seción con el usuario actual
                            auth.signInWithEmailAndPassword(currUserEmail, currUserPassword)
                                .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    // Se procede a subir la imagen del usuario
                                    uploadImageInStorage(newUserID, passValue, nameValue, emailValue, dateValue, heightValue, weightValue);

                                } else {
                                    // Se muestra el mensaje indicando que el mensaje es incorrecto
                                    showError("Lo sentimos no es posible continuar con el proceso, por favor cierra el aplicativo y vuelve a iniciar sesión.");
                                }
                            };

                        } else {

                            // imprimimos el error
                            println(task.exception.toString());

                            // mostramos el eerror
                            showError("El usuario con el correo '$emailValue' ya se encuentra registrado.");
                        }
                    }

            });

        }
    }

    /**
     * Permite subir la imagen a firebase Storage
     */
    fun uploadImageInStorage(newUserId: String, password: String, name: String, email: String, date: String, height: String, weight: String){

        // obtenemos el dato de la imagen
        imageViewStudentElement.invalidate();
        val drawable = imageViewStudentElement.getDrawable() as BitmapDrawable;
        val bitmap = drawable.bitmap;
        val baos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        val data = baos.toByteArray();

        // creamos la referencia del buchet a donde deseamos guardar la imagen
        val storageRef = mStorage.reference;

        // Definimos el nombre
        val fileName = newUserId +"_profile.jpg"

        // refenciamos el hijo que sera donde queda guardado
        val child = storageRef.child(fileName);

        // creamos la tarea que permite proceder a subir la imagen
        child?.putBytes(data).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                // creamos la tarea que permite proceder a subir la imagen
                child?.downloadUrl.addOnCompleteListener(this) { taskUrl ->
                    if (taskUrl.isSuccessful) {

                        // se procede a registrar los datos en la base de datos detiempo real
                        registerDataInDatabase(newUserId, password, name, email, date, height, weight, taskUrl.result.toString());
                    } else {

                        // mostramos el error
                        showError("No es posible registar los datos en este momento.");
                    }
                }
            } else {
                // se procede a registrar los datos en la base de datos de tiempo real, pero sin imagen
                registerDataInDatabase(newUserId, password, name, email, date, height, weight, "");
            }
        }
    }

    /**
     * Agregamos al información en la base de datos de tiempo real
     */
    fun registerDataInDatabase(newUserId: String, password: String, name: String, email: String, date: String, height: String, weight: String, imageUrl: String){

        // obtenemos la información del nuevo usuario
        val masterUserID = auth.currentUser.uid;

        // Creamos el objecto a enviar a la base de datos
        val userObject = NewUserObject(false, "player", name, email, masterUserID, imageUrl, height, weight, date);

        // Insertamos el nuevo registro
        mDatabase.child("users").child(newUserId).setValue(userObject)
        .addOnSuccessListener {

            // se procede a enviar un correo al nuevo usuario
            sendMailNewUser(name, email, password);

            // Mostramos la ventana de error
            GlobalMethods.showAlert("Nuevo estudiante registrado", "El estudiante con el correo $email fue registrado correctamente.", DialogInterface.OnClickListener  { dialog, which ->

                // Mostramos la actividad
                startActivity(Intent(this, MainActivity::class.java));

                // Cerramos esta actividad
                finish();
            });

            // Ocultamos el loading
            loadingDialog.dissmissDialog();

        }.addOnFailureListener {
            // mostramos el error
            showError("No es posible registar los datos en este momento por el siguiente motivo: '$it'.");
        }
    }

    /**
     * Permiet enviar el correo al usuario
     */
    fun sendMailNewUser(userName: String, userEmail: String, password: String){

        // Definimos la URL a consumir
        val url = "https://script.google.com/macros/s/AKfycbwHrcjyp9WMfWWgzmvgB1I-YViCC2FU0J5cGjCpMN8B0ckRqwQF/exec";

        // definimos los datos a enviar
        val json = JSONObject();
        json.put("userName", userName);
        json.put("userEmail", userEmail);
        json.put("password", password);

        // definimos el objeto base
        val mainJson = JSONObject();
        mainJson.put("method", "sendMailNewUser");
        mainJson.put("params", json);

        // se realiza la petición
        HttpTask( {
            if (it == null) {
                println("Error de conexión");
                return@HttpTask;
            }
            println(it)
        } ).execute("POST", url, mainJson.toString());
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
     * Permite abrir el selector de imagenes
     **/
    val showSelectorImage = View.OnClickListener { view ->

        // Definimos las opciones
        val option = arrayOf<CharSequence>("Tomar foto", "Elegir de galeria", "Cancelar")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this);
        builder.setTitle("Eleige una opción")
        builder.setItems(option, DialogInterface.OnClickListener { dialog, which ->
            if (option[which] === "Tomar foto") {
                openCamera();
            } else if (option[which] === "Elegir de galeria") {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.type = "image/*";
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), SELECT_PICTURE);
            } else {
                dialog.dismiss();
                imageViewStudentElement.setVisibility(View.GONE);
                imageViewStudentElement.setImageDrawable(null);
            }
        });

        builder.show();
    }

    /**
     * Permite abrir la actividad de imagen
     */
    private fun openCamera() {

        // Se cera la vista para mostar la captura de una foto
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Se muestra la vista
        startActivityForResult(intent, PHOTO_CODE);
    }

    /**
     * Permite realizar una opción al seleccionar la imagen o al elegir una imagen
     **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            println(data)
            when (requestCode) {
                PHOTO_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap;
                    imageViewStudentElement.setImageBitmap(imageBitmap);
                    imageViewStudentElement.setVisibility(View.VISIBLE);
                }
                SELECT_PICTURE -> {
                    val path = data?.data;
                    imageViewStudentElement.setImageURI(path);
                    imageViewStudentElement.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Gestionar la respuesta post verificación de permisos
     **/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();

                // Habilita el boton
                buttonImageStudentElement.setEnabled(true);
                buttonStudentElement.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    /**
     * Permite mostrar una interfaz explicando el motivo de los permisos
     */
    private fun showExplanation() {
        val builder = AlertDialog.Builder(this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton(
            "Aceptar"
        ) { dialog, which ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri =
                Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton(
            "Cancelar"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        builder.show();
    }

    /**
     * Permite revisar si el usuario ya concedio permisos para seleccionar archivos o tomar una foto
     */
    private fun verifyStoragePermission(): Boolean {

        // Se valida si la versión de andriod es menor a la M,e s decir que no es necesario solicitar permisos, ya que al momento de instalar la aplicación concedio estos
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        // se chequea los permisos necesarios
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Se valida si un permiso esta pendiente por mostrar para que lo apruebe o lo deniege
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(CAMERA)) {

            // referenciamos el contexto actual
            val contextView = findViewById<View>(android.R.id.content);

            // Mostramos un mensaje indicando la lista de permisos necesarios
            Snackbar.make(contextView, "Los permisos son necesarios para poder elegir la foto del estudiante", Snackbar.LENGTH_INDEFINITE).setAction("AUTORIZAR") {
                requestPermissions(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA
                    ), MY_PERMISSIONS
                )
            }.show();
        } else {
            Toast.makeText(this, "sssss", Toast.LENGTH_SHORT).show();
            // se solicita los permisos necesarios
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), MY_PERMISSIONS);
        }

        // retornamos que aun no se ha concedido los permisos
        return false;
    }

    /**
     * Configuramos el campos de fecha
     */
    private fun settingDate() {

        // Obtenemos la fecha actual en formato claro para el usuario
        val currentDateStr: String? = GlobalMethods.longToDateFormatter( Calendar.getInstance().timeInMillis, false);

        // Agregamos la fecha
        birthDayStudentElement?.setText("$currentDateStr")
        //fieldDate.setText("29/12/2020 - 29/01/2021");

        // Se crea la instancia del selector de fecha
        val builder = MaterialDatePicker.Builder.datePicker();

        // Se establece el titulo y el tema
        builder.setTitleText(R.string.title_date_picker)
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)

        // Se compila el selector
        val materialDatePicker = builder.build();

        // Agregamos la acción para cuando el usuarios
        materialDatePicker.addOnPositiveButtonClickListener {
            // Convertimos la fecha en un string para mostrarlo en el campo
            val dateSelected = GlobalMethods.longToDateFormatter(it, true);

            // Agregamos la fecha
            birthDayStudentElement?.setText("$dateSelected");
        }

        // Agregamos el evento para cuando seleccione el valor
        birthDayStudentElement?.setOnClickListener { // Obtenemos el valor actual en el campo
            val currentDateText = birthDayStudentElement?.text.toString();

            // Establecemos como selección la fecha actual
            val currentTimeInMillis = Calendar.getInstance().timeInMillis;

            // se define que fecha se va agregar
            var setStartTime = currentTimeInMillis;

            // se valida si existe un valor en el campos
            if (!currentDateText.isEmpty()) {
                setStartTime = GlobalMethods.stringToDate(currentDateText, "dd/MM/yyyy")?.time!!;
            }

            // Agregamos la fecha actual o la que existe en el campo
            builder.setSelection(setStartTime);

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

            // mostramos la ventana de confirmación
            GlobalMethods.showConfirm(getString(R.string.message_confirm_new_student), DialogInterface.OnClickListener { dialogInterface, i ->

                // Mostramos la actividad
                startActivity(Intent(this, MainActivity::class.java));

                // Cerramos esta actividad
                finish();
            });

        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Permite ejecutar un evento al oprimir la fecha de atras
     */
    override fun onBackPressed() {

        // mostramos la ventana de confirmación
        GlobalMethods.showConfirm(getString(R.string.message_confirm_new_student), DialogInterface.OnClickListener { dialogInterface, i ->

            // Mostramos la actividad
            startActivity(Intent(this, MainActivity::class.java));

            // Cerramos esta actividad
            finish();
        });
    }
}