package Clases;

import Interfaces.Identificable;

import java.time.LocalDate;

public class Contrataciones implements Identificable {

    private static int contadorServicios = 1;
    private final String idServicio;
    private String descripcion;
    private LocalDate fecha;
    private Oficio oficio;
    private Cliente cliente;
    private Empleado empleado;

    public Contrataciones(String descripcion, Oficio oficio, Cliente cliente, LocalDate fecha) {
        this.idServicio = generarId();
        this.descripcion = descripcion;
        this.oficio = oficio;
        this.cliente = cliente;
        this.fecha = fecha;
    }

    // Getters y Setters
    public String getIdServicio() { return idServicio; }

    public String getDescripcion() { return descripcion; }

    public Oficio getOficio() { return oficio; }

    public LocalDate getFecha() { return fecha; }

    public Cliente getCliente() { return cliente; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public static int getContadorServicios() { return contadorServicios; }

    private static void setContadorServicios(int contadorServicios) { Contrataciones.contadorServicios = contadorServicios; }

    public Empleado getEmpleado() { return empleado; }

    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }


    private String generarId() {
        return "SRV" + contadorServicios++;
    }


    // Implementación de Identificable
    public String getIdentificador() { return idServicio; }

    // Hashcode/equals para el funcionamiento del contains
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contrataciones)) return false;
        Contrataciones s = (Contrataciones) o;
        return idServicio.equals(s.idServicio);
    }

    @Override
    public int hashCode() { return idServicio.hashCode(); }

    @Override
    public String toString() {
        return "Contrataciones{" +
                "idServicio='" + idServicio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", oficio=" + oficio +
                ", cliente=" + cliente +
                '}';
    }
}

