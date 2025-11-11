package Clases;

import Exceptions.PersonaNoEncontradaException;
import Interfaces.Identificable;

import java.util.*;

// clase generica que gestiona solo elementos identificables, aplicando genericidad y encapsulamiento.
public class Repositorio<T extends Identificable> {
    private List<T> elementos;

    public Repositorio() {
        this.elementos = new ArrayList<>();
    }

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public boolean eliminar(T elemento) {
        return elementos.remove(elemento);
    }

    public List<T> listar() {
        return new ArrayList<>(elementos); // devuelve una copia para mantener encapsulamiento
    }

    public boolean existe(T elemento) {
        return elementos.contains(elemento);
    }

    public int cantidad() {
        return elementos.size();
    }

    public T buscarPorId(String id) throws PersonaNoEncontradaException {
        for (T elemento : elementos) {
            if (elemento.getIdentificador().equalsIgnoreCase(id)) {
                return elemento;
            }
        }
        throw new PersonaNoEncontradaException("No se encontró ningún elemento con el ID: " + id);
    }

    @Override
    public String toString() {
        return "Repositorio con " + elementos.size() + " elementos: " + elementos;
    }
}
