package Clases;

import Interfaces.Identificable;

import java.time.LocalDate;

public class Contrataciones implements Identificable {

    private static int contadorServicios = 1;
    private final String idServicio;
    private String descripcion;
    private double precio;
    private LocalDate fecha;
    private String categoria;
    private Cliente cliente;

    public Contrataciones(String descripcion, double precio, String categoria, Cliente cliente) {
        this.idServicio = generarId();
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.cliente = cliente;
        this.fecha = LocalDate.now();
    }

    // Getters
    public String getIdServicio() { return idServicio; }

    public String getDescripcion() { return descripcion; }

    public double getPrecio() { return precio; }

    public String getCategoria() { return categoria; }

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

