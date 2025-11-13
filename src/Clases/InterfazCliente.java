package Clases;

import Enums.Oficios;
import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.Scanner;

public class InterfazCliente {

    private Scanner sc = new Scanner(System.in);

    // Carga básica de la dirección del usuario
    // (lo uso para cuando creo un nuevo cliente)
    public Direccion cargarDireccion(){
        System.out.println("Ingrese la ciudad: ");
        String ciudad = sc.nextLine();
        System.out.println("Ingrese la calle: ");
        String calle = sc.nextLine();
        System.out.println("Ingrese el numero de la calle: ");
        int numero = sc.nextInt();
        sc.nextLine(); // limpio buffer
        return new Direccion(ciudad, calle, numero);
    }

    // Metodo que arma un Cliente completo pidiendo todos los datos por consola
    public Cliente crearUsuario(){
        System.out.println("Ingrese el dni del usuario: ");
        String dni = sc.nextLine();
        System.out.println("Ingrese el nombre del usuario: ");
        String nombre = sc.nextLine();
        System.out.println("Ingrese el apellido del usuario: ");
        String apellido = sc.nextLine();
        System.out.println("Ingrese el email del usuario: ");
        String email = sc.nextLine();
        System.out.println("Ingrese el telefono del usuario: ");
        String telefono = sc.nextLine();
        System.out.println("Ingrese la dirección del usuario: ");
        Direccion direccion = cargarDireccion();

        return new Cliente(dni, nombre, apellido, email, telefono, direccion);
    }

    // Menú para elegir oficio. Devuelve el enum.
    // Si el usuario mete mal el número, le doy otra oportunidad.
    public Oficios elegirOficio(){
        char seguir = 's';
        int opcion;

        while (seguir == 's') {
            System.out.println("INGRESE EL OFICIO DEL QUE DESEA CONOCER MAS:");
            System.out.println("1 - PLOMERO\n2 - PINTOR\n3 - ELECTRICISTA\n4 - GASISTA");
            System.out.println("5 - ALBAÑIL\n6 - CARPINTERO\n7 - HERRERO\n8 - JARDINERO");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion){
                case 1: return Oficios.PLOMERO;
                case 2: return Oficios.PINTOR;
                case 3: return Oficios.ELECTRICISTA;
                case 4: return Oficios.GASISTA;
                case 5: return Oficios.ALBAÑIL;
                case 6: return Oficios.CARPINTERO;
                case 7: return Oficios.HERRERO;
                case 8: return Oficios.JARDINERO;
                default:
                    System.out.println("Opción inválida.");
            }

            System.out.println("¿Desea volver a intentar? (s/n)");
            seguir = sc.next().charAt(0);
            sc.nextLine();
        }
        return null;
    }

    /*
        METODO CENTRAL PARA CONTRATAR (LO USO EN TODOS LOS OFICIOS)
        - Listo empleados por oficio
        - Pido DNI
        - Pido fecha
        - Verifico disponibilidad
        - Si está libre → creo la Contratación y la registro
        - Si no → le doy otra chance
    */
    private void contratarPorOficio(GestorOficios g, Oficios oficio) {

        System.out.println("\nDesea ver empleados disponibles? (s/n)");
        char seguir = sc.next().charAt(0);
        sc.nextLine();

        while (seguir == 's') {

            // Mostrar empleados por categoría
            g.mostrarEmpleadoXcategoria(oficio.name());

            System.out.println("Ingrese el DNI del empleado a contratar:");
            String dniEmpleado = sc.nextLine();

            System.out.println("Ingrese el DNI del cliente que contrata el servicio:");
            String dniCliente = sc.nextLine();

            // Buscar cliente REAL por DNI
            Cliente cliente;
            try {
                cliente = g.buscarClientePorDni(dniCliente);
            } catch (PersonaNoEncontradaException e) {
                System.out.println("ERROR: " + e.getMessage());
                return;
            }

            // Fecha del servicio (LA QUE EL CLIENTE QUIERE)
            System.out.println("Ingrese la fecha del servicio (yyyy-MM-dd):");
            String fechaStr = sc.nextLine();
            LocalDate fechaServicio = LocalDate.parse(fechaStr);

            // Ver disponibilidad del empleado
            boolean disponible = g.verSiEstaDisponible(dniEmpleado, fechaServicio);

            if (disponible) {

                System.out.println("Ingrese descripción del servicio:");
                String descripcion = sc.nextLine();

                System.out.println("Ingrese el precio del servicio:");
                double precio = sc.nextDouble();
                sc.nextLine();

                Contrataciones servicio = new Contrataciones(descripcion, precio, oficio.name(), cliente);

                // Registrar servicio al empleado con LA FECHA DEL SERVICIO
                try {
                    g.contratarEmpleado(dniEmpleado, servicio, fechaServicio);
                    System.out.println("Servicio contratado con exito");
                } catch (PersonaNoEncontradaException | EmpleadoNoDisponibleException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }

                return; // ya contrató, salimos del metodo
            }
            else {
                System.out.println("El empleado no está disponible en esa fecha.");
                System.out.println("¿Intentar con otra fecha u otro empleado? (s/n)");
                seguir = sc.next().charAt(0);
                sc.nextLine();
            }
        }

        System.out.println("Volviendo al menú principal...");
    }

    // Metodo principal que muestra la info del oficio y llama al metodo genérico
    public void verInfoOficio(GestorOficios g){
        Oficios oficio = elegirOficio();

        switch (oficio){
            case PLOMERO:
                System.out.println("Instala, repara y mantiene cañerías.");
                contratarPorOficio(g, oficio);
                break;

            case PINTOR:
                System.out.println("Pinta paredes, techos y muebles.");
                contratarPorOficio(g, oficio);
                break;

            case ELECTRICISTA:
                System.out.println("Instala y mantiene sistemas eléctricos.");
                contratarPorOficio(g, oficio);
                break;

            case GASISTA:
                System.out.println("Repara y coloca cañerías de gas.");
                contratarPorOficio(g, oficio);
                break;

            case ALBAÑIL:
                System.out.println("Construcción y refacciones.");
                contratarPorOficio(g, oficio);
                break;

            case CARPINTERO:
                System.out.println("Trabajos en madera.");
                contratarPorOficio(g, oficio);
                break;

            case HERRERO:
                System.out.println("Trabajos en metal.");
                contratarPorOficio(g, oficio);
                break;

            case JARDINERO:
                System.out.println("Mantenimiento de jardines.");
                contratarPorOficio(g, oficio);
                break;
        }
    }
}
