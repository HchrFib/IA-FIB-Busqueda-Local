import java.lang.reflect.Array;
import java.util.*;
import IA.Comparticion.Usuario;

import java.util.stream.Collectors;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

/**
 * Define la estructura interna de un itinerario.
 * @author IA
 * @version X
 */
public class Datos implements Cloneable {
    /**
     * propiedad que contiene el identificador de un conductor
     */
    private Pair<Integer, Usuario> conductor;
    /**
     * propiedad que contiene la lista de pasajeros
     */
    private ArrayList< Pair<Integer, Usuario> > pasajeros;

    private int distancia;
    //constructor

    public Datos() {
        this.conductor = new Pair<>();
        this.pasajeros = new ArrayList<>();
        this.distancia = 0;
    }

    public double getEntropia (double max_dist) {
        double porcentaje_llenado = distancia/max_dist;
        return -porcentaje_llenado*log(porcentaje_llenado);
    }

    public Object clone() {
        Datos nuevo = new Datos();
        nuevo.conductor = conductor;
        nuevo.pasajeros = (ArrayList<Pair<Integer, Usuario>>) pasajeros.clone();
        nuevo.distancia = distancia;
        return nuevo;
    }


    /**
     * Constructor con parámetros
     * @param conductor contiene el identificador de un conductor
     * @param pasajeros contiene la lista de pasajeros
     */
    public Datos(Pair<Integer, Usuario> conductor, ArrayList< Pair<Integer, Usuario>  > pasajeros) {
        this.conductor = conductor;
        this.pasajeros = pasajeros;
        distTotal();
    }

    //Getters

    public int getLongitudRuta () {
        return pasajeros.size();
    }

    /**
     * Consultora
     * @return El conductor del servicion de compartición de coches.
     */
    public Pair<Integer, Usuario> getConductor() {
       return this.conductor;
    }
    /**
     * Consultora
     * @return La lista de pasajeros.
     */
    public ArrayList< Pair<Integer, Usuario> > getPasajeros(){
        return this.pasajeros;
    }

    public int getDistancia() {
        return distancia;
    }

    public int getDistanciaByPasajero(Pair<Integer, Usuario> pasajero){
        int x0 = pasajero.getSecond().getCoordOrigenX();
        int x1 = pasajero.getSecond().getCoordDestinoX();
        int y0 = pasajero.getSecond().getCoordOrigenY();
        int y1 = pasajero.getSecond().getCoordDestinoY();
        return distance(x0,y0,x1,y1);
    }

    public int getNumPasajeros () {
        return (int)ceil(pasajeros.size()/2.f);
    }

    //Setters

