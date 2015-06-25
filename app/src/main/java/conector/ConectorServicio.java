/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author manuelmartinez
 */
public class ConectorServicio {

    //se implementa un singleton
    private static final ConectorServicio INSTANCE = new ConectorServicio();

    public static ConectorServicio getInstance() {
        return INSTANCE;
    }

    public static final String URL_BASE = "http://192.168.137.1:8080/HospitAppServer/webresources/";
    private URL url;
    private HttpURLConnection conexion;
    private OutputStream os;

    /////Doctor*****************************************************************

    private Doctor doctorFromJson(JSONObject doctorJson) {
        Doctor doctor = new Doctor();
        if (doctorJson != null) {

                doctor.setUsername(doctorJson.get("username").toString());
                doctor.setFirstName(doctorJson.get("firstName").toString());
                doctor.setLastName(doctorJson.get("lastName").toString());
                doctor.setPassword(doctorJson.get("password").toString());
                doctor.setLicense(doctorJson.get("license").toString());

                if (doctorJson.get("specialty") != null) {
                    doctor.setSpecialty(doctorJson.get("specialty").toString());
                }

            return doctor;
        }
        return null;
    }

    private Patient patientFromJson(JSONObject patientJson) {
        Patient patient = new Patient();
        if (patientJson != null) {
            try {
                patient.setNss(patientJson.get("nss").toString());

                patient.setFirstName(patientJson.get("firstName").toString());
                patient.setLastName(patientJson.get("lastName").toString());
                patient.setAddress(patientJson.get("address").toString());
                patient.setIsActive(Boolean.valueOf(patientJson.get("isActive")
                        .toString()));
                patient.setPassword(patientJson.get("password").toString());

                if (patientJson.get("doctorUsername") != null) {
                    JSONObject doctorJson = (JSONObject) patientJson.get("doctorUsername");
                    Doctor patientDoctor = doctorFromJson(doctorJson);
                    patient.setDoctorUsername(patientDoctor);
                }
            } catch (Exception e) {

            }
            return patient;
        }
        return null;
    }



