package udec.sutatausa.directedbasket.utils

import android.os.AsyncTask;
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
/**
 * Clase para manejar las peticiones http
 */
class HttpTask(callback: (String?) -> Unit) : AsyncTask<String, Unit, String>()  {
    // Variable globales
    var callback = callback;
    val TIMEOUT = 10*1000;

    override fun doInBackground(vararg params: String): String? {
        val url = URL(params[1]);
        val httpClient = url.openConnection() as HttpURLConnection;
        httpClient.setReadTimeout(TIMEOUT);
        httpClient.setConnectTimeout(TIMEOUT);
        httpClient.requestMethod = params[0];
        httpClient.doOutput = true;
        httpClient.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        try {
            if (params[0] == "POST") {
                // Estalecemos la propiedades unicas de un medoto POST
                //httpClient.instanceFollowRedirects = false;
                httpClient.doInput = true;
                httpClient.useCaches = false;

                // Se realiza la conexión
                httpClient.connect();
                val os = httpClient.getOutputStream();
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"));
                writer.write(params[2]);
                writer.flush();
                writer.close();
                os.close();
            }
            if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                val stream = BufferedInputStream(httpClient.inputStream);
                val data: String = readStream(inputStream = stream);
                return data;
            } else {
                println("ERROR ${httpClient.responseCode}");
            }
        } catch (e: Exception) {
            println("ERROR ${e.printStackTrace()}");
        } finally {
            httpClient.disconnect();
        }

        return null;
    }

    /**
     * Permite obtener los datos que retorna el webservices
     */
    fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    /**
     * Al finalizar el consumo del webservice se ejecuta la función del callback
     */
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result);
        callback(result);
    }
}
