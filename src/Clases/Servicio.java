package Clases;

import java.time.LocalDate;

public class Servicio {

    private String idServicio;
    private String descripcion;
    private double precio;
    private LocalDate fecha;
    private String categoria;
    private Cliente cliente;

    public Servicio(String idServicio, String descripcion, double precio, String categoria, Cliente cliente) {
        this.idServicio = idServicio;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.cliente = cliente;
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public String getIdServicio() { return idServicio; }
    public void setIdServicio(String idServicio) { this.idServicio = idServicio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        return "Servicio{" +
                "ID='" + idServicio + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio + '\'' +
                ",  fecha=" + fecha + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "Sin asignar") +
                '}';
    }
}

