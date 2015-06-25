package utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maritza on 3/06/15.
 */
public class Validator {
    Pattern pattern;
    Matcher matcher;

    private final String emailValidation = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final String firstNameValidation = "[a-záéíóúA-zÁÉÍÓÚ]+([ '-][a-záéíóúA-ZÁÉÍÓÚ]+)*";
    private final String lastNameValidation = "[a-záéíóúA-zÁÉÍÓÚ]+([ '-][a-záéíóúA-ZÁÉÍÓÚ]+)*";
    private final String passwordValidation ="(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    private final String nssValidation ="^[0-9][\\-\\d]*(\\d+)$";

    public int isValidEmail(String email){
        if(email.contains("@") == false){
            return -1;
        }
        if(email.startsWith("@")){
            return -2;
        }
        if(email.endsWith("@")){
            return -3;
        }
        if(email.matches(emailValidation)){
            return 1;
        }
        return -5;
    }

    public boolean isValidFirstName(String firstName){
        return firstName.matches(firstNameValidation)&& firstName.length()>2 && firstName.length()<100;
    }

    public boolean isValidLastName(String lastName){
        return lastName.matches(lastNameValidation) && lastName.length()>2 && lastName.length()<100;
    }

	/* Se busca una contraseña que incluya de 6 a 20 caracteres,
	 * una letra en mayúscula y una en minúscula,
	 * y un número para asegurarse de que la contraseña sea segura*/

    public boolean isValidPassword(String password){
        return password.matches(passwordValidation);
    }

    public boolean isValidNss(String nss){
        return nss.matches(nssValidation) && splittingNumbers(nss) == 11 && (splittingCharacters(nss) <= 1);
    }

    private static int splittingNumbers(String nss){
        int numberDigits = 0;

        for (int i = 0; i < nss.length(); i++)
        {
            char c = nss.charAt(i);
            if (Character.isDigit(c))
            {
                numberDigits++;
            }
        }
        return numberDigits;
    }

    private static int splittingCharacters(String nss){
        int numberS = 0;

        for (int i = 0; i < nss.length(); i++)
        {
            char c = nss.charAt(i);
            if (c == '-')
            {
                numberS++;
            }
        }
        return numberS;
    }




}
