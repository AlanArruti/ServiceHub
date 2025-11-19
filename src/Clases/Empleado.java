package Clases;

import Enums.DisponibilidadEmpleado;
import Exceptions.EmpleadoNoDisponibleException;
import Interfaces.Registrable;

import java.time.LocalDate;
import java.util.*;

public class Empleado extends Persona implements Registrable {
    private Direccion direccion;
    private double reputacion;
    private Map<LocalDate, Contrataciones> contrataciones;
    private DisponibilidadEmpleado estado;
    private List<Calificacion> calificaciones;
    private List<Oficio> oficios;
    private List<String> historialAcciones;

    public Empleado(String dni, String nombre, String apellido, String email, String telefono, String password, Oficio oficio) {
        super(dni, nombre, apellido, email, telefono, password);
        this.oficios = new ArrayList<>();
        if (oficio != null) this.oficios.add(oficio);
        this.estado = DisponibilidadEmpleado.DISPONIBLE;
        this.contrataciones = new HashMap<>();
        this.reputacion = 0.0;
        this.calificaciones = new ArrayList<>();
        this.historialAcciones = new ArrayList<>();
    }

    // Getters y Setters

    public Direccion getDireccion() {return direccion;}

    public void setDireccion(Direccion direccion) {this.direccion = direccion;}

    public double getReputacion() {return reputacion;}

    public Map<LocalDate, Contrataciones> getContrataciones() {return contrataciones;}

    public DisponibilidadEmpleado getEstado() {return estado;}

    public List<Calificacion> getCalificaciones() {return calificaciones;}

    public List<Oficio> getOficios() { return new ArrayList<>(oficios); }

    public boolean tieneOficio(Oficio oficio) {
        if (oficio == null) return false;
        for (Oficio o : oficios) {
            if (o != null && o.equals(oficio)) return true;
        }
        return false;
    }

    public void agregarOficio(Oficio oficio) {
        if (oficio == null) return;
        if (!tieneOficio(oficio)) oficios.add(oficio);
    }

    public boolean quitarOficio(Oficio oficio) {
        if (oficio == null) return false;
        return oficios.removeIf(o -> o != null && o.equals(oficio));
    }


    // utilizaciones del usuario

    //Metodo para verificar disponibilidad
    public boolean estaDisponible(LocalDate fechaDeseada) {
        return !contrataciones.containsKey(fechaDeseada);
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
        consolidarCalificaciones();
        if (calificaciones.isEmpty()) {
            reputacion = 0.0;
            return;
        }
        double suma = 0.0;
        for (Calificacion v : calificaciones) {
            suma += v.getPuntaje();
        }
        reputacion = suma / calificaciones.size();
    }

    // Mantiene solo la última calificación por (cliente, idServicio)
    private void consolidarCalificaciones() {
        if (calificaciones == null || calificaciones.isEmpty()) return;

        java.util.Map<String, Calificacion> ultimas = new java.util.LinkedHashMap<>();

        for (Calificacion c : calificaciones) {
            if (c == null || c.getCliente() == null) continue;
            String dni = c.getCliente().getDni();
            String id = c.getIdServicio();

            if (id == null || id.trim().isEmpty()) {
                continue;
            }

            String key = (dni == null ? "" : dni.trim()) + "|" + id.trim();
            Calificacion prev = ultimas.get(key);
            if (prev == null) {
                ultimas.put(key, c);
            } else {
                java.time.LocalDate fPrev = prev.getFecha();
                java.time.LocalDate fAct = c.getFecha();
                boolean reemplazar = (fPrev == null && fAct != null) || (fPrev != null && fAct != null && fAct.isAfter(fPrev)) || (fPrev == null && fAct == null);
                if (reemplazar) {
                    ultimas.put(key, c);
                }
            }
        }
        calificaciones.clear();
        calificaciones.addAll(ultimas.values());
    }

