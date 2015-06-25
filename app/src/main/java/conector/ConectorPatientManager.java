package conector;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import entities.Patient;

/**
 * Created by maritza on 26/05/15.
 */
public class ConectorPatientManager {

    private static final ConectorPatientManager INSTANCE =
            new ConectorPatientManager();

    private URL url;
    private HttpURLConnection conexion;

    /* Se declara aquí el URL_BASE para probar este conector, posteriormente
    hará uso del mismo atributo static de ConectorService */
    public static final String URL_BASE = ConectorServicio.URL_BASE;

    public static ConectorPatientManager getInstance() {
        return INSTANCE;
    }

    public String saveNewPatient(Patient patient){

        Map<String, String> mapaDatos = new HashMap<String, String>();
        mapaDatos.put("nss", patient.getNss());
        mapaDatos.put("firstName", patient.getFirstName());
        mapaDatos.put("lastName", patient.getLastName());
        mapaDatos.put("address", patient.getAddress());
        mapaDatos.put("password", patient.getPassword());
        mapaDatos.put("isActive", Boolean.toString(patient.getIsActive()));

        /*Doctor doctor = patient.getDoctorUsername();
        Map<String, String> doctorMap = new HashMap<String, String>();
        doctorMap.put("username", doctor.getUsername());
        doctorMap.put("password", doctor.getPassword());
        doctorMap.put("firstName", doctor.getFirstName());
        doctorMap.put("lastName", doctor.getLastName());
        doctorMap.put("license", doctor.getLicense());
        doctorMap.put("specialty", doctor.getSpecialty());
        JSONObject doctorJson = new JSONObject(doctorMap);
        String doctorToSchedule = doctorJson.toJSONString();
                */


        JSONObject patientJson = new JSONObject(mapaDatos);
        String datosPatient = patientJson.toString();

        System.err.println("DEBUG DATOS PATIENT : "+datosPatient);
        //String datos = datosPatient.substring(0,datosPatient.length()-1)+",\"doctorUsername\":"+doctorToSchedule+"}";
        String datos = datosPatient;
        System.err.println("DEBUG DATOS : "+datos);
        String respuesta = null;

        try {
            String cadena = URL_BASE + "patientmanager/savepatient?patient="
                    + datos;
            String strRegUrl = cadena.replaceAll(" ", "%20");
            URLEncoder.encode(strRegUrl, "UTF-8");

            URL url = new URL(strRegUrl);

            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            System.err.println("DEBUG CONEXION: "+conexion.getURL());
            conexion.getURL();
            conexion.setRequestProperty("Accept", "text/plain; charset=utf-8");
            conexion.setRequestProperty("Accept-Charset", "utf-8");
            Log.e("URL ",conexion.getURL().toString());

            int codigo = conexion.getResponseCode();
            System.err.println("DEBUG : "+codigo+ " "+conexion.getResponseMessage());
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();

                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                System.err.println("encodig de input : "+conexion.getContentEncoding());



                System.err.println("encoding "+isr.getEncoding());


                BufferedReader entrada = new BufferedReader(isr, 8192);
                respuesta = entrada.readLine();
                System.err.println("respuesta : "+respuesta);
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }
}
