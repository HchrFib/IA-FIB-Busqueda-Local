

import IA.Comparticion.Usuario;

import java.util.*;
/**  Calc.java
   Implementación de la clase Calc, clase para calcular las distancias.
 */
public class Calc {
    
    /**
     * Método que calcula la distancia entre dos puntos.
     *
     * @param x0 coordenada x de origen/destino.
     * @param y0 coordenada y de origen/destino.
     * @param x1 coordenada x de origen/destino.
     * @param y1 coordenada y de origen/destino.
     * @return devuelve la distancia entre dos puntos.
     */
    public static int distance(int x0, int y0, int x1, int y1) {
        return Math.abs(x0 - x1) + Math.abs(y0 - y1);
    }
    /**
     * Método que calcula la distancia entre dos usuarios
     * @param pasajero1 contiene un pasajero.
     * @param pasajero2 contiene otro pasajero.
     * @return devuelve la distancia entre dos usuarios
     */
    public static int calcDistUsuarios(Usuario pasajero1, Usuario pasajero2) {
        return Calc.distance(pasajero1.getCoordOrigenX(), pasajero1.getCoordOrigenY(),
                pasajero2.getCoordOrigenX(),pasajero2.getCoordOrigenY()) +
                Calc.distance(pasajero1.getCoordDestinoX(), pasajero1.getCoordDestinoY(),
                        pasajero2.getCoordDestinoX(),pasajero2.getCoordDestinoY());
    }

