package Clases;

import java.time.LocalDate;

public class Calificacion {
    private Cliente cliente;
    private Empleado empleado;
    private double puntaje; // 1-5
    private String comentario;
    private LocalDate fecha;
    private String idServicio;

    public Calificacion(Cliente cliente, Empleado empleado, double puntaje, String comentario) {
        this(cliente, empleado, puntaje, comentario, LocalDate.now(), null);
    }

    public Calificacion(Cliente cliente, Empleado empleado, double puntaje, String comentario, LocalDate fecha) {
        this(cliente, empleado, puntaje, comentario, (fecha == null ? LocalDate.now() : fecha), null);
    }

    public Calificacion(Cliente cliente, Empleado empleado, double puntaje, String comentario, LocalDate fecha, String idServicio) {
        this.cliente = cliente;
        this.empleado = empleado;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.fecha = (fecha == null) ? LocalDate.now() : fecha;
        this.idServicio = idServicio;
    }

    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public double getPuntaje() { return puntaje; }
    public String getComentario() { return comentario; }
    public LocalDate getFecha() { return fecha; }
    public String getIdServicio() { return idServicio; }

    public void setPuntaje(double p) { this.puntaje = p; }
    public void setComentario(String c) { this.comentario = c; }
    public void setFecha(LocalDate f) { this.fecha = f; }
    public void setIdServicio(String id) { this.idServicio = id; }

    @Override
    public String toString() {
        return cliente.getNombre() + " califico a " + empleado.getNombre() +
                " con " + String.format("%.2f", puntaje) + "/5 - " + comentario;
    }
}