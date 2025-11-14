package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.OficioNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;
import Exceptions.FechaInvalidaException;
import ManejoJSON.JSONUtiles;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorOficios {
    private Repositorio<Empleado> empleados;
    private Repositorio<Cliente> clientes;
    private Repositorio<Contrataciones> contrataciones;
    private Repositorio<Oficio> oficios;

    // Carpeta de datos (con separador)
    private static final String CARPETA_DATOS = "datos/";
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

    private Oficio buscarOficioPorId(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        for (Oficio oficio : oficios.listar()) {
            if (oficio.getId().equalsIgnoreCase(id)) {
                return oficio;
            }
        }
        return null;
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
            // No existe: se puede registrar
        }
        empleados.agregar(empleado);
        guardarEmpleados();
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) return;
        try {
            if (buscarClienteEnLista(cliente.getDni()) != null) {
                System.out.println("El DNI ya está registrado como cliente.");
                return;
            }
        } catch (PersonaNoEncontradaException e) {
            // No existe: se puede registrar
        }
        clientes.agregar(cliente);
        guardarClientes();
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

        Oficio oficioEncontrado;
        try {
            oficioEncontrado = buscarOficioPorNombre(nombre);
        } catch (OficioNoDisponibleException e) {
            Oficio nuevoOficio = new Oficio(nombre.trim());
            oficios.agregar(nuevoOficio);
            guardarOficios();
            return nuevoOficio;
        }

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

        if (fecha.isBefore(LocalDate.now())) {
            throw new FechaInvalidaException("La fecha no puede ser anterior a hoy.");
        }

        if (!empleado.estaDisponible(fecha)){
            throw new EmpleadoNoDisponibleException("El empleado no está disponible ese día.");
        }
        servicio.setEmpleado(empleado);
        servicio.setFecha(fecha);
        empleado.contratarServicio(servicio, fecha);

        contrataciones.agregar(servicio);
        guardarContrataciones();
        guardarEmpleados();
    }

    public List<Contrataciones> obtenerContratacionesDeCliente(Cliente cliente) {
        List<Contrataciones> resultado = new ArrayList<>();
        if (cliente == null) {
            return resultado;
        }
        for (Contrataciones contratacion : contrataciones.listar()) {
            if (contratacion.getCliente() != null &&
                    contratacion.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
                resultado.add(contratacion);
            }
        }
        return resultado;
    }

    public List<Contrataciones> obtenerContratacionesDeEmpleado(Empleado empleado) {
        List<Contrataciones> resultado = new ArrayList<>();
        if (empleado == null) {
            return resultado;
        }
        for (Contrataciones contratacion : contrataciones.listar()) {
            if (contratacion.getEmpleado() != null &&
                    contratacion.getEmpleado().getDni().equalsIgnoreCase(empleado.getDni())) {
                resultado.add(contratacion);
            }
        }
        return resultado;
    }

    public Contrataciones buscarContratacionPorId(String id) throws PersonaNoEncontradaException {
        return contrataciones.buscarPorId(id);
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

    public void guardarOficios() {
        JSONArray array = new JSONArray();
        for (Oficio oficio : oficios.listar()) {
            JSONObject objeto = new JSONObject();
            objeto.put("id", oficio.getId());
            objeto.put("nombre", oficio.getNombre());
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_OFICIOS);
    }

    public void guardarClientes() {
        JSONArray array = new JSONArray();
        for (Cliente cliente : clientes.listar()) {
            JSONObject objeto = new JSONObject();
            objeto.put("dni", cliente.getDni());
            objeto.put("nombre", cliente.getNombre());
            objeto.put("apellido", cliente.getApellido());
            objeto.put("email", cliente.getEmail());
            objeto.put("telefono", cliente.getTelefono());
            objeto.put("password", cliente.getPassword());
            objeto.put("direccion", convertirDireccionAJson(cliente.getDireccion()));
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_CLIENTES);
    }

    public void guardarEmpleados() {
        JSONArray array = new JSONArray();
        for (Empleado empleado : empleados.listar()) {
            JSONObject objeto = new JSONObject();
            objeto.put("dni", empleado.getDni());
            objeto.put("nombre", empleado.getNombre());
            objeto.put("apellido", empleado.getApellido());
            objeto.put("email", empleado.getEmail());
            objeto.put("telefono", empleado.getTelefono());
            objeto.put("password", empleado.getPassword());
            if (empleado.getOficio() != null) {
                objeto.put("oficioId", empleado.getOficio().getId());
                objeto.put("oficioNombre", empleado.getOficio().getNombre());
            }
            objeto.put("direccion", convertirDireccionAJson(empleado.getDireccion()));
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_EMPLEADOS);
    }

    public void guardarContrataciones() {
        JSONArray array = new JSONArray();
        for (Contrataciones contratacion : contrataciones.listar()) {
            JSONObject objeto = new JSONObject();
            objeto.put("id", contratacion.getIdServicio());
            objeto.put("descripcion", contratacion.getDescripcion());
            objeto.put("fecha", contratacion.getFecha().toString());
            if (contratacion.getOficio() != null) {
                objeto.put("oficioId", contratacion.getOficio().getId());
                objeto.put("oficioNombre", contratacion.getOficio().getNombre());
            }
            if (contratacion.getCliente() != null) {
                objeto.put("clienteDni", contratacion.getCliente().getDni());
            }
            if (contratacion.getEmpleado() != null) {
                objeto.put("empleadoDni", contratacion.getEmpleado().getDni());
            }
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_CONTRATACIONES);
    }

    
    private void cargarOficios() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_OFICIOS);
        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            String id = objeto.optString("id");
            String nombre = objeto.optString("nombre");
            if (nombre != null && !nombre.isEmpty()) {
                Oficio oficio = new Oficio(id, nombre);
                oficios.agregar(oficio);
            }
        }
    }

    private void cargarClientes() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_CLIENTES);
        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            String dni = objeto.optString("dni");
            String nombre = objeto.optString("nombre");
            String apellido = objeto.optString("apellido");
            String email = objeto.optString("email");
            String telefono = objeto.optString("telefono");
            String password = objeto.optString("password");
            Direccion direccion = crearDireccionDesdeJson(objeto.optJSONObject("direccion"));
            Cliente cliente = new Cliente(dni, nombre, apellido, email, telefono, password, direccion);
            clientes.agregar(cliente);
        }
    }

    private void cargarEmpleados() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_EMPLEADOS);
        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            String dni = objeto.optString("dni");
            String nombre = objeto.optString("nombre");
            String apellido = objeto.optString("apellido");
            String email = objeto.optString("email");
            String telefono = objeto.optString("telefono");
            String password = objeto.optString("password");
            String oficioId = objeto.optString("oficioId");
            String oficioNombre = objeto.optString("oficioNombre");
            Direccion direccion = crearDireccionDesdeJson(objeto.optJSONObject("direccion"));
            Oficio oficio = buscarOficioPorId(oficioId);
            if (oficio == null && oficioNombre != null && !oficioNombre.isEmpty()) {
                oficio = new Oficio(oficioId, oficioNombre);
                oficios.agregar(oficio);
            }
            Empleado empleado = new Empleado(dni, nombre, apellido, email, telefono, password, oficio);
            empleado.setDireccion(direccion);
            empleados.agregar(empleado);
        }
    }

    private void cargarContrataciones() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_CONTRATACIONES);
        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            String id = objeto.optString("id");
            String descripcion = objeto.optString("descripcion");
            String fechaTexto = objeto.optString("fecha");
            String oficioId = objeto.optString("oficioId");
            String oficioNombre = objeto.optString("oficioNombre");
            String clienteDni = objeto.optString("clienteDni");
            String empleadoDni = objeto.optString("empleadoDni");

            Oficio oficio = buscarOficioPorId(oficioId);
            if (oficio == null && oficioNombre != null && !oficioNombre.isEmpty()) {
                oficio = new Oficio(oficioId, oficioNombre);
                oficios.agregar(oficio);
            }

            if (fechaTexto == null || fechaTexto.isEmpty()) continue;

            Cliente cliente = null;
            try {
                cliente = buscarClienteEnLista(clienteDni);
            } catch (PersonaNoEncontradaException e) {
                throw new RuntimeException(e);
            }
            Empleado empleado = null;
            try {
                empleado = buscarEmpleadoEnLista(empleadoDni);
            } catch (PersonaNoEncontradaException e) {
                throw new RuntimeException(e);
            }
            LocalDate fecha = LocalDate.parse(fechaTexto);

            Contrataciones contratacion = new Contrataciones(descripcion, oficio, cliente, fecha);
            contratacion.setEmpleado(empleado);
            contrataciones.agregar(contratacion);

            if (empleado != null) {
                empleado.cargarContratacionGuardada(contratacion, fecha);
            }
        }
    }

    private JSONObject convertirDireccionAJson(Direccion direccion) {
        if (direccion == null) return null;
        JSONObject objeto = new JSONObject();
        objeto.put("ciudad", direccion.getCiudad());
        objeto.put("calle", direccion.getCalle());
        objeto.put("numero", direccion.getNumero());
        return objeto;
    }

    private Direccion crearDireccionDesdeJson(JSONObject objeto) {
        if (objeto == null) return null;
        String ciudad = objeto.optString("ciudad");
        String calle = objeto.optString("calle");
        int numero = objeto.optInt("numero");
        return new Direccion(ciudad, calle, numero);
    }

    //carga y guardado de datos
    public void cargarDatos() {
        cargarOficios();
        cargarClientes();
        cargarEmpleados();
        cargarContrataciones();
    }

    

    public void guardarTodo() {
        guardarOficios();
        guardarClientes();
        guardarEmpleados();
        guardarContrataciones();
    }

    public Cliente iniciarSesion(GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- LOGIN CLIENTE ---");
        System.out.print("DNI: ");
        String dni = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            return gestor.iniciarSesionCliente(dni, password);
        } catch (PersonaNoEncontradaException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }





}
