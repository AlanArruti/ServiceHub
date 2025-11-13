package Clases;

import Enums.Oficios;
import Exceptions.PersonaNoEncontradaException;

import java.time.LocalDate;
import java.util.Scanner;

public class InterfazCliente {

<<<<<<< HEAD
    public void crearUsuario()
    {
        System.out.println("");
=======
    public void cargarDireccion(){
        Scanner sc = new Scanner(System.in);

        System.out.println("Ingrese la ciudad: ");
        String ciudad = sc.nextLine();
        System.out.println("Ingrese la calle: ");
        String calle = sc.nextLine();
        System.out.println("Ingrese el numero de la calle: ");
        int numero = sc.nextInt();

    }

    public void crearUsuario(){
        Scanner sc = new Scanner(System.in);

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
        System.out.println("Ingrese el direccion del usuario: ");





>>>>>>> 9c14e5207d8c28f16c9b13ee987d3346fa11bcc9
    }

    public Oficios elegirOficio()
    {
        Scanner sc = new Scanner(System.in);
        char seguir = 's';
        int opcion;
        Oficios oficio;

        while (seguir == 's')
        {
            System.out.println("INGRESE EL OFICIO DEL QUE DESEA CONOCER MAS: ");
            System.out.println("1 - PLOMERO. \n 2 - PINTOR. \n 3 - ELECTRICISTA. \n 4 - GASISTA. \n 5 - ALBAÑIL. \n 6 - CARPINTERO. \n 7 - HERRERO. \n 8 - JARDINERO.");

            opcion = sc.nextInt();

            switch (opcion)
            {
                case 1:{
                    oficio = Oficios.PLOMERO;
                    return oficio;
                }
                case 2:{
                    oficio = Oficios.PINTOR;
                    return oficio;
                }
                case 3:{
                    oficio = Oficios.ELECTRICISTA;
                    return oficio;
                }
                case 4:{
                    oficio = Oficios.GASISTA;
                    return oficio;
                }
                case 5:{
                    oficio = Oficios.ALBAÑIL;
                    return oficio;
                }
                case 6:{
                    oficio = Oficios.CARPINTERO;
                    return oficio;
                }
                case 7:{
                    oficio = Oficios.HERRERO;
                    return oficio;
                }
                case 8:{
                    oficio = Oficios.JARDINERO;
                    return oficio;
                }
                default:{
                    System.out.println("OPCION INVALIDA");
                }
            }
            System.out.println("¿Desea conocer informacion sobre otro oficio? (s/n) ");
            sc.nextLine();
            seguir = sc.next().charAt(0);
        }
        return null;
    }


    public void verInfoOficio()
    {
        GestorOficios g = new GestorOficios();

        Scanner sc = new Scanner(System.in);
        Oficios oficio = elegirOficio();
        char seguir = 's';
        String dni, fecha;

        switch (oficio)
        {
            case PLOMERO:{
                System.out.println("Se encarga de instalar, reparar y mantener cañerías de agua, desagües, sanitarios y griferías.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("PLOMERO\n");

                    while (seguir == 's')
                    {
                        System.out.println("Ingrese el DNI y la fecha de contratacion del Plomero para ver su disponibilidad. ");
                        dni = sc.next();

                        try
                        {
                            g.buscarEmpleado(dni);
                        }
                        catch (PersonaNoEncontradaException e)
                        {
                            e.getMessage();
                        }

                        System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                        fecha = sc.next();

                        boolean dispo = g.verSiEstaDisponible(dni, LocalDate.parse(fecha));

                        if (dispo == true)
                        {
                            // viene el metodo de contratacion
                            // es un quilombo con el servicio 
                        }
                        else
                        {
                            System.out.println("¿Desea volver a intentarlo con otra fecha u otro DNI? (s/n) ");
                            seguir = sc.next().charAt(0);
                        }
                    }


                }
                break;
            }
            case PINTOR:{
                System.out.println("Prepara superficies y aplica pintura o revestimientos en paredes, techos o muebles para proteger y decorar.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("PINTOR");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Pintor para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case ELECTRICISTA:{
                System.out.println("Instala, repara y mantiene sistemas eléctricos, enchufes, iluminación y cableado en hogares o empresas.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("ELECTRICISTA");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Electricista para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case GASISTA:{
                System.out.println("Coloca y repara cañerías de gas, calefones, cocinas y estufas, garantizando que funcionen de forma segura.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("GASISTA");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Gasista para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case ALBAÑIL:{
                System.out.println("Construye, repara y refacciona paredes, pisos, techos y estructuras con materiales como ladrillo, cemento o hormigón.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("ALBAÑIL");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Albañil para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case CARPINTERO:{
                System.out.println("Diseña, fabrica y repara muebles o estructuras de madera (puertas, marcos, estanterías, etc.).");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("CARPINTERO");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Carpintero para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case  HERRERO:{
                System.out.println("Trabaja con metales, fabricando y reparando rejas, portones, estructuras y piezas metálicas.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("HERRERO");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Herrero para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
            case JARDINERO:{
                System.out.println("Se ocupa del mantenimiento de jardines: corta césped, poda plantas, siembra flores y cuida el riego.");
                System.out.println("\n¿Desea conocer a los empleados disponibles? (s/n)");
                seguir = sc.next().charAt(0);

                while (seguir == 's')
                {
                    g.mostrarEmpleadoXcategoria("JARDINERO");
                    System.out.println("Ingrese el DNI y la fecha de contratacion del Jardinero para ver su disponibilidad. ");
                    dni = sc.next();
                    System.out.println("\n A LA HORA DE INGRESAR LA FECHA UTILICE EL FORMATO (yyyy/mm/dd) ----> Ejemplo: 2025-11-14");
                    fecha = sc.next();
                    g.verSiEstaDisponible(dni, LocalDate.parse(fecha));
                }
                break;
            }
        }
    }
}
