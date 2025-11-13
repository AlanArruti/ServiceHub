package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.OficioNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorOficios {
    private Repositorio<Empleado> empleados;
    private List<Cliente> clientes;
    private List<Contrataciones> contrataciones;
    private Repositorio<Oficio> oficios;

    // Nuevos nombres de archivo (sin carpeta)
    private String archivoOficios = "oficios.json";
    private String archivoClientes = "clientes.json";
    private String archivoEmpleados = "empleados.json";
    private String archivoContrataciones = "contrataciones.json";

    public GestorOficios() {
        empleados = new Repositorio<>();
        clientes = new ArrayList<>();
        contrataciones = new ArrayList<>();
        oficios = new Repositorio<>();
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


    public Cliente buscarClienteEnLista(String dni) throws PersonaNoEncontradaException{
        for (Cliente cliente : clientes) {
            if (cliente.getDni().equalsIgnoreCase(dni)) return cliente;
        }
        throw new PersonaNoEncontradaException("El DNI no se encuentra registrado como cliente.");
    }

    public Empleado buscarEmpleadoEnLista(String dni) throws PersonaNoEncontradaException {
        for (Empleado empleado : empleados.listar()) {
            if (empleado.getDni().equalsIgnoreCase(dni)) return empleado;
        }
        throw new PersonaNoEncontradaException("El DNI no se encuentra registrado como empleado.");
    }

    public Oficio buscarOficioPorNombre(String nombre) throws OficioNoDisponibleException {
        for (Oficio oficio : oficios.listar()) {
            if (oficio.coincideConNombre(nombre)) return oficio;
        }
        throw new OficioNoDisponibleException("El oficio que busca no se encuentra disponible.");
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

    // muestra los empleados por oficio segun el que pida el usuario
    // no anda mas hay q acomodarlo
    public void mostrarEmpleadoXcategoria(String categoria) {
        for (Empleado e : empleados.listar()) {
            if (e.getOficio().name().equalsIgnoreCase(categoria.trim())) {
                System.out.println(e);
            }
        }
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
