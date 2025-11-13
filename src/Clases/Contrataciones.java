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

    public Contrataciones(String descripcion, Oficio oficio, Cliente cliente, Empleado empleado) {
        this.idServicio = generarId();
        this.descripcion = descripcion;
        this.oficio = oficio;
        this.cliente = cliente;
        this.fecha = fecha;
        this.empleado = empleado;
    }

    // Getters
    public String getIdServicio() { return idServicio; }

    public String getDescripcion() { return descripcion; }

    public Oficio getOficio() {return oficio;}

    public void setOficio(Oficio oficio) {this.oficio = oficio;}

    public LocalDate getFecha() { return fecha; }

    public Cliente getCliente() { return cliente; }

    public void setCliente(Cliente cliente) {this.cliente = cliente;}

    public void setFecha(LocalDate fecha) {this.fecha = fecha;}

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public static int getContadorServicios() {return contadorServicios;}

    public static void setContadorServicios(int contadorServicios) {Contrataciones.contadorServicios = contadorServicios;}

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    //id autogenerado con srv antes del numero para saber que voy a buscar un servicio
    private String generarId() {
        return "SRV" + contadorServicios++;
    }

    private void actualizarContador(String idExistente) {
        if (idExistente != null && idExistente.startsWith("SRV")) {
            try {
                int numero = Integer.parseInt(idExistente.substring(3));
                if (numero >= contadorServicios) {
                    contadorServicios = numero + 1;
                }
            } catch (NumberFormatException e) {
                // dejo el contador como esta
            }
        }
    }

    // implementacion de Identificable
    public String getIdentificador() {
        return idServicio;
    }

    // Hashcode/equals para el funcionamiento del contains
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contrataciones)) return false;
        Contrataciones s = (Contrataciones) o;
        return idServicio.equals(s.idServicio);
    }

    @Override
    public int hashCode() {
        return idServicio.hashCode();
    }

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

