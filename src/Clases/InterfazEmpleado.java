package Clases;

import Exceptions.PersonaNoEncontradaException;
import Exceptions.NumeroInvalidoException;

import java.util.List;
import java.util.Scanner;

public class InterfazEmpleado {

    public Empleado registrarEmpleado(GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- REGISTRO EMPLEADO ---");
        System.out.print("DNI: ");
        String dni = sc.nextLine();
        try {
            // Si ya existe, no continuar
            if (gestor.buscarEmpleadoEnLista(dni) != null) {
                System.out.println("El DNI ya esta registrado como Empleado.");
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

        // Seleccion de oficios existente(s) o creacion de uno nuevo si no aparece en la lista
        java.util.List<Oficio> existentes = gestor.getOficios().listar();
        // Ordenar por nombre normalizado (ignora acentos y mayusculas)
        if (existentes != null) {
            existentes.sort(java.util.Comparator.comparing(o -> Clases.Validaciones.normalizarNombreOficio(o.getNombre()), String.CASE_INSENSITIVE_ORDER));
        }
        java.util.List<Oficio> oficiosSeleccionados = new java.util.ArrayList<>();

        if (!existentes.isEmpty()) {
            System.out.println("\nOficios disponibles (seleccione por numero):");
            for (int i = 0; i < existentes.size(); i++) {
                System.out.println((i + 1) + " - " + existentes.get(i).getNombre());
            }
            System.out.println("0 - Crear un oficio nuevo");
            System.out.print("Ingrese numeros separados por coma (ej: 1,3) o 0 para crear: ");
            String seleccion = sc.nextLine();
            if (seleccion != null && !seleccion.trim().isEmpty()) {
                for (String parte : seleccion.split(",")) {
                    String t = parte.trim();
                    if (t.isEmpty()) continue;
                    if ("0".equals(t)) {
                        // Crear uno nuevo
                        System.out.print("Nombre del nuevo oficio: ");
                        String nombreNuevo = sc.nextLine();
                        Oficio creado = gestor.obtenerOcrearOficio(nombreNuevo);
                        if (creado != null && !oficiosSeleccionados.contains(creado)) {
                            oficiosSeleccionados.add(creado);
                        }
                        continue;
                    }
                    try {
                        int idx = Integer.parseInt(t);
                        if (idx >= 1 && idx <= existentes.size()) {
                            Oficio o = existentes.get(idx - 1);
                            if (o != null && !oficiosSeleccionados.contains(o)) {
                                oficiosSeleccionados.add(o);
                            }
                        }
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        }

        // Si no selecciono ninguno de la lista, ofrecer crear uno nuevo por nombre
        if (oficiosSeleccionados.isEmpty()) {
            System.out.print("No selecciono oficios. Ingrese nombre de oficio para crear (o ENTER para cancelar): ");
            String nombreNuevo = sc.nextLine();
            if (nombreNuevo == null || nombreNuevo.trim().isEmpty()) {
                System.out.println("Debe seleccionar o crear al menos un oficio.");
                return null;
            }
            Oficio creado = gestor.obtenerOcrearOficio(nombreNuevo);
            if (creado != null) {
                oficiosSeleccionados.add(creado);
            } else {
                System.out.println("Nombre de oficio invalido.");
                return null;
            }
        }
        Direccion direccion;
        try {
            direccion = cargarDireccion(sc);
        } catch (Exceptions.CampoVacioException | NumeroInvalidoException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }

        Empleado empleado = new Empleado(dni, nombre, apellido, email, telefono, password, null);
        for (Oficio o : oficiosSeleccionados) {
            empleado.agregarOficio(o);
        }
        empleado.setDireccion(direccion);
        gestor.registrarEmpleado(empleado);
        System.out.println("Empleado registrado correctamente.");
        return empleado;
    }

    public Empleado iniciarSesion(GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- LOGIN EMPLEADO ---");
        System.out.print("DNI: ");
        String dni = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        try {
            return gestor.iniciarSesionEmpleado(dni, password);
        } catch (PersonaNoEncontradaException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public void menuEmpleado(Empleado empleado, GestorOficios gestor) {
        if (empleado == null) {
            return;
        }
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        while (seguir == 's') {
            System.out.println("\n--- MENU EMPLEADO ---");
            System.out.println("1 - Ver agenda");
            System.out.println("2 - Ver historial de acciones");
            System.out.println("3 - Ver calificaciones");
            System.out.println("4 - Aceptar trabajo y fijar precio");
            System.out.println("5 - Rechazar trabajo");
            System.out.println("6 - Gestionar oficios");
            System.out.println("7 - Modificar mis datos");
            System.out.println("8 - Salir");
            System.out.print("Opcion: ");
            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1:
                    mostrarAgenda(empleado, gestor);
                    break;
                case 2:
                    empleado.mostrarHistorial();
                    break;
                case 3:
                    mostrarCalificaciones(empleado);
                    break;
                case 4:
                    aceptarTrabajoFijarPrecio(empleado, gestor);
                    break;
                case 5:
                    rechazarTrabajo(empleado, gestor);
                    break;
                case 6:
                    gestionarOficios(empleado, gestor);
                    break;
                case 7:
                    modificarDatosEmpleado(empleado, gestor);
                    break;
                case 8:
                    seguir = 'n';
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void mostrarAgenda(Empleado empleado, GestorOficios gestor) {
        List<Contrataciones> lista = gestor.obtenerContratacionesDeEmpleado(empleado);
        if (lista.isEmpty()) {
            System.out.println("No tiene servicios asignados.");
            return;
        }
        System.out.println("\n--- AGENDA ---");
        for (Contrataciones contratacion : lista) {
            System.out.println(
                    contratacion.getFecha() + " - " +
                    contratacion.getDescripcion() +
                    " | ID: " + contratacion.getIdServicio() +
                    " | Estado: " + (contratacion.getEstado()==null?"(sin estado)":contratacion.getEstado()) +
                    " | Precio: " + (contratacion.getPrecio()==null?"(sin precio)":String.format("$%.2f", contratacion.getPrecio()))
            );
        }
    }

    private void mostrarCalificaciones(Empleado empleado) {
        List<Calificacion> calificaciones = empleado.getCalificaciones();
        if (calificaciones.isEmpty()) {
            System.out.println("Sin calificaciones registradas.");
            return;
        }
        System.out.println("\n--- CALIFICACIONES ---");
        for (Calificacion calificacion : calificaciones) {
            System.out.println(calificacion);
        }
        System.out.println("Reputacion promedio: " + String.format("%.2f", empleado.getReputacion()));
    }

    private int leerEntero(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { return -1; }
    }

    private void gestionarOficios(Empleado empleado, GestorOficios gestor) {
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        while (seguir == 's') {
            System.out.println("\n--- GESTIONAR OFICIOS ---");
            System.out.println("Oficios actuales:");
            if (empleado.getOficios().isEmpty()) {
                System.out.println("(sin oficios)");
            } else {
                for (Oficio o : empleado.getOficios()) {
                    System.out.println("- " + o.getNombre() + " (" + o.getId() + ")");
                }
            }
            System.out.println("1 - Agregar oficio");
            System.out.println("2 - Quitar oficio");
            System.out.println("3 - Volver");
            System.out.print("Opcion: ");
            int op = -1; try { op = Integer.parseInt(sc.nextLine()); } catch (Exception ignore) {}
            switch (op) {
                case 1: {
                    System.out.print("Nombre del nuevo oficio: ");
                    String nombre = sc.nextLine();
                    Oficio oficio = gestor.obtenerOcrearOficio(nombre);
                    if (oficio == null) {
                        System.out.println("Nombre de oficio invalido.");
                        break;
                    }
                    if (empleado.tieneOficio(oficio)) {
                        System.out.println("Ya posee ese oficio.");
                        break;
                    }
                    empleado.agregarOficio(oficio);
                    gestor.guardarEmpleados();
                    System.out.println("Oficio agregado.");
                    break;
                }
                case 2: {
                    System.out.print("Ingrese el ID del oficio a quitar: ");
                    String id = sc.nextLine();
                    Oficio of = null;
                    for (Oficio o : empleado.getOficios()) { if (o.getId().equalsIgnoreCase(id)) { of = o; break; } }
                    if (of == null) {
                        System.out.println("El ID no corresponde a un oficio listado.");
                        break;
                    }
                    if (empleado.quitarOficio(of)) {
                        gestor.guardarEmpleados();
                        System.out.println("Oficio quitado.");
                    } else {
                        System.out.println("No se pudo quitar el oficio.");
                    }
                    break;
                }
                case 3: {
                    seguir = 'n';
                    break;
                }
                default: {
                    System.out.println("Opcion invalida.");
                }
            }
        }
    }

    private void modificarDatosEmpleado(Empleado empleado, GestorOficios gestor) {
        if (empleado == null) return;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("\n--- MODIFICAR MIS DATOS (EMPLEADO) ---");
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
                    empleado.setNombre(nn);
                    gestor.guardarEmpleados();
                    System.out.println("Nombre actualizado.");
                    break;
                }
                case 2: {
                    System.out.print("Nuevo apellido: ");
                    String na = sc.nextLine();
                    if (!Validaciones.esNombreApellidoValido(na)) { System.out.println("Apellido invalido."); break; }
                    empleado.setApellido(na);
                    gestor.guardarEmpleados();
                    System.out.println("Apellido actualizado.");
                    break;
                }
                case 3: {
                    System.out.print("Nuevo telefono: ");
                    String nt = sc.nextLine();
                    empleado.setTelefono(nt);
                    gestor.guardarEmpleados();
                    System.out.println("Telefono actualizado.");
                    break;
                }
                case 4: {
                    System.out.print("Nuevo email: ");
                    String ne = sc.nextLine();
                    if (!Validaciones.esEmailValido(ne)) { System.out.println("Email invalido."); break; }
                    empleado.setEmail(ne);
                    gestor.guardarEmpleados();
                    System.out.println("Email actualizado.");
                    break;
                }
                case 5: {
                    System.out.print("Nuevo DNI: ");
                    String nd = sc.nextLine();
                    if (!Validaciones.esDniValido(nd)) { System.out.println("DNI invalido."); break; }
                    for (Empleado e : gestor.getEmpleados().listar()) {
                        if (!e.equals(empleado) && e.getDni().equalsIgnoreCase(nd)) {
                            System.out.println("DNI ya registrado por otro empleado.");
                            nd = null; break;
                        }
                    }
                    if (nd == null) break;
                    empleado.setDni(nd);
                    gestor.guardarEmpleados();
                    System.out.println("DNI actualizado.");
                    break;
                }
                case 6: {
                    try {
                        Direccion nueva = cargarDireccion(sc);
                        empleado.setDireccion(nueva);
                        gestor.guardarEmpleados();
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

    private void rechazarTrabajo(Empleado empleado, GestorOficios gestor) {
        List<Contrataciones> lista = gestor.obtenerContratacionesDeEmpleado(empleado);
        if (lista.isEmpty()) {
            System.out.println("No tiene servicios asignados.");
            return;
        }
        // Filtrar solo contrataciones de hoy o futuras
        java.util.List<Contrataciones> candidatas = new java.util.ArrayList<>();
        java.time.LocalDate hoy = java.time.LocalDate.now();
        for (Contrataciones c : lista) {
            if (c.getFecha() != null && !c.getFecha().isBefore(hoy)) {
                candidatas.add(c);
            }
        }
        if (candidatas.isEmpty()) {
            System.out.println("No tiene servicios de hoy o futuros para rechazar.");
            return;
        }
        System.out.println("\n--- RECHAZAR TRABAJO ---");
        for (Contrataciones c : candidatas) {
            String clienteStr = (c.getCliente() == null) ? "(sin cliente)" : (c.getCliente().getNombre() + " " + c.getCliente().getApellido() + " (" + c.getCliente().getDni() + ")");
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Cliente: " + clienteStr);
        }
        System.out.print("Ingrese el ID del servicio a rechazar: ");
        String id = new java.util.Scanner(System.in).nextLine();
        // Validar que el ID ingresado este en la lista mostrada
        boolean pertenece = false;
        for (Contrataciones c : candidatas) {
            if (c.getIdServicio().equalsIgnoreCase(id)) { pertenece = true; break; }
        }
        if (!pertenece) {
            System.out.println("El ID ingresado no corresponde a los servicios listados para rechazo.");
            return;
        }
        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(id);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontro una contratacion con ese ID.");
            return;
        }
        if (seleccionada.getEmpleado() == null || !seleccionada.getEmpleado().getDni().equalsIgnoreCase(empleado.getDni())) {
            System.out.println("El servicio indicado no pertenece a su agenda.");
            return;
        }
        try {
            gestor.rechazarContratacion(empleado, seleccionada);
            System.out.println("Trabajo rechazado. La contratacion quedo sin empleado asignado.");
            System.out.println("Se notificara al cliente de este cambio.");
        } catch (Exceptions.TrabajoYaRealizadoException ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private void aceptarTrabajoFijarPrecio(Empleado empleado, GestorOficios gestor) {
        List<Contrataciones> lista = gestor.obtenerContratacionesDeEmpleado(empleado);
        if (lista.isEmpty()) {
            System.out.println("No tiene servicios asignados.");
            return;
        }
        java.time.LocalDate hoy = java.time.LocalDate.now();
        List<Contrataciones> candidatas = new java.util.ArrayList<>();
        for (Contrataciones c : lista) {
            if (c.getFecha() != null && !c.getFecha().isBefore(hoy) && (c.getEstado() == null || !"ACEPTADO".equalsIgnoreCase(c.getEstado()))) {
                candidatas.add(c);
            }
        }
        if (candidatas.isEmpty()) {
            System.out.println("No hay trabajos pendientes para aceptar.");
            return;
        }
        System.out.println("\n--- ACEPTAR TRABAJO Y FIJAR PRECIO ---");
        for (Contrataciones c : candidatas) {
            System.out.println("- ID " + c.getIdServicio() + " | Fecha " + c.getFecha() + " | '" + c.getDescripcion() + "' | Estado: " + c.getEstado());
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el ID del servicio a aceptar: ");
        String id = sc.nextLine();
        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(id);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontro una contratacion con ese ID.");
            return;
        }
        if (seleccionada.getEmpleado() == null || !seleccionada.getEmpleado().getDni().equalsIgnoreCase(empleado.getDni())) {
            System.out.println("El servicio indicado no pertenece a su agenda.");
            return;
        }
        System.out.print("Ingrese el precio (use coma o punto para decimales): ");
        String ptxt = sc.nextLine();
        double precio;
        try { precio = Double.parseDouble(ptxt.trim().replace(',', '.')); } catch (Exception e) { System.out.println("Precio invalido."); return; }
        if (precio <= 0) { System.out.println("El precio debe ser mayor a 0."); return; }
        gestor.aceptarContratacion(empleado, seleccionada, precio);
        empleado.registrarAccion("Acepto contratacion " + seleccionada.getIdServicio() + " con precio " + String.format("%.2f", precio));
        System.out.println("Trabajo aceptado y precio fijado.");
    }

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
            // pasa el numero de texto a formato entero
            numero = Integer.parseInt(numeroTexto.trim());
            if (numero <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new NumeroInvalidoException("El numero de la direccion es invalido.");
        }
        return new Direccion(ciudad, calle, numero);
    }


}
