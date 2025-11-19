import Clases.GestorOficios;
import Clases.InterfazGeneral;

public class Main {
    public static void main(String[] args) {
        GestorOficios gestor = new GestorOficios();
        gestor.cargarDatos();
        InterfazGeneral interfazGeneral = new InterfazGeneral(gestor);
        interfazGeneral.iniciar();
    }
}
