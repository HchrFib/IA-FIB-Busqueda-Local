
/**
 * Clase que define un Pair.
 * Representa la implementación de una estructura de datos que permite almacenar pares de datos.
 * Cada par contiene dos elementos. Ambos pueden ser del mismo tipo, o de tipos de datos diferentes.
 * @author IA
 * @param <E> primer elemento.
 * @param <T> segundo elemento.
 * @version X
 */
public class Pair <E, T> {
    private E first;
    private T second;

    // CREADORAS

    /**
     * Creadora por defecto.
     * Crea un objeto de la clase Pair.
     * pre <em>Cierto</em>
     * post Crea un par vacío (no contiene ningún elemento).
     */
    public Pair () {}
    /**
     * Creadora con paso de parámetros.
     * Crea un objeto de la clase Pair.
     * @param first primer elemento.
     * @param second segundo elemento.
     * Instancia un par con el primer elemento igual a 'first' y el segundo igual a 'second'.
     */
    public Pair (E first, T second) {
        this.first = first;
        this.second = second;
    }

    // CONSULTORAS

    /**
     * Consultora (Getter) del primer elemento de un Pair.
     * pre <em> Cierto </em>
     * @return elemento 'first'.
     */
    public E getFirst() {
        return first;
    }

    /** Consultora (Getter) del segundo elemento de un Pair.
     *\pre <em> Cierto </em>
     * @return el elemento 'second'.
     */
    public T getSecond() {
        return second;
    }

    // MODIFICADORAS

    /**
     * Modificadora (Setter) del primer elemento de un Pair.
     * @param first contiene el primer elemento del pair.
     *  Modifica el elemento 'first' del par para que sea el que se pasa por parámetro.
     */
    public void setFirst(E first) {
        this.first = first;
    }

    /**
     * Modificadora (Setter) del segundo elemento de un Pair.
     * @param second contiene el segundo elemento del pair.
     * \pre <em> Cierto </em>
     * \post Modifica el elemento 'second' del par para que sea el que se pasa por parámetro.
     */
    public void setSecond(T second) {
        this.second = second;
    }

}
