package ManejoJSON;

import org.json.*;
import java.io.*;

public class JSONUtiles {

    public static void cargarJSON(JSONArray array, String nombreArchivo){
        try {
            File archivo = new File(nombreArchivo);
            File carpeta = archivo.getParentFile();
            if (carpeta != null && !carpeta.exists()) {
                carpeta.mkdirs();
            }
            FileWriter archi = new FileWriter(nombreArchivo);
            archi.write(array.toString(4));
            archi.flush();
            archi.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray leerArreglo(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            return new JSONArray();
        }
        try {
            JSONTokener tokener = new JSONTokener(new FileReader(archivo));
            Object dato = tokener.nextValue();
            if (dato instanceof JSONArray) {
                return (JSONArray) dato;
            } else {
                return new JSONArray();
            }
        } catch (Exception e) {
            return new JSONArray();
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
