package Clases;

import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorOficios {
    private Repositorio<Empleado> empleados;
    private List<Cliente> clientes;
    private List<Servicio> servicios;


    public GestorOficios() {
        empleados = new Repositorio<>();
        clientes = new ArrayList<>();
        servicios = new ArrayList<>();
    }

    public void registrarEmpleado(Empleado e) {
        empleados.agregar(e);
    }

    public void registrarCliente(Cliente c) {
        clientes.add(c);
    }


    // muestra los empleados por oficio segun el que pida el usuario
    public void mostrarEmpleadoXcategoria(String categoria) {
        for (Empleado e : empleados.listar()) {
            if (e.getOficio().equals(categoria)) {
                System.out.println(e.toString());
            }
        }
    }

    // ver la disponibilidad del empleado en la fecha que pide el usuario

    public boolean verSiEstaDisponible(String dni, LocalDate fecha){

        for (Empleado e : empleados.listar()) {
            if (e.getDni().equals(dni)) {
                return e.estaDisponible(fecha); // la funcion devuelve true
            }
        }
        return false;
    }

    public void mostrarEmpleados() {
        for (Empleado e : empleados.listar()) {
            System.out.println(e);
        }
    }

    public void mostrarClientes() {
        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }
}
