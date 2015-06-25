package com.example.satellite.controlhospitapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import conector.ConectorScheduleManager;
import conector.ConectorServicio;
import entities.Appointment;
import entities.Global;
import entities.Patient;

/**
 * Created by SATELLITE on 13/05/2015.
 */
public class CitasActivity extends Activity {
    ListView citas;

    EditText editTextDate;
    EditText editTextDoctorUsername;
    EditText editTextTime;
    Spinner spinnerCitas;
    Button btnCancelar;
    TextView lblCita;
    static Appointment appointment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vercitas);


        editTextDate = (EditText) findViewById(R.id.editFecha);
        editTextDoctorUsername = (EditText) findViewById(R.id.editDoctorName);
        editTextTime = (EditText) findViewById(R.id.editHora);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        spinnerCitas = (Spinner) findViewById(R.id.spinnerCitas);
        lblCita = (TextView) findViewById(R.id.lblIdApp);

        SendfeedbackJob job = new SendfeedbackJob();
        try{
            job.execute();
        }catch(Exception e){
            Log.e("Encontro expecion","excepcion");
        }

        spinnerCitas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Appointment app = (Appointment) parent.getItemAtPosition(position);
                lblCita.setText(app.getIdAppointment().toString());
                editTextDate.setText(app.getDate().toString());
                editTextDoctorUsername.setText(app.getDoctorUsername().getFirstName());
                editTextTime.setText(app.getTime()+":00");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextDate.setText("");
                editTextDoctorUsername.setText("");
                editTextTime.setText("");
            }
        });


        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Boton presionado","boton");
                SendCancelAppointment sendCancelAppointment = new SendCancelAppointment();
               sendCancelAppointment.execute(new String[]{lblCita.getText().toString()});

            }
        });

    }



    private class SendfeedbackJob extends AsyncTask<String, Void, List<Appointment>> {

        @Override
        protected List<Appointment> doInBackground(String params[]) {


            ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
            ConectorServicio conectorServicio = ConectorServicio.getInstance();
            Patient patient = conectorServicio.obtenerPatient(Global.patient.getNss());
            Log.e("Patient: ",Global.patient.getFirstName()+" "+Global.patient.getLastName());

            List <Appointment> nextAppointments = conectorScheduleManager.getNextAppointment(patient);
                Log.e("Appointment is null ", "----"+(nextAppointments==null));
             return nextAppointments;
        }


        protected void onPostExecute(List<Appointment> appointments) {
            ArrayAdapter<Appointment> adapter = new ArrayAdapter
                    (CitasActivity.this, android.R.layout.simple_spinner_item,appointments);
            spinnerCitas.setAdapter(adapter);

        }
    }



    private class SendCancelAppointment extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String params[]) {

            try {
                ConectorServicio conectorServicio = ConectorServicio.getInstance();
                ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
                String idApp = lblCita.getText().toString();
                Appointment app = conectorServicio.obtenerAppointment(Long.valueOf(params[0]));
                Log.e("Appointment to cancel", app.getIdAppointment()+"");
                boolean isPossible =conectorScheduleManager.cancelAppointment(app);
                Log.e("isPossible", "---"+isPossible);
            }catch(Exception e){
                Log.e("Appointment ","No possible");
                return false;
            }
            return true;
        }


        protected void onPostExecute(Boolean isCancel) {


        }
    }




}
