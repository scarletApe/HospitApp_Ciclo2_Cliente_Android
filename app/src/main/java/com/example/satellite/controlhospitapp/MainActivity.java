package com.example.satellite.controlhospitapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import conector.ConectorServicio;
import entities.Global;
import entities.Patient;
import multixsoft.hospitApp.Manager.SessionManager;
import utilities.Validator;


public class MainActivity extends ActionBarActivity {
    EditText editTextNss;
    EditText editTextPassword;
    Button btnAcessPatient;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Button btnLinkLoginRegister;
    private Pattern pattern;
    private Matcher matcher;
    private Validator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        editTextNss = (EditText)findViewById(R.id.nss);
        editTextPassword = (EditText) findViewById(R.id.password);
        btnAcessPatient = (Button) findViewById(R.id.btnLogin);
        btnLinkLoginRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session= new SessionManager(getApplicationContext());

        validator = new Validator();

        btnAcessPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nss = editTextNss.getText().toString();
                String password = editTextPassword.getText().toString();

                boolean isValidNss = validator.isValidNss(nss);
                boolean isValidPassword = validator.isValidPassword(password);

                Log.e("isValidNss", isValidNss+"");
                Log.e("isValidPassword",isValidPassword+"");

                boolean isValidFields = true;

                if(isValidNss == false){
                    Toast.makeText(getApplicationContext(),
                            "Asegurese de haber ingresado únicamente 11 digitos " +
                                    "\n y un guión como separador", Toast.LENGTH_LONG).show();
                    isValidFields = false;
                }
                if(isValidPassword == false){
                    Toast.makeText(getApplicationContext(),
                            "Asegurese de haber ingresado su contraseña que consiste " +
                                    "\n de 8 caracteres, al menos un digito y una letra en mayúscula",
                            Toast.LENGTH_LONG).show();
                    isValidFields = false;
                }

                // Check for empty data in the form
                if(isValidFields == true) {
                    // Get patient from database
                    SendfeedbackJob job = new SendfeedbackJob(MainActivity.this);
                    job.execute(new String[]{nss, password});

                }
            }
        });

        btnLinkLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Entrando", "onClick de btnLink");
                Intent intent = new Intent(getApplicationContext(),PacientesActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public void openRegistro(View view) {
        Intent i = new Intent(this,PacientesActivity.class );
        startActivity(i);

    }
    public void openMenu(View view){
        Intent i = new Intent(this,MenuActivity.class );
        startActivity(i);
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class SendfeedbackJob extends AsyncTask<String, Void, Boolean> {
        private Activity activity;

        public SendfeedbackJob(Activity activity){
            this.activity = activity;
        }


        @Override
        protected Boolean doInBackground(String params[]) {
            String nss = params[0];
            String password = params[1];

            ConectorServicio conectorServicio = ConectorServicio.getInstance();
            Patient patient = conectorServicio.obtenerPatient(nss);

            if(patient !=null && patient.getPassword().equals(password)){
                Global.patient=patient;
                session.setLogin(true);
            }else{
                Global.patient=null;
            }
            return (Global.patient != null);
        }

        @Override
        protected void onPostExecute(Boolean message) {
            if(message){
                activity.startActivity(new Intent(activity, MenuActivity.class));
            }else{
                Toast.makeText(getApplicationContext(), "¿Se ha registrado anteriormente? \n " +
                                "No existe un paciente con los datos que ha especificado",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}
