package Clases;

import java.util.ArrayList;

public class Generica <T>{
    private ArrayList<T> elementos = new ArrayList<>();

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public ArrayList<T> Listar(){
        return new ArrayList<>(elementos);
    }

    public void eliminar(T elemento) {
        elementos.remove(elemento);
    }

    public int cantidad(){
        return elementos.size();
    }
}
