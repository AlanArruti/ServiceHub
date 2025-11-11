package Clases;

import Interfaces.Identificable;

public abstract class Persona implements Identificable {
    private int id;
    private static int contador = 1;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    public Persona(String dni ,String nombre, String apellido, String email, String telefono) {
        this.id = contador++;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
    }



    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIdentificador() {
        return String.valueOf(id); // Devuelve el ID numérico convertido a String
    }

    


}
