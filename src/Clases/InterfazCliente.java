package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;
import Exceptions.NumeroInvalidoException;
import Exceptions.FechaInvalidaException;
import Exceptions.CampoVacioException;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class InterfazCliente {

    private Direccion cargarDireccion(Scanner sc) throws NumeroInvalidoException, Exceptions.CampoVacioException {
        System.out.println("\n--- DIRECCION ---");
        System.out.print("Ciudad: ");
        String ciudad = sc.nextLine();
        if (ciudad == null || ciudad.trim().isEmpty()) { throw new Exceptions.CampoVacioException("El campo Ciudad se ingreso vacio."); }
        System.out.print("Calle: ");
        String calle = sc.nextLine();
        if (calle == null || calle.trim().isEmpty()) { throw new Exceptions.CampoVacioException("El campo Calle se ingreso vacio."); }
        System.out.print("Numero: ");
        String numeroTexto = sc.nextLine();
        int numero;
        try {
            numero = Integer.parseInt(numeroTexto.trim());
        } catch (NumberFormatException e) {
            throw new NumeroInvalidoException("El numero de la direccion es invalido.");
        }
        return new Direccion(ciudad, calle, numero);
    }

    private void verOficios(GestorOficios gestor) {
        gestor.mostrarOficios();
    }

    public Cliente registrarCliente(GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- REGISTRO CLIENTE ---");
        System.out.print("DNI: ");
        String dni = sc.nextLine();
        try {
            // Si ya existe, no continua
            if (gestor.buscarClienteEnLista(dni) != null) {
                System.out.println("El DNI ya esta registrado como cliente.");
                return null;
            }
        } catch (PersonaNoEncontradaException e) {
            // No existe: continuar con el registro
        }
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Telefono: ");
        String telefono = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        // validaciones para que los cambios no esten vacios
        try {
            if (dni == null || dni.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo DNI se ingreso vacio.");
            if (nombre == null || nombre.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo Nombre se ingreso vacio.");
            if (apellido == null || apellido.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo Apellido se ingreso vacio.");
            if (email == null || email.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo Email se ingreso vacio.");
            if (telefono == null || telefono.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo Telefono se ingreso vacio.");
            if (password == null || password.trim().isEmpty()) throw new Exceptions.CampoVacioException("El campo Password se ingreso vacio.");
        } catch (Exceptions.CampoVacioException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
        // Validaciones de formato
        if (!Validaciones.esDniValido(dni)) {
            System.out.println("ERROR: El DNI debe contener solo numeros.");
            return null;
        }
        if (!Validaciones.esNombreApellidoValido(nombre)) {
            System.out.println("ERROR: El Nombre solo puede contener letras y espacios.");
            return null;
        }
        if (!Validaciones.esNombreApellidoValido(apellido)) {
            System.out.println("ERROR: El Apellido solo puede contener letras y espacios.");
            return null;
        }
        if (!Validaciones.esEmailValido(email)) {
            System.out.println("ERROR: El Email es invalido; debe contener exactamente un '@'.");
            return null;
        }
        Direccion direccion;
        try {
            direccion = cargarDireccion(sc);
        } catch (CampoVacioException | NumeroInvalidoException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }

        Cliente cliente = new Cliente(dni, nombre, apellido, email, telefono, password, direccion);
        gestor.registrarCliente(cliente);
        System.out.println("Cliente registrado correctamente.");
        return cliente;
    }

    private void contratarServicio(Cliente cliente, GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- CONTRATAR SERVICIO ---");
        // Elegir oficio desde lista existente (sin crear nuevos)
        java.util.List<Oficio> oficios = gestor.getOficios().listar();
        if (oficios == null || oficios.isEmpty()) {
            System.out.println("No hay oficios disponibles.");
            return;
        }
        oficios.sort(java.util.Comparator.comparing(o -> Clases.Validaciones.normalizarNombreOficio(o.getNombre()), String.CASE_INSENSITIVE_ORDER));
        System.out.println("Oficios disponibles (seleccione por numero):");
        for (int i = 0; i < oficios.size(); i++) {
            System.out.println((i + 1) + " - " + oficios.get(i).getNombre());
        }
        System.out.print("Opcion: ");
        int idxOfi = -1; try { idxOfi = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignore) {}
        if (idxOfi < 1 || idxOfi > oficios.size()) {
            System.out.println("Opcion invalida.");
            return;
        }
        Oficio oficio = oficios.get(idxOfi - 1);

        // Solicitar fecha primero para listar solo los disponibles ese dia
        System.out.print("Ingrese la fecha (yyyy-MM-dd): ");
        String fechaTexto = sc.nextLine();
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto);
        } catch (Exception e) {
            System.out.println("Formato de fecha invalido.");
            return;
        }

        // Obtener candidatos por oficio y filtrar por disponibilidad en la fecha
        List<Empleado> candidatos = gestor.obtenerEmpleadosPorOficio(oficio);
        if (candidatos.isEmpty()) {
            System.out.println("No hay empleados registrados para ese oficio.");
            return;
        }
        java.util.List<Empleado> disponibles = new java.util.ArrayList<>();
        for (Empleado e : candidatos) {
            if (e.estaDisponible(fecha)) {
                disponibles.add(e);
            }
        }
        // Ordenar por reputacion promedio
        disponibles.sort(java.util.Comparator.comparingDouble(Empleado::getReputacion).reversed());
        if (disponibles.isEmpty()) {
            System.out.println("No hay empleados disponibles para ese oficio en esa fecha.");
            return;
        }

        System.out.println("Empleados disponibles para " + fecha + " (ordenados por reputacion):");
        for (Empleado emp : disponibles) {
            System.out.println("- " + emp.getNombre() + " " + emp.getApellido() +
                    " | DNI: " + emp.getDni() +
                    " | Reputacion: " + String.format("%.2f", emp.getReputacion()));
        }

        System.out.print("Ingrese el DNI del empleado elegido: ");
        String dniEmpleado = sc.nextLine();
        Empleado empleadoSeleccionado;
        try {
            empleadoSeleccionado = gestor.buscarEmpleadoEnLista(dniEmpleado);
        } catch (PersonaNoEncontradaException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        // Validar que pertenezca al oficio elegido
        if (!empleadoSeleccionado.tieneOficio(oficio)) {
            System.out.println("El empleado seleccionado no corresponde al oficio elegido.");
            return;
        }

        // Validar que esté entre los mostrados como disponibles
        boolean estaEnLista = false;
        for (Empleado emp : disponibles) {
            if (emp.getDni().equalsIgnoreCase(empleadoSeleccionado.getDni())) { estaEnLista = true; break; }
        }
        if (!estaEnLista || !empleadoSeleccionado.estaDisponible(fecha)) {
            System.out.println("El empleado no esta disponible ese dia.");
            return;
        }

        System.out.print("Descripcion del servicio: ");
        String descripcion = sc.nextLine();
        if (!Validaciones.esDescripcionValida(descripcion)) {
            System.out.println("Descripcion invalida. Debe ingresar algun detalle del servicio.");
            return;
        }
        String descNorm = Validaciones.normalizarDescripcion(descripcion);

        Contrataciones contratacion = gestor.crearContratacion(cliente, oficio, descNorm, fecha);
        if (contratacion == null) {
            System.out.println("No se pudo crear la contratacion con la descripcion ingresada.");
            return;
        }
        try {
            gestor.contratarEmpleado(empleadoSeleccionado, contratacion, fecha);
            System.out.println("Servicio contratado con exito.");
        } catch (FechaInvalidaException | EmpleadoNoDisponibleException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void mostrarContratacionesCliente(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> lista = gestor.obtenerContratacionesDeCliente(cliente);
        if (lista.isEmpty()) {
            System.out.println("No hay contrataciones registradas.");
            return;
        }
        System.out.println("\n--- MIS CONTRATACIONES ---");
        for (Contrataciones c : lista) {
            String estadoActual = c.getEstado();
            if ("CANCELADO".equalsIgnoreCase(estadoActual) || "RECHAZADO".equalsIgnoreCase(estadoActual)) {
                continue; // ocultar canceladas/rechazadas
            }
            String oficioTxt = (c.getOficio()==null)?"(sin oficio)":c.getOficio().getNombre();
            String empTxt = (c.getEmpleado()==null)?"Sin asignar":(c.getEmpleado().getNombre()+" "+c.getEmpleado().getApellido());
            String estado = c.getEstado() == null ? "(sin estado)" : c.getEstado();
            String precio = c.getPrecio() == null ? "(sin precio)" : String.format("$%.2f", c.getPrecio());
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() +
                    "' | Oficio: " + oficioTxt + " | Empleado: " + empTxt + " | Estado: " + estado + " | Precio: " + precio);
            if (c.getNotificacion() != null && !c.getNotificacion().trim().isEmpty()) {
                java.time.LocalDate hoy = java.time.LocalDate.now();
                if (c.getFecha() != null && !c.getFecha().isBefore(hoy)) {
                    System.out.println("  Aviso: " + c.getNotificacion());
                }
            }
        }
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


    public void menuCliente(Cliente cliente, GestorOficios gestor) {
        if (cliente == null) {
            return;
        }

        Scanner sc = new Scanner(System.in);
        char seguir = 's';

        while (seguir == 's') {
            System.out.println("\n--- MENU CLIENTE ---");
            System.out.println("1 - Ver oficios");
            System.out.println("2 - Contratar servicio");
            System.out.println("3 - Ver mis contrataciones");
            System.out.println("4 - Reasignar contratacion");
            System.out.println("5 - Ver contrataciones pendientes");
            System.out.println("6 - Modificar mis datos");
            System.out.println("7 - Calificar servicio");
            System.out.println("8 - Ver contrataciones futuras");
            System.out.println("9 - Cancelar contratacion futura por ID");
            System.out.println("10 - Salir");
            System.out.print("Opcion: ");
            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1:{
                    verOficios(gestor);
                    break;
                }
                case 2:{
                    contratarServicio(cliente, gestor);
                    break;
                }
                case 3:{
                    mostrarContratacionesCliente(cliente, gestor);
                    break;
                }
                case 4:{
                    reasignarContratacion(cliente, gestor);
                    break;
                }
                case 5:{
                    verPendientesYReasignar(cliente, gestor);
                    break;
                }
                case 6:{
                    modificarDatosCliente(cliente, gestor);
                    break;
                }
                case 7:{
                    calificarServicio(cliente, gestor);
                    break;
                }
                case 8:{
                    verContratacionesFuturas(cliente, gestor);
                    break;
                }
                case 9:{
                    cancelarContratacionFuturaPorId(cliente, gestor);
                    break;
                }
                case 10:{
                    seguir = 'n';
                    break;
                }
                default:{
                    System.out.println("Opcion invalida.");
                }

            }

            // No preguntar; permanecer hasta elegir 'Salir'
        }
    }

    private int leerEntero(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; }
    }

    private void verPendientesYReasignar(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> mias = gestor.obtenerContratacionesDeCliente(cliente);
        java.util.List<Contrataciones> pendientes = new java.util.ArrayList<>();
        for (Contrataciones c : mias) {
            if (c.getEmpleado() == null) {
                String estado = c.getEstado();
                if (estado == null || (!"CANCELADO".equalsIgnoreCase(estado) && !"RECHAZADO".equalsIgnoreCase(estado))) {
                    pendientes.add(c);
                }
            }
        }
        System.out.println("\n--- CONTRATACIONES PENDIENTES ---");
        if (pendientes.isEmpty()) {
            System.out.println("No tiene contrataciones pendientes de asignacion.");
            return;
        }
        for (Contrataciones c : pendientes) {
            String nombreOficio = (c.getOficio() == null) ? "(sin oficio)" : c.getOficio().getNombre();
            String estado = c.getEstado() == null ? "(sin estado)" : c.getEstado();
            String precio = c.getPrecio() == null ? "(sin precio)" : String.format("$%.2f", c.getPrecio());
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Oficio: " + nombreOficio);
            System.out.println("  Estado: " + estado + " | Precio: " + precio);
            if (c.getNotificacion() != null && !c.getNotificacion().trim().isEmpty()) {
                System.out.println("  Aviso: " + c.getNotificacion());
            }
        }
        System.out.print("¿Desea reasignar alguna ahora? (s/n): ");
        String r = new java.util.Scanner(System.in).nextLine();
        if (!r.isEmpty() && Character.toLowerCase(r.charAt(0)) == 's') {
            reasignarContratacion(cliente, gestor);
        }
    }

    private void verContratacionesFuturas(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> mias = gestor.obtenerContratacionesDeCliente(cliente);
        if (mias.isEmpty()) {
            System.out.println("No hay contrataciones registradas.");
            return;
        }
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.util.List<Contrataciones> futuras = new java.util.ArrayList<>();
        for (Contrataciones c : mias) {
            if (c.getFecha() != null && !c.getFecha().isBefore(hoy)) { // incluye hoy y futuro
                String estado = c.getEstado();
                boolean activa = (estado == null || (!"CANCELADO".equalsIgnoreCase(estado) && !"RECHAZADO".equalsIgnoreCase(estado)));
                boolean aceptadaHoyNoCancelable = ("ACEPTADO".equalsIgnoreCase(estado) && c.getFecha().isEqual(hoy));
                if (activa && !aceptadaHoyNoCancelable) {
                    futuras.add(c);
                }
            }
        }
        if (futuras.isEmpty()) {
            System.out.println("No hay contrataciones futuras.");
            return;
        }
        System.out.println("\n--- MIS CONTRATACIONES FUTURAS ---");
        for (Contrataciones c : futuras) {
            String precio = c.getPrecio() == null ? "(sin precio)" : String.format("$%.2f", c.getPrecio());
            String estado = c.getEstado() == null ? "(sin estado)" : c.getEstado();
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Estado: " + estado + " | Precio: " + precio);
        }
    }

    private void cancelarContratacionFuturaPorId(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> mias = gestor.obtenerContratacionesDeCliente(cliente);
        if (mias.isEmpty()) {
            System.out.println("No hay contrataciones registradas.");
            return;
        }
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.util.List<Contrataciones> futuras = new java.util.ArrayList<>();
        for (Contrataciones c : mias) {
            if (c.getFecha() != null && !c.getFecha().isBefore(hoy)) { // incluye hoy y futuro
                String estado = c.getEstado();
                if (estado == null || (!"CANCELADO".equalsIgnoreCase(estado) && !"RECHAZADO".equalsIgnoreCase(estado))) {
                    futuras.add(c);
                }
            }
        }
        if (futuras.isEmpty()) {
            System.out.println("No hay contrataciones futuras para cancelar.");
            return;
        }
        System.out.println("\n--- CONTRATACIONES CANCELABLES ---");
        for (Contrataciones c : futuras) {
            String precio = c.getPrecio() == null ? "(sin precio)" : String.format("$%.2f", c.getPrecio());
            String estado = c.getEstado() == null ? "(sin estado)" : c.getEstado();
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Estado: " + estado + " | Precio: " + precio);
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el ID a cancelar: ");
        String id = sc.nextLine();
        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(id);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontro una contratacion con ese ID.");
            return;
        }
        if (seleccionada == null || seleccionada.getCliente() == null ||
                !seleccionada.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
            System.out.println("La contratacion indicada no pertenece a usted.");
            return;
        }
        if (seleccionada.getFecha() == null || seleccionada.getFecha().isBefore(java.time.LocalDate.now())) {
            System.out.println("Solo puede cancelar contrataciones de hoy o futuras.");
            return;
        }
        String estadoSel = seleccionada.getEstado();
        java.time.LocalDate hoy2 = java.time.LocalDate.now();
        if (estadoSel != null) {
            if ("CANCELADO".equalsIgnoreCase(estadoSel) || "RECHAZADO".equalsIgnoreCase(estadoSel)) {
                System.out.println("La contratacion ya no esta activa (" + estadoSel + ").");
                return;
            }
            if ("ACEPTADO".equalsIgnoreCase(estadoSel)) {
                if (seleccionada.getFecha().isEqual(hoy2)) {
                    System.out.println("No puede cancelar una contratacion aceptada para hoy. Contacte al empleado.");
                    return;
                } else {
                    System.out.print("La contratacion fue aceptada con precio. ¿Desea cancelar igualmente? (s/n): ");
                    String conf = sc.nextLine();
                    if (conf.isEmpty() || Character.toLowerCase(conf.charAt(0)) != 's') {
                        System.out.println("Cancelacion abortada.");
                        return;
                    }
                }
            }
        }
        gestor.cancelarContratacionPorCliente(cliente, seleccionada);
        System.out.println("Contratacion cancelada.");
    }


    private void modificarDatosCliente(Cliente cliente, GestorOficios gestor) {
        if (cliente == null) return;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("\n--- MODIFICAR MIS DATOS (CLIENTE) ---");
            System.out.println("1 - Nombre");
            System.out.println("2 - Apellido");
            System.out.println("3 - Telefono");
            System.out.println("4 - Email");
            System.out.println("5 - DNI");
            System.out.println("6 - Direccion");
            System.out.println("7 - Volver");
            System.out.print("Opcion: ");
            int op = -1; try { op = Integer.parseInt(sc.nextLine()); } catch (Exception ignore) {}
            switch (op) {
                case 1: {
                    System.out.print("Nuevo nombre: ");
                    String nn = sc.nextLine();
                    if (!Validaciones.esNombreApellidoValido(nn)) { System.out.println("Nombre invalido."); break; }
                    cliente.setNombre(nn);
                    gestor.guardarClientes();
                    System.out.println("Nombre actualizado.");
                    break;
                }
                case 2: {
                    System.out.print("Nuevo apellido: ");
                    String na = sc.nextLine();
                    if (!Validaciones.esNombreApellidoValido(na)) { System.out.println("Apellido invalido."); break; }
                    cliente.setApellido(na);
                    gestor.guardarClientes();
                    System.out.println("Apellido actualizado.");
                    break;
                }
                case 3: {
                    System.out.print("Nuevo telefono: ");
                    String nt = sc.nextLine();
                    cliente.setTelefono(nt);
                    gestor.guardarClientes();
                    System.out.println("Telefono actualizado.");
                    break;
                }
                case 4: {
                    System.out.print("Nuevo email: ");
                    String ne = sc.nextLine();
                    if (!Validaciones.esEmailValido(ne)) { System.out.println("Email invalido."); break; }
                    cliente.setEmail(ne);
                    gestor.guardarClientes();
                    System.out.println("Email actualizado.");
                    break;
                }
                case 5: {
                    System.out.print("Nuevo DNI: ");
                    String nd = sc.nextLine();
                    if (!Validaciones.esDniValido(nd)) { System.out.println("DNI invalido."); break; }
                    // validar unico entre clientes
                    for (Cliente c : gestor.getClientes().listar()) {
                        if (!c.equals(cliente) && c.getDni().equalsIgnoreCase(nd)) {
                            System.out.println("DNI ya registrado por otro cliente.");
                            nd = null; break;
                        }
                    }
                    if (nd == null) break;
                    cliente.setDni(nd);
                    gestor.guardarClientes();
                    System.out.println("DNI actualizado.");
                    break;
                }
                case 6: {
                    try {
                        Direccion nueva = cargarDireccion(sc);
                        cliente.setDireccion(nueva);
                        gestor.guardarClientes();
                        System.out.println("Direccion actualizada.");
                    } catch (Exception e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    break;
                }
                case 7: loop = false; break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    private void reasignarContratacion(Cliente cliente, GestorOficios gestor) {
        // Listar contrataciones del cliente que no tienen empleado asignado
        List<Contrataciones> mias = gestor.obtenerContratacionesDeCliente(cliente);
        java.util.List<Contrataciones> pendientes = new java.util.ArrayList<>();
        for (Contrataciones c : mias) {
            if (c.getEmpleado() == null) {
                pendientes.add(c);
            }
        }
        if (pendientes.isEmpty()) {
            System.out.println("No tiene contrataciones pendientes de asignacion.");
            return;
        }

        System.out.println("\n--- CONTRATACIONES PENDIENTES ---");
        for (Contrataciones c : pendientes) {
            String nombreOficio = (c.getOficio() == null) ? "(sin oficio)" : c.getOficio().getNombre();
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Oficio: " + nombreOficio);
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el ID de la contratacion a reasignar: ");
        String id = sc.nextLine();
        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(id);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontro una contratacion con ese ID.");
            return;
        }

        if (seleccionada.getCliente() == null || !seleccionada.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
            System.out.println("La contratacion indicada no pertenece a usted.");
            return;
        }
        if (seleccionada.getEmpleado() != null) {
            System.out.println("La contratacion ya tiene un empleado asignado.");
            return;
        }

        // Validar que el ID ingresado corresponda a una pendiente listada
        boolean esPendienteListado = false;
        for (Contrataciones c : pendientes) {
            if (c.getIdServicio().equalsIgnoreCase(id)) { esPendienteListado = true; break; }
        }
        if (!esPendienteListado) {
            System.out.println("El ID ingresado no corresponde a una contratacion pendiente listada.");
            return;
        }

        java.time.LocalDate fecha = seleccionada.getFecha();
        Oficio oficio = seleccionada.getOficio();
        if (oficio == null) {
            System.out.println("La contratacion no tiene un oficio definido.");
            return;
        }

        // Buscar empleados del oficio disponibles en esa fecha
        List<Empleado> candidatos = gestor.obtenerEmpleadosPorOficio(oficio);
        java.util.List<Empleado> disponibles = new java.util.ArrayList<>();
        for (Empleado e : candidatos) {
            if (e.estaDisponible(fecha)) {
                disponibles.add(e);
            }
        }
        if (disponibles.isEmpty()) {
            System.out.println("No hay empleados disponibles para ese oficio en la fecha indicada.");
            return;
        }
        // Ordenar por reputaciÃ³n descendente
        disponibles.sort(java.util.Comparator.comparingDouble(Empleado::getReputacion).reversed());

        System.out.println("Empleados disponibles para " + fecha + " (ordenados por reputacion):");
        for (Empleado e : disponibles) {
            System.out.println("- " + e.getNombre() + " " + e.getApellido() + " | DNI: " + e.getDni() + " | Reputacion: " + String.format("%.2f", e.getReputacion()));
        }

        System.out.print("Ingrese el DNI del empleado elegido: ");
        String dniEmpleado = sc.nextLine();
        Empleado elegido;
        try {
            elegido = gestor.buscarEmpleadoEnLista(dniEmpleado);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }
        if (!elegido.tieneOficio(oficio)) {
            System.out.println("El empleado no corresponde al oficio de la contratacion.");
            return;
        }
        if (!elegido.estaDisponible(fecha)) {
            System.out.println("El empleado no esta¡ disponible ese dia.");
            return;
        }

        // Validar que el DNI elegido pertenezca a la lista mostrada
        boolean esDeLaLista = false;
        for (Empleado e : disponibles) {
            if (e.getDni().equalsIgnoreCase(dniEmpleado)) { esDeLaLista = true; break; }
        }
        if (!esDeLaLista) {
            System.out.println("El DNI ingresado no pertenece a los empleados listados como disponibles.");
            return;
        }

        try {
            // Usar reasignacion que no duplica en repositorio y limpia notificacion
            gestor.asignarEmpleadoAContratacion(elegido, seleccionada, fecha);
            System.out.println("Reasignacion realizada con exito.");
        } catch (FechaInvalidaException | EmpleadoNoDisponibleException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void calificarServicio(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> contratacionesCliente = gestor.obtenerContratacionesDeCliente(cliente);
        if (contratacionesCliente.isEmpty()) {
            System.out.println("No hay contrataciones registradas.");
            return;
        }

        // Mostrar solo contrataciones calificables: con empleado asignado, fecha hoy o pasada y no canceladas/rechazadas
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.util.List<Contrataciones> calificables = new java.util.ArrayList<>();
        for (Contrataciones c : contratacionesCliente) {
            String estado = c.getEstado();
            boolean activa = (estado == null || (!"CANCELADO".equalsIgnoreCase(estado) && !"RECHAZADO".equalsIgnoreCase(estado)));
            if (c.getEmpleado() != null && c.getFecha() != null && !c.getFecha().isAfter(hoy) && activa) {
                calificables.add(c);
            }
        }
        if (calificables.isEmpty()) {
            System.out.println("No tiene contrataciones calificables por el momento.");
            return;
        }
        System.out.println("Contrataciones para calificar:");
        for (Contrataciones c : calificables) {
            System.out.println("- ID " + c.getIdServicio() + ": '" + c.getDescripcion() + "' Empleado: " + c.getEmpleado().getNombre() + " " + c.getEmpleado().getApellido() + "  Fecha " + c.getFecha() + " | Estado: " + (c.getEstado()==null?"(sin estado)":c.getEstado()) + " | Precio: " + (c.getPrecio()==null?"(sin precio)":String.format("$%.2f", c.getPrecio())));
        }

        // si es la fecha de hoy o de ayer funciona, fechas futuras no
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el ID del servicio a calificar (ej. SRV1): ");
        String idServicio = sc.nextLine();

        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(idServicio);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontro la contratacion con ese ID.");
            return;
        }

        try {
            validarContratacionCalificable(cliente, seleccionada);
        } catch (Exceptions.ServicioNoCalificableException e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        System.out.print("Puntaje (1-5, permite decimales con coma o punto): ");
        String puntajeTexto = sc.nextLine();
        double puntaje;
        try {
            String normalizado = puntajeTexto.trim().replace(',', '.');
            puntaje = Double.parseDouble(normalizado);
        } catch (NumberFormatException ex) {
            System.out.println("Puntaje invalido.");
            return;
        }
        System.out.print("Comentario: ");
        String comentario = sc.nextLine();

        seleccionada.getEmpleado().agregarValoracion(cliente, puntaje, comentario, seleccionada.getIdServicio());
        // Persistir reputacion y calificaciones
        gestor.guardarEmpleados();
    }

    // parte de que no se pueda contratar si no se completo el servicio
    private void validarContratacionCalificable(Cliente cliente, Contrataciones c) throws Exceptions.ServicioNoCalificableException {
        if (c == null || c.getCliente() == null || !c.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
            throw new Exceptions.ServicioNoCalificableException("Solo puede calificar sus contrataciones.");
        }
        if (c.getEmpleado() == null) {
            throw new Exceptions.ServicioNoCalificableException("La contratacion no tiene empleado asignado.");
        }
        if (c.getFecha() == null || c.getFecha().isAfter(java.time.LocalDate.now())) {
            throw new Exceptions.ServicioNoCalificableException("Solo puede calificar contrataciones realizadas (hoy o anteriores).");
        }
    }
}




