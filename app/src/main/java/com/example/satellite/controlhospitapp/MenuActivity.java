package com.example.satellite.controlhospitapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import conector.ConectorServicio;
import entities.Doctor;
import entities.Global;
import entities.Patient;


public class MenuActivity extends Activity {

    TextView lblNombrePaciente;
    ImageButton btnEditarInformacion;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        lblNombrePaciente = (TextView) findViewById(R.id.lblPatientName);
        btnEditarInformacion = (ImageButton) findViewById(R.id.btnEditarInformacion);

        // Aun no existe implementación para la edición de datos del Paciente, futura implementación
        btnEditarInformacion.setEnabled(false);

        if(Global.patient.getFirstName() != null){
            lblNombrePaciente.setText(Global.patient.getFirstName());
        }

        // try to change the action bar color
        ActionBar actionBar;
        actionBar = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(
                Color.parseColor("#1865af"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }


    public void agendar(View view) {
        GetDoctorPatient taskDoctorPatient = new GetDoctorPatient();
        taskDoctorPatient.execute(new String[]{Global.patient.getNss()});
        checkPatientStateRegistrarCitas();
    }
    public void verCitasa(View view) {
        GetDoctorPatient taskDoctorPatient = new GetDoctorPatient();
        taskDoctorPatient.execute(new String[]{Global.patient.getNss()});
        checkPatientStateCitas();
    }

    public void logOut(View view){
        Global.patient=null;
        Intent i = new Intent(this,MainActivity.class );
        startActivity(i);
        this.finish();
    }

    public void checkPatientStateCitas() {

        if (Global.patient.getDoctorUsername() == null) {
            Toast.makeText(getApplicationContext(),
                    "Necesita tener asignado un médico para consultar sus citas", Toast.LENGTH_LONG).show();

        }else{
            Intent i = new Intent(getApplicationContext(),ConsultaCitasAndCancelActivity.class );
            startActivity(i);

        }
    }

    public void checkPatientStateRegistrarCitas(){

        if(Global.patient.getDoctorUsername() == null) {
            Toast.makeText(getApplicationContext(),
                    "Necesita tener asignado un médico para solicitar una cita",Toast.LENGTH_LONG).show();

        }else{
            Intent i = new Intent(this,AgendarCitasActivity.class );
            startActivity(i);
        }
    }


    private class GetDoctorPatient extends AsyncTask<String, Void, Doctor> {

        @Override
        protected Doctor doInBackground(String params[]) {
            ConectorServicio conectorServicio = ConectorServicio.getInstance();
            Patient patient = conectorServicio.obtenerPatient(params[0]);
            if(patient != null){
                return patient.getDoctorUsername();

            }else{
                return null;
            }

        }


        protected void onPostExecute(Doctor doctorOfPatient) {
            if(doctorOfPatient != null){
                Global.patient.setDoctorUsername(doctorOfPatient);
                Log.e("DOCTOR",doctorOfPatient.getFirstName()+"");
            }

        }
    }


}
