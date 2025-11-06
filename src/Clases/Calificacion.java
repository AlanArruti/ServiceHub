package Clases;

import java.time.LocalDate;

public class Calificacion {
    private Cliente cliente;
    private Empleado empleado;
    private int puntaje; // 1–5
    private String comentario;
    private LocalDate fecha;

    public Calificacion(Cliente cliente, Empleado empleado, int puntaje, String comentario) {
        this.cliente = cliente;
        this.empleado = empleado;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.fecha = LocalDate.now();
    }

    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public int getPuntaje() { return puntaje; }
    public String getComentario() { return comentario; }
    public LocalDate getFecha() { return fecha; }

    @Override
    public String toString() {
        return cliente.getNombre() + " calificó a " + empleado.getNombre() +
                " con " + puntaje + "/5 - " + comentario;
    }
}
