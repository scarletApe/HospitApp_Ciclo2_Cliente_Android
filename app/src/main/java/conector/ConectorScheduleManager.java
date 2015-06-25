package conector;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Appointment;
import entities.Doctor;
import entities.Patient;
import entities.Schedule;
import utilities.Date;

/**
 * Created by maritza on 26/05/15.
 */
public class ConectorScheduleManager {

    /* Se declara aquí el URL_BASE para probar este conector, posteriormente
   hará uso del mismo atributo static de ConectorService */
    public static final String URL_BASE = ConectorServicio.URL_BASE;

    private static final ConectorScheduleManager INSTANCE = new ConectorScheduleManager();

    private URL url;
    private HttpURLConnection conexion;

    private OutputStream os;

    public static ConectorScheduleManager getInstance() {
        return INSTANCE;
    }

    public long scheduleAppointment(Appointment appointment) {
        boolean result = false;


        Map<String, String> appointmentMap = new HashMap<String, String>();
        appointmentMap.put("idAppointment", appointment.getIdAppointment().toString());
        appointmentMap.put("time", appointment.getTime());
        Date date = appointment.getDate();
        int day = date.getDia();
        String dia=day+"";
        if(day<10){
            dia="0"+day;
        }
        int mes = date.getMes();
        String mesS=mes+"";
        if(mes<10){
            mesS="0"+mes;
        }

        int year = date.getYear();

        appointmentMap.put("date", year+"-"+mesS+"-"+dia+"T00:00:00-05:00");

        if(appointment.getIsFinished() != null){
            appointmentMap.put("isFinished", appointment.getIsFinished().toString());
        }
        if(appointment.getIscanceled() != null){
            appointmentMap.put("iscanceled", appointment.getIscanceled().toString());
        }

        Doctor doctorAppointment = appointment.getDoctorUsername();
        Map<String, String> doctorMap = new HashMap<String, String>();
        doctorMap.put("username", doctorAppointment.getUsername());
        doctorMap.put("password", doctorAppointment.getPassword());
        doctorMap.put("firstName", doctorAppointment.getFirstName());
        doctorMap.put("lastName", doctorAppointment.getLastName());
        doctorMap.put("license", doctorAppointment.getLicense());


        if(doctorAppointment.getSpecialty() != null){
            doctorMap.put("specialty", doctorAppointment.getSpecialty());
        }

        String docJSON = new JSONObject(doctorMap).toJSONString();
        System.err.println("------Representación de doctor de Appointment: "+docJSON);
        System.err.println();
        System.err.println();
        appointmentMap.put("doctorUsername",docJSON);

        System.err.print("Appointment until now!! --  ");
        JSONObject appointmentJsonFirst = new JSONObject(appointmentMap);

        String appointmentToScheduleFirst = appointmentJsonFirst.toJSONString();
        appointmentToScheduleFirst = appointmentToScheduleFirst.replace("\\", "");
        appointmentToScheduleFirst= appointmentToScheduleFirst.replace("\"{", "{");
        appointmentToScheduleFirst = appointmentToScheduleFirst.replace("}\"", "}");
        System.err.println(appointmentToScheduleFirst);



        Patient patient = appointment.getPatientNss();
        Map<String, String> patientMap = new HashMap<String, String>();
        patientMap.put("nss", patient.getNss());
        patientMap.put("password", patient.getPassword());
        patientMap.put("firstName", patient.getFirstName());
        patientMap.put("lastName", patient.getLastName());
        patientMap.put("address", patient.getAddress());
        patientMap.put("isActive", patient.getIsActive()+"");


        if(patient.getDoctorUsername() != null){
            Doctor patientDoctor = patient.getDoctorUsername();
            Map<String, String> patientDoctorMap = new HashMap<String, String>();
            patientDoctorMap.put("username", patientDoctor.getUsername());
            patientDoctorMap.put("password", patientDoctor.getPassword());
            patientDoctorMap.put("firstName", patientDoctor.getFirstName());
            patientDoctorMap.put("lastName", patientDoctor.getLastName());
            patientDoctorMap.put("license", patientDoctor.getLicense());
            if(doctorAppointment.getSpecialty() != null){
                patientDoctorMap.put("specialty", patientDoctor.getSpecialty());
            }


            String docPatientJSON = new JSONObject(patientDoctorMap).toJSONString();
            patientMap.put("doctorUsername", docPatientJSON);
            System.err.println("------Representación de Doctor de Patient: "+docPatientJSON);
            System.err.println();
            System.err.println();
        }

        JSONObject patientJson = new JSONObject(patientMap);
        String patientToSchedule = patientJson.toJSONString();
        patientToSchedule = patientToSchedule.replace("\\", "");
        patientToSchedule= patientToSchedule.replace("\"{", "{");
        patientToSchedule = patientToSchedule.replace("}\"", "}");

        appointmentMap.put("patientNss", patientToSchedule);
        System.err.println("------Representación de Patient de Appointment: "+patientToSchedule);
        System.err.println();
        System.err.println();



        JSONObject appointmentJson = new JSONObject(appointmentMap);

        String appointmentToSchedule = appointmentJson.toJSONString();


        appointmentToSchedule = patientToSchedule.replace("\\", "");
        appointmentToSchedule = patientToSchedule.replace("\"{", "{");
        appointmentToSchedule = patientToSchedule.replace("}\"", "}");


        String finalCadena = appointmentToScheduleFirst.substring(0,appointmentToScheduleFirst.length()-1)+","+ "\"patientNss\":{"+appointmentToSchedule.substring(1, appointmentToSchedule.length())+"}";
        System.err.println("-----------Esta si sería la final --------------------------");
        System.err.println(finalCadena);
        byte[] porEnviar = finalCadena.getBytes();

        System.out.println("Appointment Final: "+ "\"patientNss\":{"+appointmentToSchedule.substring(1, appointmentToSchedule.length()));


        String cadena = URL_BASE + "appointment/"+appointment.getIdAppointment();

        try {
            url = new URL(cadena);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setDoOutput(true);

            conexion.setRequestMethod("PUT");

            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setRequestProperty("Content-Length", Integer.toString(porEnviar.length));
            os = conexion.getOutputStream();
            os.write(porEnviar);

            int codigo = conexion.getResponseCode();

            System.out.println("Codigo recibido " + codigo);

            if (codigo / 100 != 2) {

                System.out.println("Error en Codigo recibido " + codigo);
                return -1L;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointment.getIdAppointment();


    }

    public boolean cancelAppointment(Appointment appointment) {
        boolean result = false;
        try {
            String urlBusqueda = URL_BASE + "schedulemanager/cancelappointment?"
                    + "idAppointment=" + appointment.getIdAppointment().toString();
            url = new URL(urlBusqueda);
            System.err.println(url);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "text/plain");

            int codigo = conexion.getResponseCode();

            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();
                result = Boolean.parseBoolean(respuesta);

                entrada.close();
            }

        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }

        return result;
    }



