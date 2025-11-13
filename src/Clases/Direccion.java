package Clases;

public class Direccion {
<<<<<<< HEAD
    private String direccion;
    
=======
    private String ciudad;
    private String calle;
    private int numero;
>>>>>>> 9c14e5207d8c28f16c9b13ee987d3346fa11bcc9

    public Direccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Direccion{" +
                "direccion='" + direccion + '\'' +
                '}';
    }
}
