package ManejoJSON;

import org.json.*;
import java.io.*;

public class JSONUtiles {

    public static void cargarJSON(JSONArray array, String nombreArchivo){
        try {
            FileWriter archi = new FileWriter(nombreArchivo);
            archi.write(array.toString(4));
            archi.flush();
            archi.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONTokener leerJSON(String nombreArchivo) {
        JSONTokener tokener = null;
        try {
            tokener = new JSONTokener(new FileReader(nombreArchivo));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tokener;
    }
}
