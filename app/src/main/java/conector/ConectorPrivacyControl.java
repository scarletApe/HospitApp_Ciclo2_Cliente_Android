package conector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by maritza on 26/05/15.
 */
public class ConectorPrivacyControl {

    /* Se declara aquí el URL_BASE para probar este conector, posteriormente
    hará uso del mismo atributo static de ConectorService */
    public static final String URL_BASE =ConectorServicio.URL_BASE;

    private static final ConectorPrivacyControl INSTANCE = new ConectorPrivacyControl();

    public static ConectorPrivacyControl getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @param username
     * @param password
     * @return Returns a 1 if successful as an Admin Returns a 2 if successful
     * as a Doctor -1 if it fails
     */
    public int accessAsAdminOrDoctor(String username, String password) {
        int resultado = -1;

        try {
            URL url;
            HttpURLConnection conexion;
            String urlBusqueda = URL_BASE + "privacycontrol/accessasadmindoctor?"
                    + "username=" + URLEncoder.encode(username, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");
            url = new URL(urlBusqueda);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestProperty("Accept", "text/plain");

            int codigo = conexion.getResponseCode();
            if (codigo == HttpURLConnection.HTTP_OK) {
                InputStream is = conexion.getInputStream();
                BufferedReader entrada = new BufferedReader(new InputStreamReader(is));
                String respuesta = entrada.readLine();

                resultado = Integer.parseInt(respuesta);
                entrada.close();
            }
        } catch (MalformedURLException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        }
        return resultado;
    }
}
