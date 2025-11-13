package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.OficioNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorOficios {
    private Repositorio<Empleado> empleados;
    private Repositorio<Cliente> clientes;
    private Repositorio<Contrataciones> contrataciones;
    private Repositorio<Oficio> oficios;

    // Nuevos nombres de archivo (sin carpeta)
    private static final String CARPETA_DATOS = "datos";
    private static final String ARCHIVO_OFICIOS = CARPETA_DATOS + "oficios.json";
    private static final String ARCHIVO_CLIENTES = CARPETA_DATOS + "clientes.json";
    private static final String ARCHIVO_EMPLEADOS = CARPETA_DATOS + "empleados.json";
    private static final String ARCHIVO_CONTRATACIONES = CARPETA_DATOS + "contrataciones.json";

    public GestorOficios() {
        empleados = new Repositorio<>();
        clientes = new Repositorio<>();
        contrataciones = new Repositorio<>();
        oficios = new Repositorio<>();
    }


    public Repositorio<Cliente> getClientes() {
        return clientes;
    }

    public Repositorio<Empleado> getEmpleados() {
        return empleados;
    }

    public Repositorio<Oficio> getOficios() {
        return oficios;
    }

    public Cliente buscarClienteEnLista(String dni) throws PersonaNoEncontradaException{
        for (Cliente cliente : clientes.listar()) {
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
    public boolean verSiEstaDisponible(String dni, LocalDate fecha) throws PersonaNoEncontradaException {
        Empleado empleado = buscarEmpleadoEnLista(dni);
        return empleado != null && empleado.estaDisponible(fecha);
    }

    public void registrarEmpleado(Empleado empleado) {
        if (empleado == null) return;
        try {
            if (buscarEmpleadoEnLista(empleado.getDni()) != null) {
                System.out.println("El DNI ya está registrado como empleado.");
                return;
            }
        } catch (PersonaNoEncontradaException e) {
            throw new RuntimeException(e);
        }
        empleados.agregar(empleado);
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) return;
        try {
            if (buscarClienteEnLista(cliente.getDni()) != null) {
                System.out.println("El DNI ya está registrado como cliente.");
                return;
            }
        } catch (PersonaNoEncontradaException e) {
            throw new RuntimeException(e);
        }
        clientes.agregar(cliente);
    }


    public Cliente iniciarSesionCliente(String dni, String password) throws PersonaNoEncontradaException {
        Cliente cliente = buscarClienteEnLista(dni);
        if (!cliente.validarPassword(password))
            throw new PersonaNoEncontradaException("Contraseña incorrecta para el cliente.");
        return cliente;
    }

    public Empleado iniciarSesionEmpleado(String dni, String password) throws PersonaNoEncontradaException {
        Empleado empleado = buscarEmpleadoEnLista(dni);
        if (!empleado.validarPassword(password))
            throw new PersonaNoEncontradaException("Contraseña incorrecta para el empleado.");
        return empleado;
    }


    public void mostrarOficios() {

        List<Oficio> lista = oficios.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay oficios cargados.");
            return;
        }
        System.out.println("OFICIOS DISPONIBLES:");
        for (Oficio oficio : lista) {
            System.out.println("- " + oficio.getNombre());
        }
    }

    public Oficio obtenerOcrearOficio(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        Oficio oficioEncontrado = buscarOficioPorNombre(nombre);

        // Si no lo encuentro, lo creo y lo guardo
        if (oficioEncontrado == null) {
            Oficio nuevoOficio = new Oficio(nombre.trim());
            oficios.agregar(nuevoOficio);
            return nuevoOficio;
        }
        else {
            // Si ya existe, devuelvo el que encontré
            return oficioEncontrado;
        }
    }


    public void contratarEmpleado(Empleado empleado, Contrataciones servicio, LocalDate fecha) throws EmpleadoNoDisponibleException {
        if (empleado == null || servicio == null || fecha == null) return;

        if (!empleado.estaDisponible(fecha)){
            throw new EmpleadoNoDisponibleException("El empleado no está disponible ese día.");
        }
        servicio.setEmpleado(empleado);
        servicio.setFecha(fecha);
        empleado.contratarServicio(servicio, fecha);
        empleado.registrarAccion("Contratado para: " + servicio.getDescripcion() + " (" + servicio.getIdServicio() + ") en fecha " + fecha);

        contrataciones.agregar(servicio);
    }

    public Repositorio<Contrataciones> obtenerContratacionesDeEmpleado(Empleado empleado) {

        Repositorio<Contrataciones> resultado = new Repositorio<>();

        if (empleado == null) {
            return resultado;  // devuelvo un repositorio vacío
        }

        // Recorro todas las contrataciones del sistema
        for (Contrataciones c : contrataciones.listar()) {

            // Si pertenece al empleado, lo agrego al nuevo repositorio
            if (c.getEmpleado() != null && c.getEmpleado().equals(empleado)) {
                resultado.agregar(c);
            }
        }

        return resultado;
    }

    public Repositorio<Contrataciones> obtenerContratacionesDeCliente(Cliente cliente) {
        Repositorio<Contrataciones> resultado = new Repositorio<>();
        if (cliente == null) {
            return resultado;
        }
        for (Contrataciones contratacion : contrataciones.listar()) {
            if (contratacion.getCliente() != null &&
                    contratacion.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
                resultado.agregar(contratacion);
            }
        }
        return resultado;
    }

    public Contrataciones crearContratacion(Cliente cliente, Oficio oficio, String descripcion, LocalDate fecha) {
        return new Contrataciones(descripcion, oficio, cliente, fecha);
    }

    public List<Empleado> obtenerEmpleadosPorOficio(Oficio oficio) {
        List<Empleado> resultado = new ArrayList<>();
        if (oficio == null) {
            return resultado;
        }
        for (Empleado empleado : empleados.listar()) {
            if (empleado.getOficio() != null && empleado.getOficio().equals(oficio)) {
                resultado.add(empleado);
            }
        }
        return resultado;
    }



}
