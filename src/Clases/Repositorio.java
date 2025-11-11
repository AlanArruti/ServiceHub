package Clases;

import Exceptions.PersonaNoEncontradaException;

import java.util.*;

public class Repositorio<T> {
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

    public T buscarPorEmail(String email) throws PersonaNoEncontradaException {
        for (T elemento : elementos) {
            // Verificamos si la clase tiene un metodo getEmail, para no hacer un extends Persona
            try {
                String mailElemento = (String) elemento.getClass().getMethod("getEmail").invoke(elemento);
                if (mailElemento != null && mailElemento.equalsIgnoreCase(email)) {
                    return elemento;
                }
            } catch (Exception e) {
                // Si el tipo T no tiene getEmail, sigue con su funcionamiento
            }
        }
        throw new PersonaNoEncontradaException("No se encontró ninguna persona con el email: " + email);
    }

    @Override
    public String toString() {
        return "Repositorio con " + elementos.size() + " elementos: " + elementos;
    }
}
