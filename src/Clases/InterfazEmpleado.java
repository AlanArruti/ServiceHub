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
                System.out.println("El DNI ya está registrado como Empleado.");
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

        System.out.print("Nombre del oficio: ");
        String nombreOficio = sc.nextLine();
        Oficio oficio = gestor.obtenerOcrearOficio(nombreOficio);
        Direccion direccion;
        try {
            direccion = cargarDireccion(sc);
        } catch (Exceptions.CampoVacioException | NumeroInvalidoException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }

        Empleado empleado = new Empleado(dni, nombre, apellido, email, telefono, password, oficio);
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
            System.out.println("4 - Salir");
            System.out.print("Opcion: ");
            int opcion = sc.nextInt();
            sc.nextLine();

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
                    seguir = 'n';
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

            if (seguir == 's') {
                System.out.print("Volver al menu? (s/n): ");
                seguir = leerContinuar(sc);
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
            System.out.println(contratacion.getFecha() + " - " + contratacion.getDescripcion());
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
            if (numero <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new NumeroInvalidoException("El numero de la direccion es invalido.");
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
}
