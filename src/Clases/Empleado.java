package Clases;

import Enums.DisponibilidadEmpleado;
import Exceptions.EmpleadoNoDisponibleException;
import java.time.LocalDate;
import java.util.*;


public class Empleado extends Persona{
    private Direccion direccion;
    private List<String> herramientas;
    private double reputacion;
    private Map<LocalDate, Servicio> contrataciones = new HashMap<>();
    private DisponibilidadEmpleado estado = DisponibilidadEmpleado.DISPONIBLE;

    public Empleado(String nombre, String apellido, String email, String telefono) {
        super(nombre, apellido, email, telefono);
        this.estado = DisponibilidadEmpleado.DISPONIBLE;
        this.herramientas = new ArrayList<>();
        this.contrataciones = new HashMap<>();
        this.reputacion = 0.0;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public List<String> getHerramientas() {
        return herramientas;
    }

    public void setHerramientas(List<String> herramientas) {
        this.herramientas = herramientas;
    }

    public double getReputacion() {
        return reputacion;
    }

    public void setReputacion(double reputacion) {
        if (reputacion < 0 || reputacion > 5) throw new IllegalArgumentException("La reputación debe ser entre 0 y 5");
        this.reputacion = reputacion;
    }

    public Map<LocalDate, Servicio> getContrataciones() {
        return contrataciones;
    }

    public void setContrataciones(Map<LocalDate, Servicio> contrataciones) {
        this.contrataciones = contrataciones;
    }

    public DisponibilidadEmpleado getEstado() {
        return estado;
    }

    public void setEstado(DisponibilidadEmpleado estado) {
        this.estado = estado;
    }

    //Metodo para verificar disponibilidad
    public boolean estaDisponible(LocalDate fechaDeseada) {
        return !contrataciones.containsKey(fechaDeseada);
    }

    //Metodo para contratar Servicio
    public void contratarServicio(Servicio servicio, LocalDate fechaDeseada) throws EmpleadoNoDisponibleException {
        if (!estaDisponible(fechaDeseada)) {
            throw new EmpleadoNoDisponibleException(
                    "El empleado " + getNombre() + " ya tiene un servicio asignado el " + fechaDeseada
            );
        }
        contrataciones.put(fechaDeseada, servicio);
        estado = DisponibilidadEmpleado.OCUPADO;
    }

    //Metodo para actualizar disponibilidad
    public void actualizarDisponibilidad() {
        LocalDate hoy = LocalDate.now();
        estado = DisponibilidadEmpleado.DISPONIBLE;

        for (LocalDate fecha : contrataciones.keySet()) {
            if (!fecha.isBefore(hoy)) { // si tiene servicios hoy o a futuro lo pone ocupado
                estado = DisponibilidadEmpleado.OCUPADO;
                break;
            }
        }
    }

    
}
