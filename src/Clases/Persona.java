package Clases;

import Interfaces.Identificable;

public abstract class Persona implements Identificable {
    private final int id;
    private static int contador = 1;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String password;

    public Persona(String dni ,String nombre, String apellido, String email, String telefono, String password) {
        this.id = contador++;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
    }

    //Getters y setters
    private int getId() { return id; }

    public String getDni() { return dni; }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getPassword() {return password;}

    // Setters con alcance de paquete para uso administrativo
    void setDni(String dni) { this.dni = dni; }
    void setNombre(String nombre) { this.nombre = nombre; }
    void setApellido(String apellido) { this.apellido = apellido; }
    void setEmail(String email) { this.email = email; }
    void setTelefono(String telefono) { this.telefono = telefono; }
    void setPassword(String password) { this.password = password; }


    public boolean validarPassword(String passwordIngresada) {
        if (passwordIngresada == null) {
            return false;
        }
        return password.equals(passwordIngresada);
    }

    public String getIdentificador() {
        return String.valueOf(id); // Devuelve el ID numérico convertido a String
    }

    // Hashcode/equals para el funcionamiento del contains
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return id == persona.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public String toString() {
        return nombre + " " + apellido + " (" + email + ")";
    }
}
