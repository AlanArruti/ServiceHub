import Clases.*;
import Enums.Oficios;

import java.time.LocalDate;
import Enums.Oficios;
import Exceptions.EmpleadoNoDisponibleException;
import Exceptions.PersonaNoEncontradaException;

public class Main {
    public static void main(String[] args) {

        GestorOficios gestor = new GestorOficios();

        // =============================
        // 1. AGREGAR CLIENTES
        // =============================
        Cliente c1 = new Cliente(
                "123",
                "Alan",
                "Arruti",
                "alan@gmail.com",
                "2235123456",
                new Direccion("Mar del Plata", "Belgrano", 1123)
        );

        Cliente c2 = new Cliente(
                "456",
                "Emanuel",
                "Perez",
                "ema@gmail.com",
                "2234000000",
                new Direccion("Mar del Plata", "Colon", 5500)
        );

        gestor.registrarCliente(c1);
        gestor.registrarCliente(c2);

        // =============================
        // 2. AGREGAR EMPLEADOS
        // =============================
        Empleado e1 = new Empleado("111", "Carlos", "Gonzalez", "carlos@gmail.com", "2231111111", Oficios.ELECTRICISTA);

        Empleado e2 = new Empleado(
                "222",
                "Juan",
                "Martinez",
                "juan@gmail.com", "2232222222", Oficios.PLOMERO);

        Empleado e3 = new Empleado("333", "Luciano", "Sosa", "luciano@gmail.com", "2233333333", Oficios.CARPINTERO);

        gestor.registrarEmpleado(e1);
        gestor.registrarEmpleado(e2);
        gestor.registrarEmpleado(e3);

        // =============================
        // 3. MOSTRAR DATOS
        // =============================
        System.out.println("=== CLIENTES ===");
        gestor.mostrarClientes();

        System.out.println("\n=== EMPLEADOS ===");
        gestor.mostrarEmpleados();

        // =============================
        // 4. PROBAR DISPONIBILIDAD
        // =============================

        LocalDate fechaPrueba = LocalDate.of(2024, 12, 10);

        System.out.println("\nProbando disponibilidad del empleado ELECTRICISTA (DNI 111) para " + fechaPrueba + " ...");
        boolean disponible = gestor.verSiEstaDisponible("111", fechaPrueba);
        System.out.println("¿Disponible? " + disponible);

        // =============================
        // 5. PROBAR CONTRATAR UN SERVICIO
        // =============================
        System.out.println("\n=== CONTRATANDO SERVICIO ===");

        Contrataciones servicio = new Contrataciones(
                "Reparación de instalación eléctrica",
                15000,
                Oficios.ELECTRICISTA.name(),
                c1
        );

        try {
            gestor.contratarEmpleado("111", servicio, fechaPrueba);
            System.out.println("¡Servicio contratado correctamente!");
        } catch (PersonaNoEncontradaException | EmpleadoNoDisponibleException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // =============================
        // 6. VERIFICAR QUE AHORA ESTÁ OCUPADO
        // =============================
        System.out.println("\nRevisando disponibilidad nuevamente...");
        boolean disponible2 = gestor.verSiEstaDisponible("111", fechaPrueba);
        System.out.println("¿Disponible? " + disponible2);

        // =============================
        // 7. INTENTAR CONTRATARLO OTRA VEZ
        // =============================
        System.out.println("\nIntentando contratarlo otra vez en el mismo día...");

        Contrataciones servicio2 = new Contrataciones(
                "Otra reparación eléctrica",
                18000,
                Oficios.ELECTRICISTA.name(),
                c2
        );

        try {
            gestor.contratarEmpleado("111", servicio2, fechaPrueba);
            System.out.println("¡¡ERROR!! Se contrató dos veces (esto no debería pasar)");
        } catch (PersonaNoEncontradaException | EmpleadoNoDisponibleException e) {
            System.out.println("ERROR ESPERADO -> " + e.getMessage());
        }

        System.out.println("\n=== FIN DE LA PRUEBA ===");
            }
        }