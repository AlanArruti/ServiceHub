package Clases;

import java.util.Scanner;

public class InterfazGeneral {

    private GestorOficios gestor;
    private InterfazCliente interCliente;
    private InterfazEmpleado interEmpleado;
    private InterfazAdmin interAdmin;

    public InterfazGeneral(GestorOficios gestor) {
        this.gestor = gestor;
        this.interCliente = new InterfazCliente();
        this.interEmpleado = new InterfazEmpleado();
        this.interAdmin = new InterfazAdmin();
    }

    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        while (seguir == 's') {
            System.out.println("\n--- SERVICE HUB ---");
            System.out.println("1 - Registrar cliente");
            System.out.println("2 - Registrar empleado");
            System.out.println("3 - Iniciar sesion cliente");
            System.out.println("4 - Iniciar sesion empleado");
            System.out.println("5 - Iniciar sesion administrador");
            System.out.println("6 - Salir");
            System.out.print("Opcion: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    interCliente.registrarCliente(gestor);
                    break;
                case 2:
                    interEmpleado.registrarEmpleado(gestor);
                    break;
                case 3:
                    Cliente cliente = interCliente.iniciarSesion(gestor);
                    interCliente.menuCliente(cliente, gestor);
                    break;
                case 4:
                    Empleado empleado = interEmpleado.iniciarSesion(gestor);
                    interEmpleado.menuEmpleado(empleado, gestor);
                    break;
                case 5:
                    if (interAdmin.iniciarSesionAdmin()) {
                        interAdmin.menuAdmin(gestor);
                    } else {
                        System.out.println("Password de administrador incorrecta.");
                    }
                    break;
                case 6:
                    seguir = 'n';
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

            if (seguir == 's') {
                System.out.print("Volver al menu principal? (s/n): ");
                seguir = sc.nextLine().toLowerCase().charAt(0);
            }
        }
        gestor.guardarTodo();
        System.out.println("Gracias por usar ServiceHub.");
    }
}
