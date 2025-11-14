package Clases;

import java.util.List;
import java.util.Scanner;

public class InterfazAdmin {

    private static final String ADMIN_PASSWORD = "admin";

    public boolean iniciarSesionAdmin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- LOGIN ADMINISTRADOR ---");
        System.out.print("Password: ");
        String pwd = sc.nextLine();
        return ADMIN_PASSWORD.equals(pwd);
    }

    public void menuAdmin(GestorOficios gestor) {
        if (gestor == null) return;
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        while (seguir == 's') {
            System.out.println("\n--- MENU ADMIN ---");
            System.out.println("1 - Eliminar cliente");
            System.out.println("2 - Eliminar empleado");
            System.out.println("3 - Modificar cliente");
            System.out.println("4 - Modificar empleado");
            System.out.println("5 - Salir");
            System.out.print("Opcion: ");
            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1:
                    eliminarCliente(gestor, sc);
                    break;
                case 2:
                    eliminarEmpleado(gestor, sc);
                    break;
                case 3:
                    modificarCliente(gestor, sc);
                    break;
                case 4:
                    modificarEmpleado(gestor, sc);
                    break;
                case 5:
                    seguir = 'n';
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

            if (seguir == 's') {
                System.out.print("Volver al menu admin? (s/n): ");
                String r = sc.nextLine();
                if (!r.isEmpty()) seguir = Character.toLowerCase(r.charAt(0));
            }
        }
    }

    private void eliminarCliente(GestorOficios gestor, Scanner sc) {
        List<Cliente> lista = gestor.getClientes().listar();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        imprimirPersonas(lista);
        System.out.print("ID a eliminar: ");
        String id = sc.nextLine();
        try {
            Cliente c = gestor.getClientes().buscarPorId(id);
            System.out.println("Se eliminara: " + resumenPersona(c));
            if (confirmar(sc)) {
                gestor.getClientes().eliminar(c);
                gestor.guardarClientes();
                System.out.println("Cliente eliminado.");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (Exception e) {
            System.out.println("ID invalido o no encontrado.");
        }
    }

    private void eliminarEmpleado(GestorOficios gestor, Scanner sc) {
        List<Empleado> lista = gestor.getEmpleados().listar();
        if (lista.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        imprimirPersonas(lista);
        System.out.print("ID a eliminar: ");
        String id = sc.nextLine();
        try {
            Empleado e = gestor.getEmpleados().buscarPorId(id);
            System.out.println("Se eliminara: " + resumenPersona(e));
            if (confirmar(sc)) {
                gestor.getEmpleados().eliminar(e);
                gestor.guardarEmpleados();
                System.out.println("Empleado eliminado.");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (Exception ex) {
            System.out.println("ID invalido o no encontrado.");
        }
    }

    private void modificarCliente(GestorOficios gestor, Scanner sc) {
        List<Cliente> lista = gestor.getClientes().listar();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        imprimirPersonas(lista);
        System.out.print("ID a modificar: ");
        String id = sc.nextLine();
        try {
            Cliente c = gestor.getClientes().buscarPorId(id);
            modificarPersonaComun(gestor, sc, c, true);
        } catch (Exception e) {
            System.out.println("ID invalido o no encontrado.");
        }
    }

    private void modificarEmpleado(GestorOficios gestor, Scanner sc) {
        List<Empleado> lista = gestor.getEmpleados().listar();
        if (lista.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        imprimirPersonas(lista);
        System.out.print("ID a modificar: ");
        String id = sc.nextLine();
        try {
            Empleado e = gestor.getEmpleados().buscarPorId(id);
            modificarPersonaComun(gestor, sc, e, false);
        } catch (Exception ex) {
            System.out.println("ID invalido o no encontrado.");
        }
    }

    private void modificarPersonaComun(GestorOficios gestor, Scanner sc, Persona p, boolean esCliente) {
        if (p == null) return;
        boolean seguir = true;
        while (seguir) {
            System.out.println("\nModificar: " + resumenPersona(p));
            System.out.println("1 - Nombre");
            System.out.println("2 - Apellido");
            System.out.println("3 - Telefono");
            System.out.println("4 - Email");
            System.out.println("5 - Password");
            System.out.println("6 - DNI");
            System.out.println("7 - Direccion");
            System.out.println("8 - Volver");
            System.out.print("Opcion: ");
            int op = leerEntero(sc);

            switch (op) {
                case 1:
                    System.out.print("Nuevo nombre: ");
                    String nn = sc.nextLine();
                    System.out.println("Nombre: " + p.getNombre() + " -> " + nn);
                    if (confirmar(sc)) p.setNombre(nn);
                    break;
                case 2:
                    System.out.print("Nuevo apellido: ");
                    String na = sc.nextLine();
                    System.out.println("Apellido: " + p.getApellido() + " -> " + na);
                    if (confirmar(sc)) p.setApellido(na);
                    break;
                case 3:
                    System.out.print("Nuevo telefono: ");
                    String nt = sc.nextLine();
                    System.out.println("Telefono: " + p.getTelefono() + " -> " + nt);
                    if (confirmar(sc)) p.setTelefono(nt);
                    break;
                case 4:
                    System.out.print("Nuevo email: ");
                    String ne = sc.nextLine();
                    System.out.println("Email: " + p.getEmail() + " -> " + ne);
                    if (confirmar(sc)) p.setEmail(ne);
                    break;
                case 5:
                    System.out.print("Nueva password: ");
                    String np = sc.nextLine();
                    System.out.println("Password: (oculta) -> (oculta)");
                    if (confirmar(sc)) p.setPassword(np);
                    break;
                case 6:
                    System.out.print("Nuevo DNI: ");
                    String nd = sc.nextLine();
                    if (!validarDniDisponible(gestor, p, nd, esCliente)) {
                        System.out.println("DNI ya registrado. Operacion cancelada.");
                        break;
                    }
                    System.out.println("DNI: " + p.getDni() + " -> " + nd);
                    if (confirmar(sc)) p.setDni(nd);
                    break;
                case 7:
                    Direccion actual = (p instanceof Cliente) ? ((Cliente) p).getDireccion() : (p instanceof Empleado) ? ((Empleado) p).getDireccion() : null;
                    System.out.println("Direccion actual: " + direccionToString(actual));
                    Direccion nueva = construirDireccion(sc);
                    System.out.println("Direccion: " + direccionToString(actual) + " -> " + direccionToString(nueva));
                    if (confirmar(sc)) {
                        if (p instanceof Cliente) ((Cliente) p).setDireccion(nueva);
                        if (p instanceof Empleado) ((Empleado) p).setDireccion(nueva);
                    }
                    break;
                case 8:
                    seguir = false;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

            // Guardar luego de cada cambio confirmado
            if (esCliente) gestor.guardarClientes(); else gestor.guardarEmpleados();
        }
    }

    private boolean validarDniDisponible(GestorOficios gestor, Persona p, String nuevoDni, boolean esCliente) {
        if (nuevoDni == null || nuevoDni.trim().isEmpty()) return false;
        if (nuevoDni.equalsIgnoreCase(p.getDni())) return true;
        if (esCliente) {
            for (Cliente c : gestor.getClientes().listar()) {
                if (!c.equals(p) && c.getDni().equalsIgnoreCase(nuevoDni)) return false;
            }
        } else {
            for (Empleado e : gestor.getEmpleados().listar()) {
                if (!e.equals(p) && e.getDni().equalsIgnoreCase(nuevoDni)) return false;
            }
        }
        return true;
    }

    private Direccion construirDireccion(Scanner sc) {
        System.out.print("Ciudad: ");
        String ciudad = sc.nextLine();
        System.out.print("Calle: ");
        String calle = sc.nextLine();
        Integer numero = null;
        while (numero == null) {
            System.out.print("Numero: ");
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n <= 0) {
                    System.out.println("El numero debe ser positivo.");
                } else {
                    numero = n;
                }
            } catch (Exception e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
        return new Direccion(ciudad, calle, numero);
    }

    private void imprimirPersonas(List<? extends Persona> personas) {
        System.out.println("\nID | DNI | Nombre | Apellido | Email | Tel");
        for (Persona p : personas) {
            System.out.println(p.getIdentificador() + " | " + p.getDni() + " | " + p.getNombre() + " | " + p.getApellido() + " | " + p.getEmail() + " | " + p.getTelefono());
        }
    }

    private String resumenPersona(Persona p) {
        if (p == null) return "(desconocido)";
        return "[ID=" + p.getIdentificador() + ", DNI=" + p.getDni() + ", Nombre=" + p.getNombre() + " " + p.getApellido() + "]";
    }

    private String direccionToString(Direccion d) {
        return d == null ? "(sin direccion)" : d.toString();
    }

    private boolean confirmar(Scanner sc) {
        System.out.print("Confirmar? (s/n): ");
        String r = sc.nextLine();
        return !r.isEmpty() && Character.toLowerCase(r.charAt(0)) == 's';
    }

    private int leerEntero(Scanner sc) {
        try { return Integer.parseInt(sc.nextLine()); } catch (Exception e) { return -1; }
    }
}
