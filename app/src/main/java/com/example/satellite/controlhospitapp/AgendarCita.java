package com.example.satellite.controlhospitapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import conector.ConectorScheduleManager;
import entities.Appointment;
import entities.Doctor;
import entities.Global;
import entities.Schedule;
import utilities.Date;
import utilities.IntervalFilter;


public class AgendarCita extends Activity {
    Spinner spinnerDay;
    Spinner spinnerMonth;
    EditText txtNombreDoctor;
    Button btnRegistrarCita;
    Button btnCargarHorario;
    ListView listSchedule;
    List<Integer> meses;
    List<Integer> dias;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_cita);

        spinnerDay = (Spinner) findViewById(R.id.spinnerDia);
        spinnerMonth =(Spinner) findViewById(R.id.spinnerMonth);
        txtNombreDoctor = (EditText) findViewById(R.id.txtNombreDoctor);
        btnRegistrarCita = (Button) findViewById(R.id.btnRegistrarCita);
        btnCargarHorario = (Button) findViewById(R.id.btnCargarHorario);
        listSchedule = (ListView) findViewById(R.id.listSchedule);
        meses = new ArrayList<Integer>();
        dias = new ArrayList<Integer>();

        new FillingDays().execute();
        new FillingMonths().execute();

        btnCargarHorario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("entro a horario", "cargando");
                String day = spinnerDay.getSelectedItem().toString();
                String month = spinnerMonth.getSelectedItem().toString();
                String year = "2015";
                new GettingSchedule().execute(new String[]{day, month, year});
            }
        });

        btnRegistrarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("entro a","registrar");
                String day = spinnerDay.getSelectedItem().toString();
                String month = spinnerMonth.getSelectedItem().toString();
                String hour= listSchedule.getItemAtPosition(0).toString();
                String[] dataAppointment = new String[]{hour,day,month};

                new CreateAppointments().execute(dataAppointment);
            }
        });







    }


    private class FillingDays extends AsyncTask<String, Void, List<Integer>> {

        protected List<Integer> doInBackground(String params[]) {
            for(int i=1;i<=31;i++){
                dias.add(i);
            }
            return dias;
        }

        @Override
        protected void onPostExecute(List<Integer> dias) {
            ArrayAdapter<Appointment> adapter = new ArrayAdapter
                    (AgendarCita.this, android.R.layout.simple_spinner_item,dias);
            spinnerDay.setAdapter(adapter);
        }

    }


    private class FillingMonths extends AsyncTask<String, Void, List<Integer>> {

        protected List<Integer> doInBackground(String params[]) {
            for(int i=1;i<=12;i++){
               meses.add(i);
            }
            return dias;
        }

        @Override
        protected void onPostExecute(List<Integer> meses) {
            ArrayAdapter<Appointment> adapter = new ArrayAdapter
                    (AgendarCita.this, android.R.layout.simple_spinner_item,meses);
            spinnerMonth.setAdapter(adapter);
        }

    }

    private class CreateAppointments extends AsyncTask<String, Void, Long> {

        protected Long doInBackground(String params[]) {
            Log.e("ENTRO A REGISTRAR CITA","btnREgistrarCita");
            ConectorScheduleManager conectorScheduleManager =
                    new ConectorScheduleManager().getInstance();

            Appointment appointment = new Appointment();
            String hour = params[0];
            int day = Integer.parseInt(params[1]);
            int month = Integer.parseInt(params[2]);
            int year = 2015;
            Log.e("DAY--",day+"");
            Log.e("MONTH--",month+"");
            Log.e("HOUR",hour);

            appointment.setDate(new Date(day,month,year));
            appointment.setPatientNss(Global.patient);
            appointment.setDoctorUsername(Global.patient.getDoctorUsername());
            appointment.setTime(hour);
            appointment.setIscanceled(false);
            appointment.setIsFinished(false);
            appointment.setIdAppointment(455656l);
            long isConfirmed = conectorScheduleManager.scheduleAppointment(appointment);
            Log.e("ID DE LA NEW", isConfirmed+"");
            return isConfirmed;

        }

        @Override
        protected void onPostExecute(Long meses) {}

    }


    private class GettingSchedule extends AsyncTask<String, Void, List<Integer>> {

        protected List<Integer> doInBackground(String params[]) {
            List<Integer> horas = new ArrayList<Integer>();
            Doctor doctor = Global.patient.getDoctorUsername();
            Log.e("Doctor is null",doctor.getUsername());
            ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
            Schedule doctorSchedule = conectorScheduleManager.getAvailableSchedule(doctor, false);
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getMonday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getTuesday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getWednesday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getThursday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getFriday()));
            Date appointmentDate = new Date(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
            Log.e("FECHA OBTENIDA", (doctorSchedule==null)+"");
            String intervalosTime="";

            int dayWeek = appointmentDate.getDayOfWeek();
            Log.e("DAY WEEK",dayWeek+"");
            if(dayWeek==2){
                intervalosTime = doctorSchedule.getMonday();
            }else if(dayWeek ==3){
                intervalosTime = doctorSchedule.getTuesday();

            }else if(dayWeek ==4){
                intervalosTime = doctorSchedule.getWednesday();
            }else if(dayWeek ==5){
                intervalosTime = doctorSchedule.getThursday();
            }else if(dayWeek ==6){
                intervalosTime = doctorSchedule.getFriday();
            }
            IntervalFilter intervalFilter = new IntervalFilter();
            Log.e("Intervalo verdadero",intervalosTime);

            String hoursCadena = intervalFilter.getHours(intervalosTime);
            Log.e("HORAS DE CADENA", hoursCadena);

            String[] hoursForAppointment = hoursCadena.split(",");

            for(String hour : hoursForAppointment){
                horas.add(Integer.parseInt(hour));
            }

            return horas;
        }

        @Override
        protected void onPostExecute(List<Integer> horas) {
            ArrayAdapter<Appointment> adapter = new ArrayAdapter
                    (AgendarCita.this, android.R.layout.simple_spinner_item,horas);
            listSchedule.setAdapter(adapter);
        }


    }


}