    public Doctor obtenerDoctor(String username){
        Doctor resultado = null;
        String urlBase = URL_BASE + "doctor/"+username;
        System.err.println("---CODIGO "+urlBase);
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();
            System.err.println("---CODIGO "+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object objectDoctorJson = JSONValue.parse(respuesta);
                JSONObject doctorJson = (JSONObject) objectDoctorJson;
                System.err.println(doctorJson.toString());
                Doctor doctor = doctorFromJson(doctorJson);
                resultado = doctor;
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;

    }


    public List<Doctor> obtenerListaDoctor() {
        List<Doctor> resultado = new ArrayList<>();
        String urlBase = URL_BASE + "doctor";
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();

            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object doctorArrayJson = JSONValue.parse(respuesta);
                JSONArray doctorArray = (JSONArray) doctorArrayJson;
                for (Object elem : doctorArray) {
                    JSONObject doctorJson = (JSONObject) elem;
                    Doctor doctor = doctorFromJson(doctorJson);
                    resultado.add(doctor);

                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }catch(Exception e){

        }
        return resultado;
    }

    public boolean eliminarDoctor(String username) {
        boolean resultado = false;
        String urlBase = URL_BASE + "doctor";
        try {
            url = new URL(urlBase + "/" + username);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("DELETE");
            int codigo = conexion.getResponseCode();
            if (codigo / 100 == 2) {
                resultado = true;
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;
    }

    /* Se ha omitido el método crearDoctor de manuel dado que ya existe el mismo
    en ConectorDoctorManager bajo en nombre saveNewDoctor que utiliza los management,
    el de a continuación utiliza los facade pero si se desea incluir
    favor de descomentar la función
    */

/*  ---- DESCOMENTAR PARA CREAR DOCTOR MEDIANTE FACADE

    public void crearDoctor(Doctor doc) {
        Map<String, String> mapaDatos = new HashMap<String, String>();
        mapaDatos.put("username", doc.getUsername());
        mapaDatos.put("firstName", doc.getFirstName());
        mapaDatos.put("lastName", doc.getLastName());
        mapaDatos.put("license", doc.getLicense());
        mapaDatos.put("specialty", doc.getSpecialty());
        mapaDatos.put("password", doc.getPassword());
        JSONObject obj = new JSONObject(mapaDatos);
        String datos = obj.toString();
        byte[] porEnviar = datos.getBytes();

        String cadena = URL_BASE + "doctor";

        try {
            url = new URL(cadena);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setDoOutput(true);

            conexion.setRequestMethod("POST");

            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setRequestProperty("Content-Length", Integer.toString(porEnviar.length));
            os = conexion.getOutputStream();
            os.write(porEnviar);

            int codigo = conexion.getResponseCode();
            System.out.println("Codigo recibido" + codigo);
            if (codigo / 100 != 2) {
                System.out.println("Error en Codigo recibido" + codigo);
                return;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 */

    public void updateDoctor(Doctor doctor) {
        Map<String, String> mapaDatos = new HashMap<String, String>();
        mapaDatos.put("username", doctor.getUsername());
        mapaDatos.put("firstName", doctor.getFirstName());
        mapaDatos.put("lastName", doctor.getLastName());
        mapaDatos.put("license", doctor.getLicense());
        mapaDatos.put("specialty", doctor.getSpecialty());
        mapaDatos.put("password", doctor.getPassword());
        JSONObject obj = new JSONObject(mapaDatos);
        String datos = obj.toString();
        byte[] porEnviar = datos.getBytes();

        String cadena = URL_BASE + "doctor";
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
            System.out.println("Codigo recibido" + codigo);
            if (codigo / 100 != 2) {
                System.out.println("Error en Codigo recibido" + codigo);
                return;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////Doctor*****************************************************************

    /////Patient****************************************************************

    public Patient obtenerPatient(String nss){
        Patient resultado = null;
        String urlBase = URL_BASE + "patient/"+nss;
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();
            Log.e("codigo: ","--"+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();
                Log.e("actualizarPatient",respuesta);

                Object objectPatientJson = JSONValue.parse(respuesta);
                JSONObject patientJson = (JSONObject) objectPatientJson;

                Patient patient = patientFromJson(patientJson);

                resultado = patient;
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;

    }

    public List<Patient> obtenerListaPatient() {
        List<Patient> resultado = new ArrayList<>();
        String urlBase = URL_BASE + "patient";
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");

            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object listPatientJson = JSONValue.parse(respuesta);
                JSONArray listPatient = (JSONArray) listPatientJson;

                for (Object elem : listPatient) {
                    JSONObject patientJson = (JSONObject) elem;
                    Patient patient = patientFromJson(patientJson);

                    // Obtener la información del Doctor
                    JSONObject doctorJson = (JSONObject) patientJson.get("doctorUsername");
                    Doctor doctor = doctorFromJson(doctorJson);
                    patient.setDoctorUsername(doctor);
                    patient.setIsActive(Boolean.valueOf(patientJson.get("isActive").toString()));

                    resultado.add(patient);
                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultado;
    }

    public boolean eliminarPatient(String nss) {
        boolean resultado = false;
        String urlBase = URL_BASE + "patient";
        try {
            url = new URL(urlBase + "/" + nss);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("DELETE");
            int codigo = conexion.getResponseCode();
            if (codigo / 100 == 2) {
                resultado = true;
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;
    }

     /* Se ha omitido el método crearPaciente de manuel dado que ya existe el mismo
    en ConectorPatientManager bajo en nombre saveNewPatient que utiliza los management,
    el de a continuación utiliza los facade pero si se desea incluir
    favor de descomentar la función
    */

    /*

    public void crearPatient(Patient pac) {
        Map<String, String> mapaDatos = new HashMap<String, String>();
        mapaDatos.put("nss", pac.getNss());
        mapaDatos.put("first_name", pac.getFirstName());
        mapaDatos.put("last_name", pac.getLastName());
        mapaDatos.put("address", pac.getAddress());
        mapaDatos.put("doctor_username", pac.getDoctorUsername());
        mapaDatos.put("password", pac.getPassword());
        mapaDatos.put("is_active", Boolean.toString(pac.isIsActive()));
        JSONObject obj = new JSONObject(mapaDatos);
        String datos = obj.toString();
        byte[] porEnviar = datos.getBytes();

        String cadena = URL_BASE + "patient";
        try {
            url = new URL(cadena);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setDoOutput(true);

            conexion.setRequestMethod("POST");

            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setRequestProperty("Content-Length", Integer.toString(porEnviar.length));
            os = conexion.getOutputStream();
            os.write(porEnviar);

            int codigo = conexion.getResponseCode();
            System.out.println("Codigo recibido" + codigo);
            if (codigo / 100 != 2) {
                System.out.println("Error en Codigo recibido" + codigo);
                return;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    public void updatePatient(Patient patient) {
        Map<String, String> mapaDatos = new HashMap<String, String>();
        mapaDatos.put("nss", patient.getNss());
        mapaDatos.put("firstName", patient.getFirstName());
        mapaDatos.put("lastName", patient.getLastName());
        mapaDatos.put("address", patient.getAddress());
        mapaDatos.put("password", patient.getPassword());
        mapaDatos.put("isActive", Boolean.toString(patient.getIsActive()));

        Doctor doctor = patient.getDoctorUsername();
        Map<String, String> mapaDoctor = new HashMap<String, String>();
        mapaDoctor.put("username", doctor.getUsername());
        mapaDoctor.put("firstName", doctor.getFirstName());
        mapaDoctor.put("lastName", doctor.getLastName());
        mapaDoctor.put("license", doctor.getLicense());
        mapaDoctor.put("password", doctor.getPassword());
        if(doctor.getSpecialty() != null){
            mapaDoctor.put("specialty", doctor.getSpecialty());
        }
        String docJSON = new JSONObject(mapaDoctor).toJSONString();
        System.err.println("----------"+docJSON);
        mapaDatos.put("doctorUsername",docJSON);

        JSONObject patientJson = new JSONObject(mapaDatos);
        String patientData = patientJson.toString();
        patientData = patientData.replace("\\", "");
        patientData = patientData.replace("\"{", "{");
        patientData = patientData.replace("}\"", "}");
        //Duda sobre como mandar Doctor
        byte[] porEnviar = patientData.getBytes();

        System.out.println(patientData);

        String cadena = URL_BASE + "patient/"+patient.getNss();
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
                return;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /////Appointment************************************************************
    public Appointment obtenerAppointment(Long idAppointment){
        Appointment resultado = null;
        String urlBase = URL_BASE + "appointment/"+idAppointment;
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();

            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object objectAppointmentJson = JSONValue.parse(respuesta);
                JSONObject appointmentJson = (JSONObject) objectAppointmentJson;
                System.err.println(appointmentJson.toString());
                Appointment appointment = new Appointment();
                appointment.setIdAppointment(Long.parseLong(appointmentJson.get("idAppointment").toString()));
                appointment.setTime(appointmentJson.get("time").toString());

                /* Manejar el caso de los atributos que pueden ser nulos de
                    un appointment */
                if(appointmentJson.get("iscanceled") != null ){
                    appointment.setIscanceled(Boolean.valueOf(appointmentJson.get("iscanceled").toString()));
                }
                if(appointmentJson.get("isFinished") != null){
                    appointment.setIsFinished(Boolean.valueOf(appointmentJson.get("isFinished").toString()));
                }
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

                JSONObject patientJson = (JSONObject) appointmentJson.get("patientNss");
                Patient patient = patientFromJson(patientJson);

                JSONObject doctorJson = (JSONObject) appointmentJson.get("doctorUsername");
                Doctor doctor = doctorFromJson(doctorJson);

                appointment.setPatientNss(patient);
                appointment.setDoctorUsername(doctor);

                resultado = appointment;
                entrada.close();
            }

        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();

        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        } catch( Exception e){

        }

        return resultado;

    }


    public List<Appointment> obtenerListaAppointment() {
        List<Appointment> resultado = new ArrayList<>();
        String urlBase = URL_BASE + "appointment";
        Log.e("DEBUG ","Aqui llego"+urlBase);
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();
            Log.e("CODIGO ","codigo "+codigo);
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();

                Object objectListAppointment = JSONValue.parse(respuesta);
                JSONArray listAppointment = (JSONArray) objectListAppointment;

                for (Object elem : listAppointment) {
                    JSONObject appointmentJson = (JSONObject) elem;
                    Appointment appointment = new Appointment();
                    Log.e("ID:APP","id: "+ appointmentJson.get("idAppointment").toString() );

                    appointment.setIdAppointment(Long.parseLong(appointmentJson.get("idAppointment").toString()));
                    appointment.setTime(appointmentJson.get("time").toString());

                    //Obtener los datos del doctor
                    JSONObject doctorJson = (JSONObject) appointmentJson.get("doctorUsername");
                    Doctor doctor = doctorFromJson(doctorJson);
                    appointment.setDoctorUsername(doctor);

                    //Obtener los datos del paciente
                    JSONObject patientJson = (JSONObject) appointmentJson.get("patientNss");
                    Patient patient = patientFromJson(patientJson);

                    appointment.setPatientNss(patient);

                    /* Manejar el caso de los atributos que pueden ser nulos de
                    un appointment */
                    if(appointmentJson.get("iscanceled") != null ){
                        appointment.setIscanceled(Boolean.valueOf(appointmentJson.get("iscanceled").toString()));
                    }
                    if(appointmentJson.get("isFinished") != null){
                        appointment.setIsFinished(Boolean.valueOf(appointmentJson.get("isFinished").toString()));
                    }
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
                    Log.e("Appointment: ","ID: "+appointment.getIdAppointment());
                    resultado.add(appointment);
                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }catch(Exception e ){
            Log.e("CAYO EN EXCEPCION","HI");

        }
        return resultado;
    }

    public boolean eliminarAppointment(String id) {
        boolean resultado = false;
        String urlBase = URL_BASE + "appointment";
        try {
            url = new URL(urlBase + "/" + id);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("DELETE");
            int codigo = conexion.getResponseCode();
            if (codigo / 100 == 2) {
                resultado = true;
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;
    }

    /////Schedule***************************************************************

    public Schedule obtenerSchedule(Long idSchedule) {
        Schedule resultado = null;
        String urlBase = URL_BASE + "schedule/"+idSchedule;
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();
                Object objectSchedule = JSONValue.parse(respuesta);
                JSONObject scheduleJson = (JSONObject) objectSchedule;

                Schedule schedule = new Schedule();
                schedule.setIdSchedule(Long.parseLong(scheduleJson.get("idSchedule").toString()));

                if (scheduleJson.get("monday") != null) {
                    schedule.setMonday(scheduleJson.get("monday").toString());
                }

                if (scheduleJson.get("tuesday") != null) {
                    schedule.setTuesday(scheduleJson.get("tuesday").toString());
                }

                if (scheduleJson.get("wednesday") != null) {
                    schedule.setWednesday(scheduleJson.get("wednesday").toString());
                }

                if (scheduleJson.get("thursday") != null) {
                    schedule.setThursday(scheduleJson.get("thursday").toString());
                }

                if (scheduleJson.get("friday") != null) {
                    schedule.setFriday(scheduleJson.get("friday").toString());
                }

                //Obtener los datos del doctor
                JSONObject doctorJson = (JSONObject) scheduleJson.get("doctorUsername");
                Doctor doctor = doctorFromJson(doctorJson);
                schedule.setDoctorUsername(doctor);
                resultado = schedule;

                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }catch(Exception e){

        }
        return resultado;
    }


    public List<Schedule> obtenerListaSchedule() {
        List<Schedule> resultado = new ArrayList<>();
        String urlBase = URL_BASE + "schedule";
        try {
            url = new URL(urlBase);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "application/json");
            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada
                        = new BufferedReader(
                        new InputStreamReader(is));
                String respuesta = entrada.readLine();
                Object objectListSchedule = JSONValue.parse(respuesta);
                JSONArray scheduleArray = (JSONArray) objectListSchedule;

                for (Object elem : scheduleArray) {

                    JSONObject scheduleJson = (JSONObject) elem;
                    Schedule schedule = new Schedule();

                    schedule.setIdSchedule(Long.parseLong(scheduleJson.get("idSchedule").toString()));

                    if(scheduleJson.get("monday") != null){
                        schedule.setMonday(scheduleJson.get("monday").toString());
                    }

                    if(scheduleJson.get("tuesday") != null){
                        schedule.setTuesday(scheduleJson.get("tuesday").toString());
                    }

                    if(scheduleJson.get("wednesday") != null){
                        schedule.setWednesday(scheduleJson.get("wednesday").toString());
                    }

                    if(scheduleJson.get("thursday") != null){
                        schedule.setThursday(scheduleJson.get("thursday").toString());
                    }

                    if(scheduleJson.get("friday") != null){
                        schedule.setFriday(scheduleJson.get("friday").toString());
                    }

                    //Obtener los datos del doctor
                    JSONObject doctorJson = (JSONObject) scheduleJson.get("doctorUsername");
                    Doctor doctor = doctorFromJson(doctorJson);
                    schedule.setDoctorUsername(doctor);
                    resultado.add(schedule);
                }
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }catch(Exception e){

        }
        return resultado;
    }

    public boolean eliminarSchedule(String id) {
        boolean resultado = false;
        String urlBase = URL_BASE + "schedule";
        try {
            url = new URL(urlBase + "/" + id);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("DELETE");
            int codigo = conexion.getResponseCode();
            if (codigo / 100 == 2) {
                resultado = true;
            }
        } catch (MalformedURLException e) {
            System.err.println("Error en el URL base");
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Error al contactar al servidor");
            ioe.printStackTrace();
        }
        return resultado;
    }

}