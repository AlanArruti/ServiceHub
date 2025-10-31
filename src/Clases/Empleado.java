package Clases;

import Enums.DisponibilidadEmpleado;

import java.util.List;

public class Empleado extends Persona{
    private Direccion direccion;
    private List<String> herramientas;
    private double reputacion;
    private DisponibilidadEmpleado disponibilidadEmpleado;

    public Empleado(String nombre, String apellido, String email, String telefono, DisponibilidadEmpleado disponibilidadEmpleado) {
        super(nombre, apellido, email, telefono);
        this.disponibilidadEmpleado = disponibilidadEmpleado;
    }
}
