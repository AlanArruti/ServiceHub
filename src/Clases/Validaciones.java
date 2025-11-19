package Clases;

import java.text.Normalizer;

public class Validaciones {

    // Validaciones para q no se rompa mas

    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        String t = dni.trim();
        return !t.isEmpty() && t.matches("\\d+");
    }

    public static boolean esNombreApellidoValido(String texto) {
        if (texto == null) return false;
        String t = texto.trim();
        // Solo letras unicode y espacios
        return !t.isEmpty() && t.matches("[\\p{L} ]+");
    }

    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.isEmpty()) return false;
        int first = e.indexOf('@');
        int last = e.lastIndexOf('@');
        if (first <= 0 || first != last || first == e.length() - 1) return false;
        return true;
    }

    public static boolean esNombreOficioValido(String nombre) {
        if (nombre == null) return false;
        String t = nombre.trim();
        return !t.isEmpty() && t.matches("[\\p{L} ]+");
    }

    public static String normalizarDescripcion(String descripcion) {
        if (descripcion == null) return null;
        String t = descripcion.trim();
        // Eliminar diacriticos (acentos) y normalizar
        t = Normalizer.normalize(t, Normalizer.Form.NFD);
        t = t.replaceAll("\\p{M}+", "");
        // Permitir letras, digitos, espacio, '?', coma, guion, punto, dos puntos, punto y coma, parentesis y '/'
        t = t.replaceAll("[^A-Za-z0-9 ?.,:;()/\\-]", "");
        // Compactar espacios
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }

    public static boolean esDescripcionValida(String descripcion) {
        String n = normalizarDescripcion(descripcion);
        return n != null && !n.isEmpty();
    }
}