    public Schedule getAvailableSchedule(Doctor doctor, boolean original) {
        Schedule horario = null;
        try {
            String urlBusqueda = URL_BASE + "schedulemanager/availableschedule?"
                    + "username=" + URLEncoder.encode(doctor.getUsername() + "", "UTF-8")
                    + "&original=" + original;
            url = new URL(urlBusqueda);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");

            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();

                JSONObject scheduleJson = (JSONObject) JSONValue.parse(respuesta);
                Schedule schedule = new Schedule();

                schedule.setIdSchedule(Long.parseLong(scheduleJson.get("idSchedule")
                        .toString()));
                Doctor doctorSchedule = doctorFromJson((JSONObject) scheduleJson
                        .get("doctorUsername"));
                schedule.setDoctorUsername(doctorSchedule);
                if(scheduleJson.get("monday") != null) {
                    schedule.setMonday(scheduleJson.get("monday").toString());
                }
                if(scheduleJson.get("tuesday") != null) {
                    schedule.setTuesday(scheduleJson.get("tuesday").toString());
                }
                if(scheduleJson.get("wednesday") != null) {
                    schedule.setWednesday(scheduleJson.get("wednesday").toString());
                }
                if(scheduleJson.get("thursday") != null) {
                    schedule.setThursday(scheduleJson.get("thursday").toString());
                }
                if(scheduleJson.get("friday") != null) {
                    schedule.setFriday(scheduleJson.get("friday").toString());
                }

                horario = schedule;

                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }
        return horario;
    }



    public boolean setAppointmentFinish(Appointment appointment) {
        boolean result = false;
        try {
            String urlBusqueda = URL_BASE
                    + "schedulemanager/finishappointment?"
                    + "idAppointment="
                    + (appointment.getIdAppointment().toString());
            url = new URL(urlBusqueda);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "text/plain");

            int codigo = conexion.getResponseCode();
            System.err.println("CODIGO "+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();
                result = Boolean.parseBoolean(respuesta);
                entrada.close();
            }

        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }

