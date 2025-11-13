package Clases;

import java.util.Scanner;

public class InterfazGeneral {

    public void interfazBinaria()
    {
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        int opcion;
        InterfazCliente interCliente = new InterfazCliente();
        InterfazEmpleado interEmpleado = new InterfazEmpleado();

        while (seguir == 's')
        {
            System.out.println("¿Como desea registrarse?");
            System.out.println("1 - Cliente. \n2 - Empleado. ");
            opcion = sc.nextInt();

            switch (opcion)
            {
                case 1:{
                    // Cliente cliente1 = interCliente.crearUsuario();
                   // interCliente.verInfoOficio(cliente1);
                }
            }
        }

    }
}
