package Clases;

import Exceptions.IdInvalidoException;
import Interfaces.Identificable;

public class Oficio implements Identificable {
    private static int contador = 1;
    private final String id;
    private String nombre;

    public Oficio(String nombre) {
        this.id = generarId();
        this.nombre = nombre;
    }

    public Oficio(String idExistente, String nombre) {
        this.id = idExistente;
        this.nombre = nombre;
        actualizarContador(idExistente);
    }

    private String generarId() {
        return "OFI" + contador++;
    }

    private void actualizarContador(String idExistente) throws IdInvalidoException {
        if (idExistente == null || !idExistente.startsWith("OFI")) {
            return;
        }
        String numeroStr = idExistente.substring(3);
        try {
            int numero = Integer.parseInt(numeroStr);
            if (numero >= contador) {
                contador = numero + 1;
            }
        } catch (NumberFormatException e) {
            throw new IdInvalidoException("El ID existente no tiene un formato numÃ©rico vÃ¡lido.");
        }
    }

    public String getId() { return id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String getIdentificador() { return id; }

    public boolean coincideConNombre(String nombreBuscado) {
        if (nombreBuscado == null) return false;
        return nombre.equalsIgnoreCase(nombreBuscado.trim());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Oficio)) return false;
        Oficio otro = (Oficio) obj;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return nombre + " (" + id + ")"; }
}