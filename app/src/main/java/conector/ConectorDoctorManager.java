package conector;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import entities.Doctor;
import entities.Schedule;

/**
 * Created by maritza on 26/05/15.
 */
public class ConectorDoctorManager {

    private static final ConectorDoctorManager INSTANCE =
            new ConectorDoctorManager();

    /* Se declara aquí el URL_BASE para probar este conector, posteriormente
    hará uso del mismo atributo static de ConectorService */
    public static final String URL_BASE = ConectorServicio.URL_BASE;

    public static ConectorDoctorManager getInstance() {
        return INSTANCE;
    }

    public String saveNewDoctor(Doctor doctor) {
        Map<String, String> mapaDatos = new HashMap<String, String>();

        /* Se ha cambiado el nombre de los atributos desde la notación
        existente en la base de datos a la de las entidades actuales
        Ej: de first_name --> firstName */
        mapaDatos.put("username", doctor.getUsername());
        mapaDatos.put("firstName", doctor.getFirstName());
        mapaDatos.put("lastName", doctor.getLastName());
        mapaDatos.put("license", doctor.getLicense());
        mapaDatos.put("specialty", doctor.getSpecialty());
        mapaDatos.put("password", doctor.getPassword());

        JSONObject objectFromMap = new JSONObject(mapaDatos);
        String datos = objectFromMap.toString();

        String respuesta = null;

        /* Se elimino la cadena URLEncoder(datos) y reemplazo sólo por datos
        ya que regresaba una cadena irreconocible para los servicios */
        try {
            String cadena = URL_BASE + "doctormanager/savenewdoctor?doctor="
                    + datos;

            URL url = new URL(cadena);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "text/plain");

            int codigo = conexion.getResponseCode();
            System.err.println("DEBUG : "+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                respuesta = entrada.readLine();
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public String setSchedule(Schedule schedule) {
        Map<String, String> mapaDatos = new HashMap<String, String>();

        Doctor doctor = schedule.getDoctorUsername();

        /* Dado que originalmente la entidad Schedule de manuel consideraba
        al username del Doctor como un String, fue necesario cambiarlo
        para agregar la representación del doctor a la ruta del servicio
        por lo que a continuación se obtienen sus atributos con el proposito
        de formar su representación JSON*/
        Map<String, String> doctorMap = new HashMap<String, String>();
        doctorMap.put("username", doctor.getUsername());
        doctorMap.put("password", doctor.getPassword());
        doctorMap.put("firstName", doctor.getFirstName());
        doctorMap.put("lastName", doctor.getLastName());
        doctorMap.put("license", doctor.getLicense());
        doctorMap.put("specialty", doctor.getSpecialty());

        mapaDatos.put("idSchedule", Long.toString(schedule.getIdSchedule()));

        JSONObject doctorJson = new JSONObject(doctorMap);
        String doctorToSchedule = doctorJson.toString();

        mapaDatos.put("monday", schedule.getMonday());
        mapaDatos.put("tuesday", schedule.getTuesday());
        mapaDatos.put("wednesday", schedule.getWednesday());
        mapaDatos.put("thursday", schedule.getThursday());
        mapaDatos.put("friday", schedule.getFriday());

        JSONObject scheduleJson = new JSONObject(mapaDatos);
        String datosSchedule = scheduleJson.toString();

       /* Problema al tratar de añadir la representación JSON de un doctor
        si se añade directamente al mapaDatos el string doctorToSchedule
        obtenemos una cadena no válida para los servicios, por lo que
        a continuación se obtiene los datos del Schedule sin incluir la
        llave de cierre y se concatenan los datos del doctor añadiendo la
        llave de cierre para completar la cadena*/
        String datos = datosSchedule.substring(0,datosSchedule.length()-1)+",\"doctorUsername\":"+doctorToSchedule+"}";
        String respuesta = null;

        try {
            /* Se elimino la cadena URLEncoder(datos) y reemplazo sólo por datos
        ya que regresaba una cadena irreconocible para los servicios */
            String cadena = URL_BASE + "doctormanager/setschedule?schedule="
                    + datos;
            String strRegUrl = cadena.replaceAll(" ", "%20");
            URL url = new URL(strRegUrl);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "text/plain");

            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                respuesta = entrada.readLine();
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respuesta;
    }
}
