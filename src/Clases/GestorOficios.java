package Clases;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorOficios {
    private List<Empleado> empleados;
    private List<Cliente> clientes;
    private List<Servicio> servicios;

    public GestorOficios() {
        empleados = new ArrayList<>();
        clientes = new ArrayList<>();
        servicios = new ArrayList<>();
    }

    public void registrarEmpleado(Empleado e) {
        empleados.add(e);
    }

    public void registrarCliente(Cliente c) {
        clientes.add(c);
    }

    public List<Empleado> BuscarPorOficio(String categoria) {
        List<Empleado> resultado = new ArrayList<>();

        for (Empleado e : empleados) {
            if (e.estaDisponible(LocalDate.now()) && e.toString().contains(categoria)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public void mostrarEmpleados() {
        for (Empleado e : empleados) {
            System.out.println(e);
        }
    }

    public void mostrarClientes() {
        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }
}
