package com.example.satellite.controlhospitapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import conector.ConectorPatientManager;
import entities.Global;
import entities.Patient;
import utilities.Validator;

/**
 * Created by manuelmartinez on 6/23/15.
 */
public class ActualizarActivity extends Activity {

    //private EditText inputNss;
    private EditText inputName;
    private EditText inputLastName;
    private EditText inputAddress;
    private EditText inputPassword;

    //private Button btnAceptar;
    //private Button btnLinkToLogin;
    Validator validator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.actualizar_datos);
    }

    public void actualizar(View view){
        validator = new Validator();
        //inputNss = (EditText) findViewById(R.id.nss);
        inputName = (EditText) findViewById(R.id.update_nombre);
        inputLastName = (EditText) findViewById(R.id.update_apellido);
        inputAddress = (EditText) findViewById(R.id.update_direccion);
        inputPassword = (EditText) findViewById(R.id.update_password);

        //btnAceptar = (Button) findViewById(R.id.btnRegister);
        //btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        //String nss = inputNss.getText().toString();
        String firstName = inputName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String direccion = inputAddress.getText().toString();
        String contrasena = inputPassword.getText().toString();

        // realizando las validaciones de los datos ingresados en el registro
        //boolean isValidNss = validator.isValidNss(nss);
        boolean isValidPassword = validator.isValidPassword(contrasena);
        boolean isFirstNameValid = validator.isValidFirstName(firstName);
        boolean isLastNameValid = validator.isValidLastName(lastName);
        int isEmailValid = validator.isValidEmail(direccion);

        //Log.e("nss", isValidNss + "");

        Log.e("email",isEmailValid+"");
        Log.e("pass",isValidPassword+"");
        Log.e("firstName",isFirstNameValid+"");
        Log.e("lastAppointment",isLastNameValid+"");

        // Check for empty data in the form


        //Log.e("nss",isValidNss+"");
        Log.e("email",isEmailValid+"");
        Log.e("pass",isValidPassword+"");
        Log.e("firstName",isFirstNameValid+"");

        boolean isValidFields = true;

        // Check for valid data in the form

        /*
        if(isValidNss == false){
            Toast.makeText(getApplicationContext(),
                    "Asegurese de haber ingresado únicamente 11 digitos " +
                            "\n y opcionalmente un guión como separador", Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        */

        if(isEmailValid == -1){
            Toast.makeText(getApplicationContext(),
                    "El correo que ha ingresado no es válido\n Asegurese de haber agregado la @",
                    Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isEmailValid == -2){
            Toast.makeText(getApplicationContext(),
                    "El correo que ha ingresado no es válido\n El correo no puede comenzar con una @",
                    Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isEmailValid == -3){
            Toast.makeText(getApplicationContext(),
                    "El correo que ha ingresado no es válido, el correo no puede finalizar con @ \nasegurese de haber" +
                            " especificado el dominio en su dirección",
                    Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isEmailValid == -5){
            Toast.makeText(getApplicationContext(),
                    "El correo que ha ingresado no es válido, asegúrate que cumple con el siguiente formato micorrero@ejemplo.com",
                    Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isFirstNameValid == false){
            Toast.makeText(getApplicationContext(),"El nombre que ha ingresado no es válido," +
                    " asegurese de no haber ingresado caracteres inválidos", Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isLastNameValid == false){
            Toast.makeText(getApplicationContext(),"El apellido que ha ingresado no es válido," +
                    " asegurese de no haber ingresado caracteres inválidos", Toast.LENGTH_LONG).show();
            isValidFields = false;
        }
        if(isValidPassword == false){
            Toast.makeText(getApplicationContext(),
                    "Asegurese de que la contraseña contenga al menos 8 caracteres incluyendo al menos un número y una mayúscula",
                    Toast.LENGTH_LONG).show();
            isValidFields = false;
        }


        if(isValidFields == true){
            SendfeedbackJob job = new SendfeedbackJob();
            job.execute(Global.patient.getNss(),contrasena, firstName, lastName,
                    direccion, "true");
        }
    }

    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String params[]) {

            ConectorPatientManager conectorPatientManager = ConectorPatientManager.getInstance();

            String nssPatient = params[0];
            String passwordPatient = params[1];
            String firstNamePatient = params[2];
            String lastNamePatient = params[3];
            String addressPatient = params[4];
            String isActivePatient = params[5];


            Patient paciente = new Patient();
            paciente.setNss(nssPatient);
            paciente.setPassword(passwordPatient);
            paciente.setFirstName(firstNamePatient);
            paciente.setLastName(lastNamePatient);
            paciente.setAddress(addressPatient);
            paciente.setIsActive(Boolean.valueOf(isActivePatient));

            //change to update method when ready
            String transactionComplete = conectorPatientManager.saveNewPatient(paciente);
            return transactionComplete;
        }

        @Override
        protected void onPostExecute(String endPatient) {

            if(endPatient != null){
                Toast.makeText(getApplicationContext(),"Paciente fue actualizado con éxito",
                        Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"No ha sido posible la actualización del paciente",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
