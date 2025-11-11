package Clases;

public class Cliente extends Persona{
    private Direccion direccion;

    public Cliente(String dni, String nombre, String apellido, String email, String telefono,Direccion direccion) {
        super(dni, nombre, apellido, email, telefono);
        this.direccion = direccion;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "direccion=" + direccion +
                "} " + super.toString();
    }
}
