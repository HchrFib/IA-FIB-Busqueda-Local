import IA.Comparticion.Usuario;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SucesorHC implements SuccessorFunction {
    static public Estado last;
    private int step = 0;
    static private int set_operadores = 1;

    public static void cambiarConjuntoOperadores (int set_operadores) {
        if (set_operadores < 0 || set_operadores > 1)
            System.out.println("ERROR: Operador de hill climbing no existe");
        else
            SucesorHC.set_operadores = set_operadores;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Successor> getSuccessors(Object state) {
        last = (Estado) state;
        if (step%10 == 0) {
            System.out.print("Step: ");
            System.out.println(step);
        }
        step++;

        return switch (set_operadores) {
            case 0 -> operador0y1((Estado) state, 0);
            case 1 -> operador0y1((Estado) state, 1);
            default -> null;
        };
    }

    // El operador 0 es un "macro-operador" en el que cualquier pasajero o conductor sin pasajeros puede ser
    // cambiado a cualquier otro coche en cualquier otra posicion o como conductor en una nueva ruta (es decir,
    // todas las combinaciones posibles)
    // El operador 1 es un operador mas limitado que el 0 ya que solamente permite, para cada pasajero
    // o conductor sin pasajeros, o bien reubicarlo en cualquier posicion dentro del mismo coche o bien
    // cambiarlo a otro coche en la primera posicion posible en la que puede ser insertado (a diferencia
    // del operador 0, en donde al cambiar de coche se probaban todas las combinaciones de nuevas posiciones)

    private ArrayList<Successor> operador0y1 (Estado e, int operador) {
        ArrayList<Successor> sucesores = new ArrayList<>();
        int it_num = 0;
        // por cada uno de los itinerarios, tomar cada uno de los pasajeros/conductor y hacer todas
        // las combinaciones posibles de reposicionamientos
        for (Datos it : e.getItinerario()) {
            int longitud_recorrido = it.getLongitudRuta();
            // solamente se sacara al conductor y se reubicara en el caso de que no tenga ningun pasajero
            if (longitud_recorrido == 0)
                operador0y1TratarConductor(e, it, it_num, sucesores, operador);
            else
                operador0y1TratarPasajeros(e, it, it_num, sucesores, operador);
            it_num++;
        }
        return sucesores;
    }

    private void operador0y1TratarConductor (Estado e, Datos it, int it_num, ArrayList<Successor> sucesores, int operador) {
        Pair<Integer, Usuario> cond = it.getConductor();
        // crear clon sin el conductor y crear todos los sucesores con las reubicaciones del conductor
        Estado sin_cond = (Estado) e.clone();
        sin_cond.getItinerario().remove(it_num);
        String descr = "* Driver " + it.getConductor().getFirst() + " (it: " + it_num + ") removed from drivers and ";
        if (operador == 0)
            operador0CombsUbicar(sin_cond, cond, sucesores, -1, -1, -1, descr);
        else
            operador1CombsUbicar(sin_cond, cond, sucesores, -1, -1, -1, descr);
    }

    private void operador0y1TratarPasajeros (Estado e, Datos it, int it_num, ArrayList<Successor> sucesores, int operador) {
        // como cada uno de los pasajeros esta presente ds veces en el itinerario (una cuando se sube y otra
        // cuando se baja) tenemos que llevar la cuenta de los pasajeros que ya hemos tratado para no repetir
        // la operacion cuando nos lo volvamos a encontrar, por eso se meten en el ector usados
        Vector<Integer> usados = new Vector<>();
        ArrayList<Pair<Integer, Usuario>> pasajeros = it.getPasajeros();
        for (int i = 0; i < it.getLongitudRuta(); i++) {
            Pair<Integer, Usuario> pasajero = pasajeros.get(i);
            // si no ha sido tratado ya...
            if (!usados.contains(pasajero.getFirst())) {
                usados.add(pasajero.getFirst());
                // crear clon del estado sin pasajero y obtener todas las combinaciones de reubicacion del pasajero
                Estado sin_pas = (Estado) e.clone();
                Pair<Integer,Integer> originales = sin_pas.getItinerario().get(it_num).eliminarPasajero(pasajero);
                String descr = "* Passenger " + pasajero.getFirst() + " (it: " + it_num + ") ";
                if (operador == 0)
                    operador0CombsUbicar(sin_pas, pasajero, sucesores, it_num, originales.getFirst(), originales.getSecond(), descr);
                else
                    operador1CombsUbicar(sin_pas, pasajero, sucesores, it_num, originales.getFirst(), originales.getSecond(), descr);
            }
        }
    }

    // prueba todas las posibles combinaciones, excepto la que esta en la ruta it_orig en posicion
    // pini_orig (posicion inicial original) y pfi_orig. Si it_orig = -1 entonces no se añadira este
    // usuario como conductor en una nueva ruta
    private void operador0CombsUbicar (Estado estado_base, Pair<Integer, Usuario> user, ArrayList<Successor> sucesores,
                                       int it_orig, int pini_orig, int pfi_orig, String descr_orig)
    {
        int it_counter = 0;
        for (Datos it : estado_base.getItinerario()) {
            // si el itinerario no es el mismo que en el caso inicial ya no tenemos que preocuparnos
            // por insertar el usuario en el mismo sitio
            if (it_orig != it_counter)
                operador0CombsUbicarPax(estado_base, user, sucesores, it, it_counter, -1, -1, descr_orig);
            else
                operador0CombsUbicarPax(estado_base, user, sucesores, it, it_counter, pini_orig, pfi_orig, descr_orig);
            it_counter++;
        }
        // si el ususario era conductor it_orig es -1, por lo que no queremos volver a ponerlo como conductor
        if (it_orig != -1)
            operador0y1CombsUbicarConductor(estado_base, user, sucesores, descr_orig);
    }

    private void operador0CombsUbicarPax (Estado estado_base, Pair<Integer, Usuario> user, ArrayList<Successor> sucesores,
                                          Datos itinerario, int it_counter,int pini_orig, int pfi_orig, String descr_orig)
    {
        ArrayList<Pair<Integer,Integer>> tramos = itinerario.tramosInsertables();
        // para cada uno de los tramos tratar de ubicar el pasajero en todas las combinaciones posibles
        for (Pair<Integer,Integer> tramo : tramos) {
            for (int orig = tramo.getFirst(); orig <= tramo.getSecond(); orig++) {
                for (int dest = orig; dest <= tramo.getSecond(); dest++) {
                    // evitar generar nuevamente el estado inicial
                    if (pini_orig != orig || pfi_orig != dest) {
                        try {
                            Estado nuevo_estado = (Estado) estado_base.clone();
                            // tratar de insertar el pasajero en esta posicion, si la insercion causa
                            // que el recorrido supere el limite de distancia entonces se lanza una excepcion
                            // y el usuario queda sin ser insertado
                            nuevo_estado.getItinerario().get(it_counter).insertarPasajero(user, orig, dest);
                            String descr = descr_orig + " set as passenger in itinerary " + it_counter +
                                    " picked at " + orig + " and left at " + (dest+1);
                            sucesores.add(new Successor(descr, nuevo_estado));
                        } catch (Exception e) {
                            // en caso de que no se pueda insertar por distancia no se añade
                        }
                    }
                }
            }
        }
    }

    private void operador1CombsUbicar (Estado estado_base, Pair<Integer, Usuario> user, ArrayList<Successor> sucesores,
                                       int it_orig, int pini_orig, int pfi_orig, String descr_orig)
    {
        // si el usuario era conductor solamente hay que ponerlo en cada uno de los coches
        operador1CambiarCochePasajero(estado_base, user, it_orig, sucesores, descr_orig);
        // si no era conductor ponerlo en una nueva ruta como conductor y reubicarlo dentro del coche
        if (it_orig != -1) {
            operador0y1CombsUbicarConductor(estado_base, user, sucesores, descr_orig);
            Datos it = estado_base.getItinerario().get(it_orig);
            operador1ReubicarPasajeroEnCoche(estado_base, user, it, it_orig, sucesores, descr_orig, pini_orig, pfi_orig);
        }
    }

    private void operador0y1CombsUbicarConductor (Estado estado_base, Pair<Integer, Usuario> user,
                                                  ArrayList<Successor> sucesores, String descr_orig)
    {
        Estado nuevo_estado = (Estado) estado_base.clone();
        nuevo_estado.getItinerario().add(new Datos(user, new ArrayList<>()));
        String descr = descr_orig+" set as conductor in a new route.";
        sucesores.add(new Successor(descr, nuevo_estado));
    }

    private void operador1CambiarCochePasajero (Estado estado_base, Pair<Integer, Usuario> user, int it_orig,
                                               ArrayList<Successor> sucesores, String descr_orig)
    {
        int it_counter = 0;
        // meter al pasajero en un nuevo coche (excepto en el coche del que proviene)
        for (Datos it : estado_base.getItinerario()) {
            if (it_counter != it_orig) {
                boolean ubicado = false;
                // probar todas las combinaciones donde colocar el pasajero hasta que alguna resulte exitosa
                ArrayList<Pair<Integer,Integer>> tramos = it.tramosInsertables();
                for (Pair<Integer, Integer> tramo : tramos) {
                    for (int orig = tramo.getFirst(); orig <= tramo.getSecond() && !ubicado; orig++) {
                        for (int dest = orig; dest <= tramo.getSecond() && !ubicado; dest++) {
                            Estado nuevo_sucesor = (Estado) estado_base.clone();
                            try {
                                // este metodo lanza una excepcion en caso de que no se pueda insertar por limite
                                // de longitud, por lo que en ese caso se pasa al catch y no se inserta
                                nuevo_sucesor.getItinerario().get(it_counter).insertarPasajero(user, orig, dest);
                                ubicado = true;
                                String descr = descr_orig + " set as passenger in itinerary " + it_counter +
                                        " picked at " + orig + " and left at " + (dest+1);
                                sucesores.add(new Successor(descr, nuevo_sucesor));
                                break;
                            } catch (Exception e) {
                                // ha saltado la excepcion, no se añade usuario
                            }
                        }
                    }
                    if (ubicado)
                        break;
                }
            }
            it_counter++;
        }
    }

    private void operador1ReubicarPasajeroEnCoche (Estado estado_base, Pair<Integer, Usuario> user, Datos itinerario, int it_num,
                                                   ArrayList<Successor> sucesores, String descr_orig, int pini_orig, int pfi_orig)
    {
        // ponerlo en todas las combinaciones posibles dentro del mismo coche
        ArrayList<Pair<Integer,Integer>> tramos = itinerario.tramosInsertables();
        for (Pair<Integer, Integer> tramo : tramos) {
            for (int orig = tramo.getFirst(); orig < tramo.getSecond(); orig++) {
                for (int dest = orig; dest < tramo.getSecond(); dest++) {
                    if (orig != pini_orig | dest != pfi_orig) {
                        Estado nuevo_sucesor = (Estado) estado_base.clone();
                        try {
                            // este metodo lanza una excepcion en caso de que no se pueda insertar por limite
                            // de longitud, por lo que en ese caso se pasa al catch y no se inserta
                            nuevo_sucesor.getItinerario().get(it_num).insertarPasajero(user, orig, dest);
                            String descr = descr_orig + " set as passenger in itinerary " + it_num +
                                    " picked at " + orig + " and left at " + (dest + 1);
                            sucesores.add(new Successor(descr, nuevo_sucesor));
                        } catch (Exception e) {
                            // ha saltado la excepcion, no se añade usuario
                        }
                    }
                }
            }
        }
    }
}
