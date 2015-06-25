package com.example.satellite.controlhospitapp;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import conector.ConectorScheduleManager;
import entities.Appointment;
import entities.Doctor;
import entities.Global;
import entities.Schedule;
import utilities.Date;
import utilities.IntervalFilter;

public class AgendarCitasActivity extends ActionBarActivity {

    // Date related

    final int Date_Dialog_ID=0;
    Button change_date;
    int cDay,cMonth,cYear; // this is the instances of the current date
    Calendar cDate;
    int sDay,sMonth,sYear; // this is the instances of the entered date
    private OnDateSetListener onDateSet=new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            System.out.println("2");
            sYear=year;
            sMonth=monthOfYear;
            sDay=dayOfMonth;
            Log.e("El mes escogido ",sMonth+"");

            new GettingSchedule().execute(new String[]{sDay+"", (sMonth+1)+"", sYear+""});

            btnScheduleApp.setTextColor(getResources().getColor(R.color.black));
            btnScheduleApp.setBackgroundColor(getResources().getColor(R.color.light_gray));

            btnScheduleApp.setVisibility(Button.VISIBLE);

            btnScheduleApp.setEnabled(true);
        }
    };
    String horaAppointment;
    String fechaAppointment;
    TextView lblDoctorName;
    Button btnScheduleApp;
    ListView listSchedule;
    TextView lblHeaderList;
    int globalPosition=-1;
    TextView lblHourSelected;
    TextView lblFechaAppointment;
    TextView lblHoraAppointment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_citas);



        change_date=(Button)findViewById(R.id.btnFecha);
        lblDoctorName = (TextView)findViewById(R.id.lblDoctorName);
        btnScheduleApp = (Button) findViewById(R.id.btnScheduleApp);
        lblHeaderList = (TextView) findViewById(R.id.lblCabeceraHorarios);
        listSchedule = (ListView) findViewById(R.id.listAvailableSchedule);
        lblHourSelected = (TextView) findViewById(R.id.lblHourSelected);
        lblFechaAppointment = (TextView) findViewById(R.id.lblFechaAppointment);
        lblHoraAppointment = (TextView) findViewById(R.id.lblHoraAppointment);

        listSchedule.setCacheColorHint(getResources().getColor(R.color.blue));


        btnScheduleApp.setBackgroundColor(getResources().getColor(R.color.pink));
        btnScheduleApp.setEnabled(false);
        btnScheduleApp.setVisibility(Button.INVISIBLE);
        Toast.makeText(getApplicationContext(),"Para solicitar una cita primeramente elige une fecha\n" +
                "Posteriormente selecciona uno de los horarios disponibles para agendar tu cita", Toast.LENGTH_LONG).show();

        if(Global.patient.getDoctorUsername() != null) {
            lblDoctorName.setText(Global.patient.getDoctorUsername().toString());
        }/*else{
            Toast.makeText(getApplicationContext(),"Necesita tener asignado un médico para solicitar una cita",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this,MenuActivity.class );
            startActivity(i);
            finish();
        }*/

        change_date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lblFechaAppointment.setText(fechaAppointment);
                lblHoraAppointment.setText(horaAppointment);

                //triggers the DatePickerDialog
                showDialog(Date_Dialog_ID);
            }
        });

       listSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                               public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                                                  globalPosition=position;
                                                  btnScheduleApp.setBackgroundColor(getResources().getColor(R.color.pink));
                                                   btnScheduleApp.setTextColor(getResources().getColor(R.color.white));

                                               }
                                           });

               btnScheduleApp.setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       CreateAppointments createAppointments = new CreateAppointments();
                       if(listSchedule.getAdapter().isEmpty()){
                           Toast.makeText(getApplicationContext(),"No existen horarios disponibles para la fecha solicitada",Toast.LENGTH_LONG).show();
                       }else {
                           if (globalPosition != -1) {
                               String hour = listSchedule.getItemAtPosition(globalPosition).toString().substring(0, 2);
                               if (hour.endsWith(":")) {
                                   hour = hour.substring(0, 1);
                               }
                               createAppointments.execute(new String[]{hour, sDay + "", sMonth + ""});
                               globalPosition = -1;
                           }
                       }

                   }
               });

        //getting current date
        cDate=Calendar.getInstance();
        cDay=cDate.get(Calendar.DAY_OF_MONTH);
        cMonth=cDate.get(Calendar.MONTH);
        cYear=cDate.get(Calendar.YEAR);

        //assigning the edittext with the current date in the beginning
        sDay=cDay;
        sMonth=cMonth;
        sYear=cYear;
        updateDateDisplay(sYear,sMonth,sDay);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case Date_Dialog_ID:
                return new DatePickerDialog(this, onDateSet, cYear, cMonth,
                        cDay);
        }
        return null;
    }

    private void updateDateDisplay(int year,int month,int date) {

    }


    /* To Getting all the available time for the Doctor in the specified Date*/

    private class GettingSchedule extends AsyncTask<String, Void, List<String>> {

        protected List<String> doInBackground(String params[]) {
            List<String> horas = new ArrayList<String>();
            Doctor doctor = Global.patient.getDoctorUsername();
            Log.e("Doctor is null", doctor.getUsername());

            ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
            Schedule doctorSchedule = conectorScheduleManager.getAvailableSchedule(doctor, false);
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule == null));
            /*Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getMonday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getTuesday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getWednesday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getThursday()));
            Log.e("--DoctorSchedule null?", "--"+(doctorSchedule.getFriday()));*/
            Date appointmentDate = new Date(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
            Log.e("FECHA OBTENIDA", (appointmentDate.toString())+"");
            String intervalosTime="";




            int dayWeek = appointmentDate.getDayOfWeek();
            Log.e("DAY WEEK",dayWeek+"");

            if((doctorSchedule == null) == false && (doctorSchedule.getMonday().trim().length() > 0) && dayWeek==2){
                intervalosTime = doctorSchedule.getMonday();

            }else if((doctorSchedule == null) == false && (doctorSchedule.getTuesday().trim().length() > 0) && dayWeek ==3){
                intervalosTime = doctorSchedule.getTuesday();

            }else if((doctorSchedule == null) == false && (doctorSchedule.getWednesday().trim().length() > 0) && dayWeek ==4){
                intervalosTime = doctorSchedule.getWednesday();
            }else if((doctorSchedule == null) == false && (doctorSchedule.getThursday().trim().length() > 0) && dayWeek ==5){
                intervalosTime = doctorSchedule.getThursday();
            }else if((doctorSchedule == null) == false && (doctorSchedule.getFriday().trim().length() > 0) && dayWeek ==6){
                intervalosTime = doctorSchedule.getFriday();
            }else{
                return Collections.emptyList();
            }
            IntervalFilter intervalFilter = new IntervalFilter();
            Log.e("Intervalo verdadero",intervalosTime);

            String hoursCadena = intervalFilter.getHours(intervalosTime);
            Log.e("HORAS DE CADENA", hoursCadena);

            String[] hoursForAppointment = hoursCadena.split(",");

            for(String hour : hoursForAppointment){
                horas.add(hour+":00");
            }

            return horas;
        }

        @Override
        protected void onPostExecute(List<String> horas) {
            ArrayAdapter<Appointment> adapter = new ArrayAdapter
                    (AgendarCitasActivity.this, android.R.layout.simple_list_item_single_choice,horas);
            Log.e("AdapterEmpy", adapter.isEmpty()+"");
            if(adapter.isEmpty()){
                btnScheduleApp.setEnabled(false);
                btnScheduleApp.setVisibility(Button.INVISIBLE);
                Toast.makeText(getApplicationContext(),"No existen horarios disponibles para la fecha que"+
                        " ha especificado",Toast.LENGTH_LONG).show();

            }

            listSchedule.setAdapter(adapter);


        }


    }

    /* To schedule the specified Appointment */

    private class CreateAppointments extends AsyncTask<String, Void, Long> {

        protected Long doInBackground(String params[]) {
            Log.e("ENTRO A REGISTRAR CITA","btnREgistrarCita");
            ConectorScheduleManager conectorScheduleManager =
                    new ConectorScheduleManager().getInstance();
            List<Appointment> appointments = conectorScheduleManager.getNextAppointment(Global.patient);

            Appointment appointment = new Appointment();
            String hour = params[0];
            int day = Integer.parseInt(params[1]);
            int month = Integer.parseInt(params[2]);
            int year = 2015;
            Log.e("DAY--",day+"");
            Log.e("MONTH--",month+"");
            Log.e("HOUR",hour);
            Date appointmentDate = new Date(day,month+1,year);




            if(appointmentDate.isBefore(new Date())){

                return -1L;
            }

            for(Appointment app: appointments){
                if(app.getDate().equals(appointmentDate) && app.getIscanceled() == false){
                    return -2L;
                }
            }
            fechaAppointment = appointmentDate.toString();
            horaAppointment = hour+":00";
            appointment.setDate(appointmentDate);
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
        protected void onPostExecute(Long answer) {
            String mensaje="";
            if(answer == -1L) {
                Toast.makeText(getApplicationContext(), "La fecha seleccionada debe ser posterior a la fecha actual"
                        , Toast.LENGTH_LONG).show();
                btnScheduleApp.setVisibility(Button.INVISIBLE);
            }else if(answer == -2L){
                Toast.makeText(getApplicationContext(), "No puede agendar más de una cita el mismo día"
                        , Toast.LENGTH_LONG).show();
                btnScheduleApp.setVisibility(Button.INVISIBLE);

            }else{
                lblFechaAppointment.setText(fechaAppointment);
                lblHoraAppointment.setText(horaAppointment);
                Toast.makeText(getApplicationContext(),"La cita se ha registrado con éxito",Toast. LENGTH_LONG).show();

            }

        }

    }


}