    /**
     * Modificadora
     * @param conductor contiene el conductor.
     */
    public void setConductor(Pair<Integer, Usuario> conductor) {
        this.conductor = conductor;
    }
    /**
     * Modificadora
     * @param pasajeros contiene la lista de pasajeros.
     */
    public void setPasajeros(ArrayList<Pair<Integer, Usuario>> pasajeros) {
        this.pasajeros = pasajeros;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    //Auxiliares

    private int distance(int x0, int y0, int x1, int y1) {
        return Math.abs(x0-x1) + Math.abs(y0-y1);
    }

    private void distTotal() {
        distancia = 0;
        ArrayList<Integer> aux = new ArrayList<>();
        int prevX = conductor.getSecond().getCoordOrigenX();
        int prevY = conductor.getSecond().getCoordOrigenY();
        for (Pair<Integer, Usuario> pasajero: pasajeros) {
            if (aux.indexOf(pasajero.getFirst()) == -1) {
                distancia += distance(prevX, prevY, pasajero.getSecond().getCoordOrigenX(), pasajero.getSecond().getCoordOrigenY());
                prevX = pasajero.getSecond().getCoordOrigenX();
                prevY = pasajero.getSecond().getCoordOrigenY();
                aux.add(pasajero.getFirst());
            }
            else {
                distancia += distance(prevX, prevY, pasajero.getSecond().getCoordDestinoX(), pasajero.getSecond().getCoordDestinoY());
                prevX = pasajero.getSecond().getCoordDestinoX();
                prevY = pasajero.getSecond().getCoordDestinoY();
                aux.remove(pasajero.getFirst());
            }
        }
        distancia += distance(prevX, prevY, conductor.getSecond().getCoordDestinoX(), conductor.getSecond().getCoordDestinoY());
    }

    public void removePasajero(Pair<Integer, Usuario> pasajero){
        this.pasajeros.remove(pasajero);
        this.pasajeros.remove(pasajero);
        distTotal();
    }

    public void addPasajero(Pair<Integer, Usuario> pasajero){
        this.pasajeros.add(pasajero);
        this.pasajeros.add(pasajero);
        distTotal();
    }

    // this function does NOT check if the insertion surpasses the two passenger limit
    public void insertarPasajero (Pair<Integer,Usuario> p, int p_ini, int p_fi) throws Exception {
        if (p_ini > p_fi)
            throw new Exception("ERROR: no se puede insertar pasajero con destino antes de recogida.");
        Usuario u = p.getSecond();
        pasajeros.add(p_ini, p);
        pasajeros.add(p_fi+1, p);
        distTotal();
        if (distancia > 300) {
            pasajeros.remove(p);
            pasajeros.remove(p);
            distTotal();
            throw new Exception("ERROR: la distancia de la ruta supera el maximo permitido.");
        }
    }

    public Pair<Integer, Integer> eliminarPasajero (Pair<Integer,Usuario> p) {
        // el pasajero aparece dos veces
        Pair<Integer, Integer> posiciones = new Pair<>();
        posiciones.setFirst(pasajeros.indexOf(p));
        pasajeros.remove(p);
        posiciones.setSecond(pasajeros.indexOf(p));
        pasajeros.remove(p);
        distTotal();
        return posiciones;
    }

    // retorna una lista con intervalos de posiciones en los que no se ha superado
    // el limite de dos pasajeros (ambos extremos inclusives)
    public ArrayList<Pair<Integer,Integer>> tramosInsertables () {
        ArrayList<Pair<Integer,Integer>> tramos = new ArrayList<>();
        ArrayList<Integer> recogido = new ArrayList<>();
        Pair<Integer,Integer> tramo = new Pair<>();
        tramo.setFirst(0);
        int n_pax = 0, n_pax_old = 0;
        for (int i = 0; i < pasajeros.size(); i++) {
            int id = pasajeros.get(i).getFirst();
            if (recogido.contains(id)) n_pax--;
            else {
                n_pax++;
                recogido.add(id);
            }
            if (n_pax == 2 && n_pax_old == 2) {
                tramo.setSecond(i-1);
                tramos.add(tramo);
                tramo = new Pair<>();
            } else if (n_pax == 1 && n_pax_old == 2)
                tramo.setFirst(i);
            n_pax_old = n_pax;
        }
        if (n_pax != 2) {
            tramo.setSecond(pasajeros.size());
            tramos.add(tramo);
        }
        return tramos;
    }

    // dado una posicion en el vector de pasajeros dice si este ha sido recogido o dejado
    private boolean esRecogida (int pos) {
        int usuario = pasajeros.get(pos).getFirst();
        for (int i = 0; i < pos; i++)
            if (pasajeros.get(i).getFirst() == usuario)
                return false;
        return true;
    }

    public String toString () {
        String output = "[(conductor: " + conductor.getFirst().toString() + ", dist: " + distancia + "): " +
                        conductor.getFirst() + "^";
        ArrayList<Integer> mostrados = new ArrayList<>();
        for (Pair<Integer, Usuario> p : pasajeros) {
            if (!mostrados.contains(p.getFirst())) {
                output += " " + p.getFirst() + "^";
                mostrados.add(p.getFirst());
            } else {
                output += " " + p.getFirst() + "v";
            }
        }
        return output + " " + conductor.getFirst() + "v ]";
    }
}