        return result;
    }




    public List<Appointment> getAllAppointmentsFor(Doctor doctor, Date date) {
        List<Appointment> citas = new ArrayList<Appointment>();
        try {

            String urlBusqueda = URL_BASE + "schedulemanager/appointmentsfor?"
                    + "username=" + URLEncoder.encode(doctor.getUsername(), "UTF-8")
                    + "&date=" + obtainFormatedDate(date);
            System.err.println(urlBusqueda);

            url = new URL(urlBusqueda);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");

            int codigo = conexion.getResponseCode();
            Log.e("CODIGO APP","--"+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object objectAppointment = JSONValue.parse(respuesta);
                JSONArray appointmentList = (JSONArray) objectAppointment;
                for (Object elem : appointmentList) {
                    JSONObject appointmentJson = (JSONObject) elem;
                    Appointment appointment = new Appointment();

                    appointment.setDate(date);
                    appointment.setDoctorUsername(doctorFromJson((JSONObject) appointmentJson
                            .get("doctorUsername")));

                    appointment.setIdAppointment(Long.parseLong(appointmentJson.get(
                            "idAppointment").toString()));

                    appointment.setIscanceled(Boolean.getBoolean(appointmentJson.get(
                            "iscanceled").toString()));

                    appointment.setIsFinished(Boolean.getBoolean(appointmentJson.get(
                            "isFinished").toString()));

                    appointment.setPatientNss((patientFromJson((JSONObject) appointmentJson
                            .get("patientNss"))));

                    appointment.setTime(appointmentJson.get("time").toString());

                    citas.add(appointment);
                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }
        return citas;
    }

    public List<Appointment> getNextAppointment(Patient p) {
        List<Appointment> citas = new ArrayList<Appointment>();
        try {

            String urlBusqueda = URL_BASE + "schedulemanager/nextappointment?"
                    + "nss=" + URLEncoder.encode(p.getNss(), "UTF-8");

            System.err.println(urlBusqueda);

            url = new URL(urlBusqueda);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");

            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object objectAppointment = JSONValue.parse(respuesta);
                JSONArray appointmentList = (JSONArray) objectAppointment;
                Log.e("SIZE ARRAY APP", appointmentList.size()+"");
                for (Object elem : appointmentList) {

                    JSONObject appointmentJson = (JSONObject) elem;
                    Appointment appointment = new Appointment();

                    if(appointmentJson.get("date") != null){
                        try {
                            String dateForApp = appointmentJson.get("date").toString().substring(0, 10);
                            String[] dateArray = dateForApp.split("-");
                            int year = Integer.parseInt(dateArray[0]);
                            int month = Integer.parseInt(dateArray[1]);
                            int day = Integer.parseInt(dateArray[2]);
                            appointment.setDate(new Date(day, month, year));
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }


                    appointment.setDoctorUsername(doctorFromJson((JSONObject) appointmentJson
                            .get("doctorUsername")));

                    appointment.setIdAppointment(Long.parseLong(appointmentJson.get(
                            "idAppointment").toString()));

                    appointment.setIscanceled(Boolean.valueOf(appointmentJson.get(
                            "iscanceled").toString()));
                    Log.e("DESPUES",appointmentJson.get(
                            "iscanceled").toString());

                    appointment.setIsFinished(Boolean.valueOf(appointmentJson.get(
                            "isFinished").toString()));


                    appointment.setPatientNss((patientFromJson((JSONObject) appointmentJson
                            .get("patientNss"))));

                    appointment.setTime(appointmentJson.get("time").toString());

                    if(appointment.getIscanceled() == false|| appointment.getIsFinished() == false){
                        citas.add(appointment);
                    }


                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }
        return citas;
    }


    private Doctor doctorFromJson(JSONObject doctorJson){
        Doctor doctor = new Doctor();
        if(doctorJson != null){
            doctor.setUsername(doctorJson.get("username").toString());
            doctor.setFirstName(doctorJson.get("firstName").toString());
            doctor.setLastName(doctorJson.get("lastName").toString());
            doctor.setPassword(doctorJson.get("password").toString());
            doctor.setLicense(doctorJson.get("license").toString());

            if(doctorJson.get("specialty") != null){
                doctor.setSpecialty(doctorJson.get("specialty").toString());
            }
        }
        return doctor;
    }

    private Patient patientFromJson(JSONObject patientJson){
        Patient patient = new Patient();
        if(patientJson != null){
            patient.setNss(patientJson.get("nss").toString());
            patient.setFirstName(patientJson.get("firstName").toString());
            patient.setLastName(patientJson.get("lastName").toString());
            patient.setAddress(patientJson.get("address").toString());
            patient.setIsActive(Boolean.valueOf(patientJson.get("isActive")
                    .toString()));

            if(patientJson.get("doctorUsername") != null){
                JSONObject doctorJson = (JSONObject) patientJson.get("doctorUsername");
                Doctor patientDoctor = doctorFromJson(doctorJson);
                patient.setDoctorUsername(patientDoctor);
            }
        }
        return patient;
    }

    private String obtainFormatedDate(Date date){
        String day = "";
        String month = "";
        if(date.getDia()<=9){
            day = "0"+date.getDia();
        }else{
            day = date.getDia()+"";
        }
        if(date.getMes()<=9){
            month = "0"+date.getMes();
        }else{
            month = date.getMes()+"";
        }
        return day+"/"+month+"/"+date.getYear();
    }
}
