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
            throw new NumeroInvalidoException("El número de la dirección es inválido.");
        }
        return new Direccion(ciudad, calle, numero);
    }

    private char leerContinuar(Scanner sc) {
        String respuesta = sc.nextLine().toLowerCase();
        if (respuesta.isEmpty()) {
            return 'n';
        }
        return respuesta.charAt(0);
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
            // Si ya existe, no continuar
            if (gestor.buscarClienteEnLista(dni) != null) {
                System.out.println("El DNI ya está registrado como cliente.");
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
        //gestor.mostrarOficios();
        System.out.print("Ingrese el oficio deseado: ");
        String nombreOficio = sc.nextLine();
        Oficio oficio = gestor.obtenerOcrearOficio(nombreOficio);
        if (oficio == null) {
            System.out.println("Debe ingresar un oficio valido.");
            return;
        }

        List<Empleado> disponibles = gestor.obtenerEmpleadosPorOficio(oficio);
        if (disponibles.isEmpty()) {
            System.out.println("No hay empleados registrados para ese oficio.");
            return;
        }

        System.out.println("Empleados disponibles:");
        for (Empleado empleado : disponibles) {
            System.out.println("- " + empleado.getNombre() + " " + empleado.getApellido() + " | DNI: " + empleado.getDni());
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

        System.out.print("Ingrese la fecha (yyyy-MM-dd): ");
        String fechaTexto = sc.nextLine();
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto);
        } catch (Exception e) {
            System.out.println("Formato de fecha invalido.");
            return;
        }

        if (!empleadoSeleccionado.estaDisponible(fecha)) {
            System.out.println("El empleado no esta disponible ese dia.");
            return;
        }

        System.out.print("Descripcion del servicio: ");
        String descripcion = sc.nextLine();

        Contrataciones contratacion = gestor.crearContratacion(cliente, oficio, descripcion, fecha);
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
        for (Contrataciones contratacion : lista) {
            System.out.println(contratacion);
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
            System.out.println("4 - Calificar servicio");
            System.out.println("5 - Salir");
            System.out.print("Opcion: ");
            int opcion = sc.nextInt();
            sc.nextLine();

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
                    calificarServicio(cliente, gestor);
                    break;
                }
                case 5:{
                    seguir = 'n';
                    break;
                }
                default:{
                    System.out.println("Opcion invalida.");
                }

            }

            if (seguir == 's') {
                System.out.print("Volver al menu? (s/n): ");
                seguir = leerContinuar(sc);
            }
        }
    }

    private void calificarServicio(Cliente cliente, GestorOficios gestor) {
        List<Contrataciones> contratacionesCliente = gestor.obtenerContratacionesDeCliente(cliente);
        if (contratacionesCliente.isEmpty()) {
            System.out.println("No hay contrataciones registradas.");
            return;
        }

        System.out.println("Contrataciones para calificar:");
        for (Contrataciones c : contratacionesCliente) {
            if (c.getEmpleado() != null) {
                System.out.println("- ID " + c.getIdServicio() + ": '" + c.getDescripcion() + "' – Empleado: " + c.getEmpleado().getNombre() + " " + c.getEmpleado().getApellido() + " – Fecha " + c.getFecha());
            } else {
                System.out.println("- ID " + c.getIdServicio() + ": '" + c.getDescripcion() + "' – Empleado: Sin asignar – Fecha " + c.getFecha());
            }
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el ID del servicio a calificar (ej. SRV1): ");
        String idServicio = sc.nextLine();

        Contrataciones seleccionada;
        try {
            seleccionada = gestor.buscarContratacionPorId(idServicio);
        } catch (Exceptions.PersonaNoEncontradaException e) {
            System.out.println("ERROR: No se encontró la contratación con ese ID.");
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
            System.out.println("Puntaje inválido.");
            return;
        }
        System.out.print("Comentario: ");
        String comentario = sc.nextLine();

        seleccionada.getEmpleado().agregarValoracion(cliente, puntaje, comentario);
    }

    private void validarContratacionCalificable(Cliente cliente, Contrataciones c) throws Exceptions.ServicioNoCalificableException {
        if (c == null || c.getCliente() == null || !c.getCliente().getDni().equalsIgnoreCase(cliente.getDni())) {
            throw new Exceptions.ServicioNoCalificableException("Solo puede calificar sus contrataciones.");
        }
        if (c.getEmpleado() == null) {
            throw new Exceptions.ServicioNoCalificableException("La contratación aún no tiene empleado asignado.");
        }
        if (c.getFecha() == null || c.getFecha().isAfter(java.time.LocalDate.now())) {
            throw new Exceptions.ServicioNoCalificableException("Solo puede calificar contrataciones realizadas (hoy o anteriores).");
        }
    }
}