    // metodo para que el cliente puede valorar al empleado
    public void agregarValoracion(Cliente cliente, double puntaje, String comentario, String idServicio) {
        boolean contratoPrevio = false;
        for (Contrataciones c : this.contrataciones.values()) {
            if (c.getCliente() != null && c.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
                contratoPrevio = true;
                break;
            }
        }
        if (!contratoPrevio) {
            System.out.println("El cliente " + cliente.getNombre() + " no ha contratado a este empleado.");
            return;
        }
        if (puntaje < 1.0 || puntaje > 5.0) {
            System.out.println("La calificacion debe ser entre 1 y 5.");
            return;
        }
        if (idServicio != null && !idServicio.trim().isEmpty()) {
            java.util.Iterator<Calificacion> it = calificaciones.iterator();
            while (it.hasNext()) {
                Calificacion prev = it.next();
                if (prev.getCliente() != null && prev.getCliente().getDni().equalsIgnoreCase(cliente.getDni()) &&
                        idServicio.equalsIgnoreCase(prev.getIdServicio())) {
                    it.remove();
                }
            }
        }
        Calificacion cal = new Calificacion(cliente, this, puntaje, comentario);
        cal.setIdServicio(idServicio);
        cal.setFecha(LocalDate.now());
        calificaciones.add(cal);
        actualizarReputacion();
        System.out.println("Valoracion registrada correctamente.");
    }
    // metodo para que el usuario pueda contratar a un empleado en una fecha
    public void contratarServicio(Contrataciones servicio, LocalDate fechaDeseada) throws EmpleadoNoDisponibleException {

        if (!estaDisponible(fechaDeseada)) {
            throw new EmpleadoNoDisponibleException("El empleado " + getNombre() + " ya tiene un servicio asignado el " + fechaDeseada);
        }

        contrataciones.put(fechaDeseada, servicio);
        estado = DisponibilidadEmpleado.OCUPADO;

        registrarAccion("Contratacion " + servicio.getIdServicio() + ": '" + servicio.getDescripcion() + "' - Fecha " + fechaDeseada);
    }

    public void cargarContratacionGuardada(Contrataciones servicio, LocalDate fecha) {
        contrataciones.put(fecha, servicio);
        actualizarDisponibilidad();
    }

    // Rechazar un servicio previamente asignado al empleado
    public void rechazarServicio(Contrataciones servicio) {
        if (servicio == null) return;
        LocalDate encontrada = null;

        // recorro todas las filas del mapa
        for (java.util.Map.Entry<LocalDate, Contrataciones> e : contrataciones.entrySet()) {
            if (e.getValue().equals(servicio)) {
                encontrada = e.getKey();
                break;
            }
        }
        if (encontrada != null) {
            contrataciones.remove(encontrada);
            actualizarDisponibilidad();
            registrarAccion("Rechazo contratacion " + servicio.getIdServicio() + ": '" + servicio.getDescripcion() + "' - Fecha " + encontrada);
        }
    }

    // sirve para las contrataciones
    public void registrarAccion(String descripcion) {
        historialAcciones.add(LocalDate.now() + ": " + descripcion);
    }

    // Agrega una calificacion cargada desde persistencia y recalcula la reputacion
    public void agregarCalificacionGuardada(Cliente cliente, double puntaje, String comentario, LocalDate fecha, String idServicio) {
        calificaciones.add(new Calificacion(cliente, this, puntaje, comentario, fecha, idServicio));
        actualizarReputacion();
    }

    public void mostrarHistorial() {
        System.out.println("Historial de acciones de: " + getNombre());
        for (String accion : historialAcciones) {
            System.out.println(" - " + accion);
        }
    }


    @Override
    public String toString() {
        return "Empleado{" +
                "direccion=" + direccion +
                ", reputacion=" + String.format("%.2f", reputacion) +
                ", contrataciones=" + contrataciones +
                ", estado=" + estado +
                ", calificaciones=" + calificaciones +
                ", oficios=" + oficios +
                ", historialAcciones=" + historialAcciones +
                '}';
    }
}