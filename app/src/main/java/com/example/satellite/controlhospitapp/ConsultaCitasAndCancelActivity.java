package com.example.satellite.controlhospitapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import conector.ConectorScheduleManager;
import conector.ConectorServicio;
import entities.Appointment;
import entities.Global;
import entities.Patient;

public class ConsultaCitasAndCancelActivity extends ActionBarActivity {
    MyCustomAdapter dataAdapter = null;
    ListView listViewAppointments;
    Button checkButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultar_citas);

        listViewAppointments = (ListView) findViewById(R.id.listViewAppointments);
        checkButton = (Button) findViewById(R.id.findSelected);

        if(Global.patient.getDoctorUsername()!=null) {
            SendfeedbackJob findData = new SendfeedbackJob();
            findData.execute();
        }else{
            Toast.makeText(getApplicationContext(),"No cuenta con citas previas", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this,MenuActivity.class );
            startActivity(i);
            finish();
        }

        listViewAppointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView Text
                Appointment appointment = (Appointment) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Marca las citas si deseas cancelar: " + appointment.getDate().toString()
                        , Toast.LENGTH_LONG).show();

            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer responseText = new StringBuffer();
                responseText.append("Las siguientes citas fueron canceladas...\n");

                List<Appointment> appointmentList = dataAdapter.appointmentList;
                if(appointmentList.isEmpty()){
                    Toast.makeText(getApplicationContext(),"No hay citas para cancelar",Toast.LENGTH_LONG).show();
                    return;
                }
                for(int i=0; i< appointmentList.size(); i++){
                    Appointment appointment = appointmentList.get(i);

                    if(appointment.getIscanceled()){
                        SendCancelAppointment sendCancelAppointment = new SendCancelAppointment();
                        sendCancelAppointment.execute(new String[]{appointment.getIdAppointment().toString()});
                        responseText.append("\n"+appointment.getDate().toString());
                        Toast.makeText(getApplicationContext(),responseText,Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }




    private class SendfeedbackJob extends AsyncTask<String, Void, List<Appointment>> {

        @Override
        protected List<Appointment> doInBackground(String params[]) {


            ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
            ConectorServicio conectorServicio = ConectorServicio.getInstance();
            Patient patient = conectorServicio.obtenerPatient(Global.patient.getNss());
            Log.e("Patient: ", Global.patient.getFirstName() + " " + Global.patient.getLastName());
            List<Appointment> appointmentsWithoutCancel= new ArrayList<Appointment>();

            List <Appointment> nextAppointments = conectorScheduleManager.getNextAppointment(patient);

            for(Appointment app: nextAppointments){
                if(app.getIscanceled() == false && app.getIsFinished() == false){
                    Log.e("IS canceled :",app.getIdAppointment()+" "+app.getIscanceled());
                    appointmentsWithoutCancel.add(app);
                }
            }

            Log.e("Appointment is null ", "----"+(nextAppointments==null));
            return appointmentsWithoutCancel;
        }


        protected void onPostExecute(List<Appointment> appointments) {
            if(appointments == null || appointments.isEmpty()) {
                Toast.makeText(getApplicationContext(),"No existen citas agendadas", Toast.LENGTH_LONG).show();
            }else{
                dataAdapter = new MyCustomAdapter(getApplicationContext(), R.layout.list_layout, appointments);
                // Assign adapter to ListView
                listViewAppointments.setAdapter(dataAdapter);
            }

        }
    }


    private class SendCancelAppointment extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String params[]) {

            String idAppointment = params[0];
            try {
                ConectorServicio conectorServicio = ConectorServicio.getInstance();
                ConectorScheduleManager conectorScheduleManager = ConectorScheduleManager.getInstance();
                Appointment app = conectorServicio.obtenerAppointment(Long.valueOf(idAppointment));
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
            if(isCancel) {
                SendfeedbackJob findData = new SendfeedbackJob();
                findData.execute();
            }else{
                Toast.makeText(getApplicationContext(),"La cita no ha podido ser cancelada", Toast.LENGTH_LONG);
            }

        }
    }


    private class MyCustomAdapter extends ArrayAdapter<Appointment> {

        private List<Appointment> appointmentList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<Appointment> appointmentList) {
            super(context, textViewResourceId, appointmentList);
            this.appointmentList = new ArrayList<Appointment>();
            this.appointmentList.addAll(appointmentList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));


            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_layout, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Appointment appointment = (Appointment) cb.getTag();
                        appointment.setIscanceled(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Appointment appointmentToCheck = appointmentList.get(position);
            holder.code.setText(" (" +  appointmentToCheck.getIdAppointment() + ")");
            holder.name.setText(appointmentToCheck.getDate().toString()+" "+appointmentToCheck.getTime()+":00");
            holder.name.setChecked(appointmentToCheck.getIscanceled());
            holder.name.setTag(appointmentToCheck);

            return convertView;

        }

    }
}
