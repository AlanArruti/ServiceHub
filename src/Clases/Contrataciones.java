package Clases;

import Enums.Oficios;
import Interfaces.Identificable;

import java.time.LocalDate;

public class Contrataciones implements Identificable {

    private static int contadorServicios = 1;
    private final String idServicio;
    private String descripcion;
    private LocalDate fecha;
    private Oficios oficio;
    private Cliente cliente;

    public Contrataciones(String descripcion, Oficios oficio, Cliente cliente) {
        this.idServicio = generarId();
        this.descripcion = descripcion;
        this.oficio = oficio;
        this.cliente = cliente;
        this.fecha = LocalDate.now();
    }

    // Getters
    public String getIdServicio() { return idServicio; }

    public String getDescripcion() { return descripcion; }

    public Oficios getOficio() {return oficio;}

    public void setOficio(Oficios oficio) {this.oficio = oficio;}

    public LocalDate getFecha() { return fecha; }

    public Cliente getCliente() { return cliente; }

    //id autogenerado con srv antes del numero para saber que voy a buscar un servicio
    private String generarId() {
        return "SRV" + contadorServicios++;
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
        return "Servicio{" +
                "ID='" + idServicio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ",  fecha=" + fecha +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin asignar") +
                '}';
    }
}

