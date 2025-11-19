package ManejoJSON;

import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class JSONUtiles {

    public static void cargarJSON(JSONArray array, String nombreArchivo){
        try {
            File archivo = new File(nombreArchivo);
            File carpeta = archivo.getParentFile();
            if (carpeta != null && !carpeta.exists()) {
                carpeta.mkdirs();
            }
            try (OutputStream os = new FileOutputStream(nombreArchivo);
                 OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                writer.write(array.toString(4));
                writer.flush();
            }
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
            JSONTokener tokener = new JSONTokener(new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8));
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
            tokener = new JSONTokener(new InputStreamReader(new FileInputStream(nombreArchivo), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tokener;
    }
}
