package Clases;

import Enums.DisponibilidadEmpleado;
import Enums.Oficios;
import Exceptions.EmpleadoNoDisponibleException;
import Interfaces.Registrable;

import java.time.LocalDate;
import java.util.*;

public class Empleado extends Persona implements Registrable {
    private Direccion direccion;
    private List<String> herramientas;
    private double reputacion;
    private Map<LocalDate, Servicio> contrataciones = new HashMap<>();
    private DisponibilidadEmpleado estado;
    private List<Calificacion> calificaciones;
    private Oficios oficio;
    private List<String> historialAcciones;

    public Empleado(String dni ,String nombre, String apellido, String email, String telefono, Oficios oficio) {
        super(dni, nombre, apellido, email, telefono);
        this.estado = DisponibilidadEmpleado.DISPONIBLE;
        this.herramientas = new ArrayList<>();
        this.contrataciones = new HashMap<>();
        this.reputacion = 0.0;
        this.calificaciones = new ArrayList<>();
        this.oficio = oficio;
        this.historialAcciones = new ArrayList<>();
    }


    public Oficios getOficio() {return oficio;}
    public void setOficio(Oficios oficio) {this.oficio = oficio;}

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public List<String> getHerramientas() { return herramientas; }
    public void setHerramientas(List<String> herramientas) { this.herramientas = herramientas; }

    public DisponibilidadEmpleado getEstado() { return estado; }
    public double getReputacion() { return reputacion; }
    public List<Calificacion> getCalificaciones() { return calificaciones; }



    // utilizaciones del usuario

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

    // utilizaciones del empleado

    //Metodo para actualizar las calificaciones
    private void actualizarReputacion() {
        if (calificaciones.isEmpty()) {
            reputacion = 0.0;
            return;
        }
        int suma = 0;
        for (Calificacion v : calificaciones) {
            suma += v.getPuntaje();
        }
        reputacion = (double) suma / calificaciones.size();
    }


    public void agregarValoracion(Cliente cliente, int puntaje, String comentario) {
        boolean contratoPrevio = false;

        //Recorremos todas las contrataciones para verificar si el cliente lo contrató
        for (Servicio servicio : contrataciones.values()) {
            if (servicio.getCliente().equals(cliente)) {
                contratoPrevio = true;
                break;
            }
        }

        //Si no contrato no puede calificar
        if (!contratoPrevio) {
            System.out.println("El cliente " + cliente.getNombre() + " no ha contratado a este empleado.");
            return;
        }

        if (puntaje < 1 || puntaje > 5) {
            System.out.println("La calificación debe ser entre 1 y 5.");
            return;
        }

        //agrego la calificacion y promedio las calificaciones
        calificaciones.add(new Calificacion(cliente, this, puntaje, comentario));
        actualizarReputacion();
        System.out.println("Valoración registrada correctamente.");
    }

    //Historial de acciones del empleado(Contrataciones, calificaciones)
    @Override
    public void registrarAccion(String descripcion) {
        historialAcciones.add(descripcion);
    }

    public void mostrarHistorial() {
        System.out.println("Historial de "+getNombre()+ ":");
        for (String accion : historialAcciones) {
            System.out.println(" - "+accion);
        }
    }


    @Override
    public String toString() {
        return "Empleado{" +
                "direccion=" + direccion +
                ", herramientas=" + herramientas +
                ", reputacion=" + reputacion +
                ", contrataciones=" + contrataciones +
                ", estado=" + estado +
                ", calificaciones=" + calificaciones +
                ", oficio=" + oficio +
                "} " + super.toString();
    }
}