    public static int distAdd(Datos d, Usuario pasajero, int pos1, int pos2) {
        int dist = d.getDistancia();
        int x_ini, y_ini, x_fin, y_fin;

        //Colocar en pos1
        if (pos1 > 0) {     //No va al principio
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos1-1)) == (pos1 - 1)) {
                x_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordOrigenX();
                y_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordOrigenY();
            } else {
                x_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordDestinoX();
                y_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordDestinoY();
            }
        } else {
            x_ini = d.getConductor().getSecond().getCoordOrigenX();
            y_ini = d.getConductor().getSecond().getCoordOrigenY();
        }
        if (pos1 < d.getPasajeros().size()) {   //No va al final
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos1)) == (pos1)) {
                x_fin = d.getPasajeros().get(pos1).getSecond().getCoordOrigenX();
                y_fin = d.getPasajeros().get(pos1).getSecond().getCoordOrigenY();
            } else {
                x_fin = d.getPasajeros().get(pos1).getSecond().getCoordDestinoX();
                y_fin = d.getPasajeros().get(pos1).getSecond().getCoordDestinoY();
            }
        } else {
            x_fin = d.getConductor().getSecond().getCoordDestinoX();
            y_fin = d.getConductor().getSecond().getCoordDestinoY();
        }
        dist -= distance(x_ini, y_ini, x_fin, y_fin);
        dist += distance(x_ini, y_ini, pasajero.getCoordOrigenX(), pasajero.getCoordOrigenY());
        dist += distance(pasajero.getCoordOrigenX(), pasajero.getCoordOrigenY(), x_fin, y_fin);

        //Colocar en pos2
        if (pos2 > pos1 + 1) {     //No va después de pos1
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos2-2)) == (pos2-2)) {
                x_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordOrigenX();
                y_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordOrigenY();
            } else {
                x_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordDestinoX();
                y_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordDestinoY();
            }
        } else {
            x_ini = pasajero.getCoordOrigenX();
            y_ini = pasajero.getCoordOrigenY();
        }
        if (pos2 < d.getPasajeros().size()+1) {   //No va al final
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos2-1)) == (pos2-1)) {
                x_fin = d.getPasajeros().get(pos2-1).getSecond().getCoordOrigenX();
                y_fin = d.getPasajeros().get(pos2-1).getSecond().getCoordOrigenY();
            } else {
                x_fin = d.getPasajeros().get(pos2-1).getSecond().getCoordDestinoX();
                y_fin = d.getPasajeros().get(pos2-1).getSecond().getCoordDestinoY();
            }
        } else {
            x_fin = d.getConductor().getSecond().getCoordDestinoX();
            y_fin = d.getConductor().getSecond().getCoordDestinoY();
        }
        dist -= distance(x_ini, y_ini, x_fin, y_fin);
        dist += distance(x_ini, y_ini, pasajero.getCoordDestinoX(), pasajero.getCoordDestinoY());
        dist += distance(pasajero.getCoordDestinoX(), pasajero.getCoordDestinoY(), x_fin, y_fin);

        return dist;
    }
    public static int distQuitar(Datos d, Usuario pasajero, int id) {
        int pos1 = d.getPasajeros().indexOf(new Pair(id,pasajero));
        int pos2 = d.getPasajeros().lastIndexOf(new Pair(id,pasajero));
        int dist = d.getDistancia();
        int x_ini, y_ini, x_fin, y_fin;

        //Colocar en pos1
        if (pos1 > 0) {     //No va al principio
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos1-1)) == (pos1 - 1)) {
                x_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordOrigenX();
                y_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordOrigenY();
            } else {
                x_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordDestinoX();
                y_ini = d.getPasajeros().get(pos1-1).getSecond().getCoordDestinoY();
            }
        } else {
            x_ini = d.getConductor().getSecond().getCoordOrigenX();
            y_ini = d.getConductor().getSecond().getCoordOrigenY();
        }
        if (d.getPasajeros().indexOf(d.getPasajeros().get(pos1+1)) == (pos1+1)) {
            x_fin = d.getPasajeros().get(pos1+1).getSecond().getCoordOrigenX();
            y_fin = d.getPasajeros().get(pos1+1).getSecond().getCoordOrigenY();
        } else {
            x_fin = d.getPasajeros().get(pos1+1).getSecond().getCoordDestinoX();
            y_fin = d.getPasajeros().get(pos1+1).getSecond().getCoordDestinoY();
        }
        dist += distance(x_ini, y_ini, x_fin, y_fin);
        dist -= distance(x_ini, y_ini, pasajero.getCoordOrigenX(), pasajero.getCoordOrigenY());
        dist -= distance(pasajero.getCoordOrigenX(), pasajero.getCoordOrigenY(), x_fin, y_fin);

        //Colocar en pos2
        if (pos2 > pos1 + 1) {     //No va después de pos1
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos2-1)) == (pos2-1)) {
                x_ini = d.getPasajeros().get(pos2-1).getSecond().getCoordOrigenX();
                y_ini = d.getPasajeros().get(pos2-1).getSecond().getCoordOrigenY();
            } else {
                x_ini = d.getPasajeros().get(pos2-1).getSecond().getCoordDestinoX();
                y_ini = d.getPasajeros().get(pos2-1).getSecond().getCoordDestinoY();
            }
        } else {
            if (pos2 > 1) {     //No va al principio
                if (d.getPasajeros().indexOf(d.getPasajeros().get(pos2-2)) == (pos2 - 2)) {
                    x_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordOrigenX();
                    y_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordOrigenY();
                } else {
                    x_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordDestinoX();
                    y_ini = d.getPasajeros().get(pos2-2).getSecond().getCoordDestinoY();
                }
            } else {
                x_ini = d.getConductor().getSecond().getCoordOrigenX();
                y_ini = d.getConductor().getSecond().getCoordOrigenY();
            }
        }
        if (pos2 < d.getPasajeros().size()-1) {   //No va al final
            if (d.getPasajeros().indexOf(d.getPasajeros().get(pos2+1)) == (pos2+1)) {
                x_fin = d.getPasajeros().get(pos2+1).getSecond().getCoordOrigenX();
                y_fin = d.getPasajeros().get(pos2+1).getSecond().getCoordOrigenY();
            } else {
                x_fin = d.getPasajeros().get(pos2+1).getSecond().getCoordDestinoX();
                y_fin = d.getPasajeros().get(pos2+1).getSecond().getCoordDestinoY();
            }
        } else {
            x_fin = d.getConductor().getSecond().getCoordDestinoX();
            y_fin = d.getConductor().getSecond().getCoordDestinoY();
        }
        dist += distance(x_ini, y_ini, x_fin, y_fin);
        dist -= distance(x_ini, y_ini, pasajero.getCoordDestinoX(), pasajero.getCoordDestinoY());
        dist -= distance(pasajero.getCoordDestinoX(), pasajero.getCoordDestinoY(), x_fin, y_fin);

        return dist;
    }
    public static int generarNumAleatorio(int n) {
        return (int) (Math.random() * (n));
    }
    public static Pair<Integer, Integer> calculaDistanciaTrayecto(int dist,Datos d,Pair<Integer, Usuario> usuario1 , Pair<Integer, Usuario> usuario2) {
        // conductor = usuario1
        // pasajero  = usuario2

        if(d.getPasajeros().isEmpty()) {
            dist = Calc.distance_source_to_source(usuario1, usuario2);
        } else {
            //accedemos al pasajero del itinerario
            Pair<Integer, Usuario>  lastPasajero = d.getPasajeros().get(d.getPasajeros().size()-1);
            dist += Calc.distance_dest_to_source(lastPasajero, usuario2);
        }
        dist += Calc.distanceHW(usuario2);
        int dist_dest_to_dest = Calc.distance_dest_to_dest(usuario2, usuario1);
        return new Pair<>(dist, dist_dest_to_dest);
    }
    /**
     * Método que calcula la distancia entre dos puntos source_to_source.
     *
     * @param p1 coordenada x de origen
     * @param p2 coordenada y de origen
     * @return devuelve la distancia entre dos puntos.
     */
    public static int distance_source_to_source(Pair<Integer,Usuario> p1, Pair<Integer, Usuario> p2 ) {
        return distance(p1.getSecond().getCoordOrigenX(),
                p1.getSecond().getCoordOrigenY(),
                p2.getSecond().getCoordOrigenX(),
                p2.getSecond().getCoordOrigenY());
    }
    public static int distance_dest_to_dest(Pair<Integer,Usuario> p1, Pair<Integer, Usuario> p2 ) {
        return distance(p1.getSecond().getCoordDestinoX(),
                p1.getSecond().getCoordDestinoY(),
                p2.getSecond().getCoordDestinoX(),
                p2.getSecond().getCoordDestinoY());
    }
    public static int distance_dest_to_source(Pair<Integer,Usuario> p1, Pair<Integer, Usuario> p2 ) {
        return distance(p1.getSecond().getCoordDestinoX(),
                p1.getSecond().getCoordDestinoY(),
                p2.getSecond().getCoordOrigenX(),
                p2.getSecond().getCoordOrigenY());
    }
    public static int distance_source_to_dest(Pair<Integer,Usuario> p1, Pair<Integer, Usuario> p2 ) {
        return distance( p1.getSecond().getCoordOrigenX(),
                p1.getSecond().getCoordOrigenY(),
                p2.getSecond().getCoordDestinoX(),
                p2.getSecond().getCoordDestinoY());
    }
    public static int distanceHW(Pair<Integer, Usuario> pasajero) {
        return distance(pasajero.getSecond().getCoordOrigenX(),
                pasajero.getSecond().getCoordOrigenY(),
                pasajero.getSecond().getCoordDestinoX(),
                pasajero.getSecond().getCoordDestinoY());
    }


}