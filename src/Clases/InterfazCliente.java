package Clases;

import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;
import Exceptions.NumeroInvalidoException;
import Exceptions.FechaInvalidaException;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class InterfazCliente {

    private Direccion cargarDireccion(Scanner sc) throws NumeroInvalidoException {
        System.out.println("\n--- DIRECCION ---");
        System.out.print("Ciudad: ");
        String ciudad = sc.nextLine();
        System.out.print("Calle: ");
        String calle = sc.nextLine();
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
        Direccion direccion;
        try {
            direccion = cargarDireccion(sc);
        } catch (NumeroInvalidoException e) {
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
            System.out.println("4 - Salir");
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
}
