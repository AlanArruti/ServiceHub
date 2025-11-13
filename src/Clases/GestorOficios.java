package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorOficios {
    private Repositorio<Empleado> empleados;
    private List<Cliente> clientes;
    private List<Contrataciones> contrataciones;


    public GestorOficios() {
        empleados = new Repositorio<>();
        clientes = new ArrayList<>();
        contrataciones = new ArrayList<>();
    }

    public List<Cliente> getClientes() {
        return clientes;
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
            if (e.getOficio().name().equalsIgnoreCase(categoria.trim())) {
                System.out.println(e);
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

    public boolean buscarEmpleado(String dni) throws PersonaNoEncontradaException {
        for (Empleado e : empleados.listar()) {
            if (e.getDni().equals(dni)) {
                return true;
            }
        }
        throw new PersonaNoEncontradaException("El DNI no se encuentra registrado como empleado.");
    }

    public Cliente buscarClientePorDni(String dni) throws PersonaNoEncontradaException {
        for (Cliente c : clientes) {
            if (c.getDni().equalsIgnoreCase(dni)) {
                return c;
            }
        }
        throw new PersonaNoEncontradaException("No se encontró cliente con DNI: " + dni);
    }

    // hace falta que se le mande el cliente porque contrataciones tiene como variable el cliente
    public void contratarEmpleado(String dniEmpleado, Contrataciones servicio, LocalDate fecha)
            throws PersonaNoEncontradaException, EmpleadoNoDisponibleException {

        Empleado empleadoEncontrado = null;

        // Buscar empleado por DNI
        for (Empleado e : empleados.listar()) {
            if (e.getDni().equalsIgnoreCase(dniEmpleado)) {
                empleadoEncontrado = e;
                break;
            }
        }

        if (empleadoEncontrado == null) {
            throw new PersonaNoEncontradaException("No se encontró empleado con DNI: " + dniEmpleado);
        }

        // Intentar asignar servicio
        empleadoEncontrado.contratarServicio(servicio, fecha);

        // Registrar acción interna
        empleadoEncontrado.registrarAccion("Contratado para: " + servicio.getDescripcion() + " (" + servicio.getIdServicio() + ") en fecha " + fecha);

        // Guardar contratación global
        contrataciones.add(servicio);

        System.out.println("Contratación registrada con éxito para el empleado: " + empleadoEncontrado.getNombre());
    }



}
