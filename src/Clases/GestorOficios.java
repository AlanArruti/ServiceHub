package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.OficioNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;
import Exceptions.FechaInvalidaException;
import Exceptions.TrabajoYaRealizadoException;
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

    public Cliente buscarClienteEnLista(String dni) throws PersonaNoEncontradaException {
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
        String key = Validaciones.claveNombreOficio(nombre);
        if (key == null || key.isEmpty()) {
            throw new OficioNoDisponibleException("Nombre de oficio invalido.");
        }
        for (Oficio oficio : oficios.listar()) {
            String ok = Validaciones.claveNombreOficio(oficio.getNombre());
            if (key.equals(ok)) return oficio;
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
                System.out.println("El DNI ya esta registrado como empleado.");
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
                System.out.println("El DNI ya esta registrado como cliente.");
                return;
            }
        } catch (PersonaNoEncontradaException e) {
            // No existe: se puede registrar
        }
        clientes.agregar(cliente);
        guardarClientes();
    }

    //Inicio de sesion de clientes y empleados
    public Cliente iniciarSesionCliente(String dni, String password) throws PersonaNoEncontradaException {
        Cliente cliente = buscarClienteEnLista(dni);
        if (!cliente.validarPassword(password))
            throw new PersonaNoEncontradaException("Contrasena incorrecta para el cliente.");
        return cliente;
    }

    public Empleado iniciarSesionEmpleado(String dni, String password) throws PersonaNoEncontradaException {
        Empleado empleado = buscarEmpleadoEnLista(dni);
        if (!empleado.validarPassword(password))
            throw new PersonaNoEncontradaException("Contrasena incorrecta para el empleado.");
        return empleado;
    }


    public void mostrarOficios() {

        List<Oficio> lista = oficios.listar();
        // Ordenar por nombre normalizado (ignora acentos y mayusculas)
        if (lista != null) {
            lista.sort(java.util.Comparator.comparing(o -> Validaciones.normalizarNombreOficio(o.getNombre()), String.CASE_INSENSITIVE_ORDER));
        }
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
        if (nombre == null) return null;
        String normalizado = Validaciones.normalizarNombreOficio(nombre);
        if (normalizado == null || normalizado.isEmpty()) return null;
        if (!Validaciones.esNombreOficioValido(normalizado)) {
            System.out.println("Nombre de oficio invalido. Solo letras y espacios.");
            return null;
        }
        try {
            return buscarOficioPorNombre(normalizado);
        } catch (OficioNoDisponibleException e) {
            Oficio nuevoOficio = new Oficio(normalizado);
            oficios.agregar(nuevoOficio);
            guardarOficios();
            return nuevoOficio;
        }
    }

    // Contrataciones
    public void contratarEmpleado(Empleado empleado, Contrataciones servicio, LocalDate fecha) throws EmpleadoNoDisponibleException {
        if (empleado == null || servicio == null || fecha == null) return;

        // Tiene que ser una fecha actual o futura
        if (fecha.isBefore(LocalDate.now())) {
            throw new FechaInvalidaException("La fecha no puede ser anterior a hoy.");
        }

        // Verificamos si ya tiene contrataciones activas
        if (!empleado.estaDisponible(fecha)) {
            throw new EmpleadoNoDisponibleException("El empleado no esta disponible ese dia.");
        }
        servicio.setEmpleado(empleado);
        servicio.setFecha(fecha);
        servicio.setEstado("PENDIENTE");
        empleado.contratarServicio(servicio, fecha);

        contrataciones.agregar(servicio);
        guardarContrataciones();
        guardarEmpleados();
    }

    // Permite que un empleado rechace una contratacion
    public void rechazarContratacion(Empleado empleado, Contrataciones servicio) throws TrabajoYaRealizadoException {
        if (empleado == null || servicio == null) return;

        // No se pueden rechazar contrataciones en fechas pasadas
        if (servicio.getFecha() != null && servicio.getFecha().isBefore(LocalDate.now())) {
            throw new TrabajoYaRealizadoException("El trabajo ya fue realizado y no puede ser rechazado.");
        }

        empleado.rechazarServicio(servicio);
        servicio.setEstado("RECHAZADO");

        // Dejar la contratacion sin empleado y registrar una notificacion para el cliente
        servicio.setEmpleado(null);

        // Evita NullPointerException en nombre y apellido
        String nombreEmpleado = (empleado.getNombre() == null ? "" : empleado.getNombre()) + " " + (empleado.getApellido() == null ? "" : empleado.getApellido());

        String msg = "El empleado " + nombreEmpleado.trim() + " rechazo su contratacion (" + servicio.getIdServicio() + ") de '" + servicio.getDescripcion() + "' para la fecha " + servicio.getFecha() + ".";

        servicio.setNotificacion(msg);
        guardarEmpleados();
        guardarContrataciones();
    }

    // Cancelar una contratacion por el cliente (ej. precio muy alto)
    public void cancelarContratacionPorCliente(Cliente cliente, Contrataciones servicio) {
        if (cliente == null || servicio == null) return;

        if (servicio.getCliente() == null || !servicio.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) return;
        Empleado e = servicio.getEmpleado();

        if (e != null) { e.rechazarServicio(servicio); e.registrarAccion("El cliente " + cliente.getNombre() + " cancelo la contratacion " + servicio.getIdServicio()); }
        servicio.setEmpleado(null);
        servicio.setEstado("CANCELADO");
        guardarEmpleados();
        guardarContrataciones();
    }

    // Asigna un empleado a una contratacion existente (reasignacion), sin duplicar en el repositorio
    public void asignarEmpleadoAContratacion(Empleado empleado, Contrataciones servicio, LocalDate fecha) throws EmpleadoNoDisponibleException {
        if (empleado == null || servicio == null || fecha == null) return;

        if (fecha.isBefore(LocalDate.now())) {
            throw new FechaInvalidaException("La fecha no puede ser anterior a hoy.");
        }
        if (!empleado.estaDisponible(fecha)) {
            throw new EmpleadoNoDisponibleException("El empleado no esta disponible ese dia.");
        }

        servicio.setEmpleado(empleado);
        servicio.setFecha(fecha);
        servicio.setEstado("PENDIENTE");
        empleado.contratarServicio(servicio, fecha);

        // Limpia las notificaciones al cliente por reasignacion exitosa
        servicio.setNotificacion(null);
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
        String desc = Clases.Validaciones.normalizarDescripcion(descripcion);
        if (oficio == null || cliente == null || fecha == null || !Clases.Validaciones.esDescripcionValida(desc)) {
            return null;
        }
        return new Contrataciones(desc, oficio, cliente, fecha);
    }

    public List<Empleado> obtenerEmpleadosPorOficio(Oficio oficio) {
        List<Empleado> resultado = new ArrayList<>();
        if (oficio == null) {
            return resultado;
        }
        for (Empleado empleado : empleados.listar()) {
            if (empleado.tieneOficio(oficio)) {
                resultado.add(empleado);
            }
        }
        return resultado;
    }


    // Pasamos los datos al archivo
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
            // persistir oficios del empleado como arreglo
            JSONArray arrOficios = new JSONArray();
            for (Oficio o : empleado.getOficios()) {
                if (o != null) {
                    JSONObject ojson = new JSONObject();
                    ojson.put("id", o.getId());
                    ojson.put("nombre", o.getNombre());
                    arrOficios.put(ojson);
                }
            }
            if (arrOficios.length() > 0) {
                objeto.put("oficios", arrOficios);
            }
            objeto.put("direccion", convertirDireccionAJson(empleado.getDireccion()));
            // Persistir calificaciones del empleado
            JSONArray califs = new JSONArray();
            for (Calificacion cal : empleado.getCalificaciones()) {
                JSONObject oc = new JSONObject();
                Cliente cli = cal.getCliente();
                if (cli != null) oc.put("clienteDni", cli.getDni());
                oc.put("puntaje", cal.getPuntaje());
                oc.put("comentario", cal.getComentario());
                oc.put("fecha", cal.getFecha() != null ? cal.getFecha().toString() : "");
                String idc = cal.getIdServicio();
                if (idc == null || idc.trim().isEmpty()) continue;
                oc.put("idServicio", idc);
                califs.put(oc);
            }
            if (califs.length() > 0) {
                objeto.put("calificaciones", califs);
            }
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_EMPLEADOS);
    }

    public void guardarContrataciones() {
        JSONArray array = new JSONArray();
        for (Contrataciones contratacion : contrataciones.listar()) {
            JSONObject objeto = new JSONObject();
            objeto.put("id", contratacion.getIdServicio());
            objeto.put("descripcion", Clases.Validaciones.normalizarDescripcion(contratacion.getDescripcion()));
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
            if (contratacion.getNotificacion() != null) {
                objeto.put("notificacion", contratacion.getNotificacion());
            }
            if (contratacion.getEstado() != null) {
                objeto.put("estado", contratacion.getEstado());
            }
            if (contratacion.getPrecio() != null) {
                objeto.put("precio", contratacion.getPrecio());
            }
            array.put(objeto);
        }
        JSONUtiles.cargarJSON(array, ARCHIVO_CONTRATACIONES);
    }

    // Deserializacion
    private void cargarOficios() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_OFICIOS);

        java.util.Set<String> nombresCargados = new java.util.HashSet<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            if (objeto.has("fecha")) {
                System.out.println("ADVERTENCIA: Se encontro un campo 'fecha' en oficios.json. Se ignorara");
            }
            String id = objeto.optString("id");
            String nombre = objeto.optString("nombre");
            if (nombre == null) continue;
            String n = nombre.trim();
            if (n.isEmpty()) continue;
            String key = Validaciones.claveNombreOficio(n);
            if (nombresCargados.contains(key)) {
                continue;
            }
            if (!Validaciones.esNombreOficioValido(n)) continue;
            // Guardar con nombre normalizado para evitar duplicados visuales por tildes
            String nombreNormalizado = Validaciones.normalizarNombreOficio(n);
            Oficio oficio = new Oficio(id, nombreNormalizado);
            oficios.agregar(oficio);
            nombresCargados.add(key);
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
            // Cargar mÃºltiples oficios si existen
            List<Oficio> oficiosEmpleado = new ArrayList<>();
            JSONArray arrOficios = objeto.optJSONArray("oficios");
            if (arrOficios != null) {
                for (int j = 0; j < arrOficios.length(); j++) {
                    JSONObject oj = arrOficios.getJSONObject(j);
                    String oid = oj.optString("id");
                    String onom = oj.optString("nombre");
                    Oficio o = buscarOficioPorId(oid);
                    if (o == null && onom != null && !onom.isEmpty()) {
                        try {
                            o = buscarOficioPorNombre(onom);
                        } catch (OficioNoDisponibleException ignore) {
                        }
                        if (o == null) {
                            o = new Oficio(oid, onom);
                            oficios.agregar(o);
                        }
                    }
                    if (o != null) oficiosEmpleado.add(o);
                }
            } else {
                // compatibilidad hacia atras: campos oficioId/oficioNombre
                Oficio oficio = buscarOficioPorId(oficioId);
                if (oficio == null && oficioNombre != null && !oficioNombre.isEmpty()) {
                    try {
                        oficio = buscarOficioPorNombre(oficioNombre.trim());
                    } catch (OficioNoDisponibleException ignore) {
                    }
                    if (oficio == null) {
                        oficio = new Oficio(oficioId, oficioNombre);
                        oficios.agregar(oficio);
                    }
                }
                if (oficio != null) oficiosEmpleado.add(oficio);
            }

            Empleado empleado = new Empleado(dni, nombre, apellido, email, telefono, password, null);
            for (Oficio o : oficiosEmpleado) {
                empleado.agregarOficio(o);
            }
            empleado.setDireccion(direccion);
            // Cargar calificaciones si existen
            JSONArray califs = objeto.optJSONArray("calificaciones");
            if (califs != null) {
                for (int j = 0; j < califs.length(); j++) {
                    JSONObject oc = califs.getJSONObject(j);
                    String clienteDni = oc.optString("clienteDni");
                    double puntaje = oc.optDouble("puntaje", 0.0);
                    String comentario = oc.optString("comentario");
                    String fechaTxt = oc.optString("fecha");
                    String idServ = oc.optString("idServicio");
                    if (idServ == null || idServ.isEmpty()) continue;
                    LocalDate fecha = null;
                    try {
                        if (fechaTxt != null && !fechaTxt.isEmpty()) fecha = LocalDate.parse(fechaTxt);
                    } catch (Exception ignore) {
                    }
                    Cliente cli = null;
                    try {
                        cli = buscarClienteEnLista(clienteDni);
                    } catch (PersonaNoEncontradaException ex) {
                        cli = null;
                    }
                    if (cli != null) {
                        empleado.agregarCalificacionGuardada(cli, puntaje, comentario, fecha, idServ);
                    }
                }
            }
            empleados.agregar(empleado);
        }
    }

    private void cargarContrataciones() {
        JSONArray array = JSONUtiles.leerArreglo(ARCHIVO_CONTRATACIONES);
        for (int i = 0; i < array.length(); i++) {
            JSONObject objeto = array.getJSONObject(i);
            String id = objeto.optString("id");
            String descripcion = Clases.Validaciones.normalizarDescripcion(objeto.optString("descripcion"));
            if (descripcion == null || descripcion.isEmpty()) continue;
            String fechaTexto = objeto.optString("fecha");
            String oficioId = objeto.optString("oficioId");
            String oficioNombre = objeto.optString("oficioNombre");
            String clienteDni = objeto.optString("clienteDni");
            String empleadoDni = objeto.optString("empleadoDni");
            String notificacion = objeto.optString("notificacion");
            String estado = objeto.optString("estado");
            Double precio = objeto.has("precio") ? objeto.optDouble("precio") : null;

            Oficio oficio = buscarOficioPorId(oficioId);
            if (oficio == null && oficioNombre != null && !oficioNombre.isEmpty()) {
                try {
                    oficio = buscarOficioPorNombre(oficioNombre.trim());
                } catch (OficioNoDisponibleException ignore) {
                }
                if (oficio == null && Clases.Validaciones.esNombreOficioValido(oficioNombre)) {
                    oficio = new Oficio(oficioId, oficioNombre.trim());
                    oficios.agregar(oficio);
                }
            }
            if (fechaTexto == null || fechaTexto.isEmpty()) continue;

            Cliente cliente = null;
            try {
                cliente = buscarClienteEnLista(clienteDni);
            } catch (PersonaNoEncontradaException e) {
                System.out.println("ADVERTENCIA: Contratacion '" + id + "' referencia un cliente inexistente (DNI: " + clienteDni + "). Se omite.");
                continue;
            }
            Empleado empleado = null;
            try {
                empleado = buscarEmpleadoEnLista(empleadoDni);
            } catch (PersonaNoEncontradaException e) {
                empleado = null; // Puede no estar asignado o no existir; se carga igual
            }
            LocalDate fecha = LocalDate.parse(fechaTexto);

            Contrataciones contratacion = new Contrataciones(descripcion, oficio, cliente, fecha);
            contratacion.setEmpleado(empleado);
            if (estado != null && !estado.isEmpty()) {
                contratacion.setEstado(estado);
            }
            if (precio != null && precio > 0) {
                try {
                    contratacion.setPrecio(precio);
                } catch (Exception ignore) {
                }
            }
            if (notificacion != null && !notificacion.isEmpty()) {
                contratacion.setNotificacion(notificacion);
            }
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

    public void aceptarContratacion(Empleado empleado, Contrataciones servicio, double precio) {
        if (empleado == null || servicio == null) return;
        if (servicio.getEmpleado() == null || !servicio.getEmpleado().getDni().equalsIgnoreCase(empleado.getDni()))
            return;
        if (precio <= 0) return;
        servicio.setPrecio(precio);
        servicio.setEstado("ACEPTADO");
        servicio.setNotificacion(null);
        guardarContrataciones();
        guardarEmpleados();
    }
}
