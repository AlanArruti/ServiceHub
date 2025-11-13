package Clases;

public class Direccion {
    private String ciudad;
    private String calle;
    private int numero;

    public Direccion(String ciudad, String calle, int numero) {
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        return "Direccion{" +
                "ciudad='" + ciudad + '\'' +
                ", calle='" + calle + '\'' +
                ", numero=" + numero +
                '}';
    }
}
