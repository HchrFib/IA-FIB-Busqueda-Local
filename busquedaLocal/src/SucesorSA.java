import IA.Comparticion.Usuario;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.random;

public class SucesorSA implements SuccessorFunction {
    static public Estado last;
    private int step = 0;
    public List getSuccessors(Object state) {
        last = (Estado) state;
        if (step%500 == 0) {
            System.out.print("Step: ");
            System.out.println(step);
        }
        step++;
        return operador0((Estado)state);
    }

    private ArrayList<Successor> operador0 (Estado e) {
        String descr = "";
        Estado sucesor = null;
        boolean valido = false;

        while (!valido) {
            Integer it_orig = 0, pini_orig = 0, pfi_orig = 0;
            sucesor = (Estado) e.clone();
            Pair<Integer, Usuario> usuario_select = operador0GetUsuarioAleatorio(sucesor, descr, it_orig, pini_orig, pfi_orig);

            // reubicar este pasajero (posiblemente como un nuevo conductor si no lo era ya antes)
            valido = operador0TratarDeInsertarPasajero(sucesor, usuario_select, descr, it_orig, pini_orig, pfi_orig);
        }

        ArrayList<Successor> sucesores = new ArrayList<>();
        sucesores.add(new Successor(descr, sucesor));
        return sucesores;
    }

    private Pair<Integer, Usuario> operador0GetUsuarioAleatorio (Estado estado, String descr, Integer it_orig,
                                                                 Integer pini_orig, Integer pfi_orig)
    {
        int user_id;
        boolean valido = false;
        Pair<Integer, Usuario> usuario_seleccionado = null;
        // seleccionar un pasajero al azar
        while (!valido) {
            user_id = (int) floor(random() * estado.getNumUsers());
            it_orig = 0;
            // buscar al usuario en cada itinerario
            for (Datos it : estado.getItinerario()) {
                // si es conductor solamente nos interesa si no tiene pasajeros, en caso contrario buscar
                // otro usuario
                if (it.getConductor().getFirst() == user_id) {
                    if (it.getPasajeros().size() == 0) {
                        valido = true;
                        usuario_seleccionado = it.getConductor();
                        pini_orig = -1;
                        estado.getItinerario().remove(it);
                        it_orig = -1;
                        descr = "* Driver " + user_id + " (it: " + it_orig + ") removed from drivers and ";
                        break;
                    } else {
                        break; // user is driver that has passengers, cannot be removed
                    }
                } else {
                    for (Pair<Integer, Usuario> u : it.getPasajeros()) {
                        if (u.getFirst() == user_id) {
                            valido = true;
                            usuario_seleccionado = u;
                            Pair<Integer, Integer> p_orig = it.eliminarPasajero(u);
                            pini_orig = p_orig.getFirst();
                            pfi_orig = p_orig.getSecond();
                            descr = "* Passenger " + user_id + " (it: " + it_orig + ") ";
                            break;
                        }
                    }
                }
                if (valido)
                    break;
                else
                    it_orig++;
            }
        }
        return usuario_seleccionado;
    }

    private boolean operador0TratarDeInsertarPasajero (Estado estado, Pair<Integer, Usuario> usuario_select, String descr,
                                                       Integer it_orig, Integer pini_orig, Integer pfi_orig)
    {
        boolean valido = false;
        int intentos = 0;
        // intentos sirve para evitar que el programa se quede mucho tiempo insertando un usuario que
        // es muy complicado de ubicar
        while (!valido && intentos < 20) {
            int it_new;
            it_new = (int) floor(random() * (estado.getItinerario().size() + 1));
            // if it_new == number of itineraries it means that the new user will be set as a new driver
            // otherwise it will be inserted in the itinerary number it_new
            if (it_new != estado.getItinerario().size()) {
                // seleccionar un tramo aleatorio e intentar insertar el usuario en una posicion
                // aleatoria dentro de ese tramo
                ArrayList<Pair<Integer, Integer>> tramos = estado.getItinerario().get(it_new).tramosInsertables();
                Pair<Integer, Integer> tramo = tramos.get((int) floor(random() * tramos.size()));
                // get numero aleatorio dentro de tramo
                int pini_new = (int) floor(random() * (1 + tramo.getSecond() - tramo.getFirst()) + tramo.getFirst());
                int pfi_new = (int) floor(random() * (1 + tramo.getSecond() - pini_new) + pini_new);
                // avoid repeating state
                if (it_orig != it_new || pini_orig != pini_new || pfi_orig != pfi_new) {
                    try {
                        // insertar pasajero puede lanzar una excepcion en caso de que se supere la distancia
                        // total de la ruta, por lo que en ese caso se salta al catch y no se inserta el
                        // usuario (se probara con otro lugar diferente)
                        estado.getItinerario().get(it_new).insertarPasajero(usuario_select, pini_new, pfi_new);
                        valido = true;
                        descr += " set as passenger in itinerary " + it_new +
                                " picked at " + pini_new + " and left at " + (pfi_new + 1);
                    } catch (Exception exc) {
                        // en caso de que no se pueda isnertar ahi se repite el bucle y se selecciona una
                        // nueva ubicacion
                    }
                }
            // it_orig is -1 in case this user was previously a driver, we want to avoid setting
            // it as a driver again (it_new = number of itineraries)
            } else if (it_new == estado.getItinerario().size() && it_orig != -1) {
                Datos nuevo_iter = new Datos(usuario_select, new ArrayList<>());
                estado.getItinerario().add(nuevo_iter);
                descr += " set as conductor in a new route.";
                valido = true;
            }
            intentos++;
        }
        return valido;
    }
}
