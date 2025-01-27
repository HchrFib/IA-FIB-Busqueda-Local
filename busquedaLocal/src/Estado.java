import  IA.Comparticion.Usuario;
import  IA.Comparticion.Usuarios;
import aima.search.framework.Successor;


import java.io.Serial;
import java.util.*;
import java.lang.Math;

import static java.lang.Math.*;

/**
 * Clase que define un estado.
 * @author IA
 * @version X
 */
public class Estado implements Cloneable {
    /**
     * Propiedad que contiene el itinerario (es decir, el conductor y sus pasajeros asigandos)
     */
    private ArrayList<Datos> itinerario;
    /**
     * Mapeo de cada usuario con su posicion en itinerario
     */
    private  HashMap<Integer,Integer> usuario_itinerario;
    /**
     * Lista de usuarios de conductores que estan actuando como pasajeros
     */
    private ArrayList<Pair<Integer,Usuario>> conductoresNoUsados;
    //Constructores
    private static int id;

    static final int MAX = 300;
    private int num_usuarios;

    /**
     * Constructor por defecto
     */
    public Estado() {
        this.itinerario = new ArrayList<>();
        this.usuario_itinerario = new HashMap<>();
        id = 0;
        conductoresNoUsados = new ArrayList<>();
    }

    /**
     * Constructor con parámetros
     * @param itinerario contiene la asignación de conductores con sus respectivos pasajeros.
     * @param usuario_itinerario diccionario que contiene las asignaciones de usuarios a sus respectivos itinerarios.
     */
    public Estado(ArrayList<Datos> itinerario, HashMap<Integer,Integer> usuario_itinerario) {
        this.itinerario = itinerario;
        this.usuario_itinerario = usuario_itinerario;
    }

    public Object clone() {
        Estado nuevo = new Estado();
        nuevo.num_usuarios = num_usuarios;
        nuevo.itinerario = new ArrayList<>();
        for (Datos d : itinerario) nuevo.itinerario.add((Datos)d.clone());
        nuevo.usuario_itinerario = new HashMap<>();
        nuevo.usuario_itinerario.putAll(usuario_itinerario);
        nuevo.conductoresNoUsados = new ArrayList<>();
        nuevo.conductoresNoUsados.addAll(conductoresNoUsados);
        return nuevo;
    }

    public int getNumUsers () {
        return num_usuarios;
    }

    //Getters

    public double getEntropia () {
        double total = 0;
        for (Datos d : itinerario)
            total += d.getEntropia(MAX);
        return total;
    }

    public double getDistanciaTotal () {
        double total = 0;
        for (Datos d : itinerario)
            total += d.getDistancia();
        return total;
    }

    public double getNumConductoresUsados() {
        return itinerario.size();
    }

    /**
     * Función que devuelve un itinerario
     * @return devuelve un itinerario.
     */
    public ArrayList<Datos> getItinerario() {
        return itinerario;
    }
    /**
     * Consultora que devuelve un diccionario que contiene las asignaciones de usuario a un itienerario.
     * @return devuelve un usuario-itinerario.
     */
    public HashMap<Integer, Integer> getUsuario_itinerario() {
        return usuario_itinerario;
    }

    /**
     * Consultora que devuelve una lista de los usuarios que son usados como pasajeros.
     * @return devuelve conductoresNoUsados.
     */
    public ArrayList<Pair<Integer, Usuario>> getConductoresNoUsados() {
        return conductoresNoUsados;
    }
//Setters
    /**
     * Modificadora que establece un itinerario
     * @param itinerario contiene la asignación de conductores con sus respectivos pasajeros.
     */
    public void setItinerario(ArrayList<Datos> itinerario) {
        this.itinerario = itinerario;
    }

    /**
     * Modificadora que establece un diccionario con las asignaciones de usuarios a un itinerario.
     * @param usuario_itinerario contiene la asignación de conductores con sus respectivos pasajeros.
     */
    public void setUsuario_itinerario(HashMap<Integer, Integer> usuario_itinerario) {
        this.usuario_itinerario = usuario_itinerario;
    }

    /**
     *
     * Función que generadora de soluciones iniciales
     * @param listaUsuarios contiene la lista de usuarios del servicio de compartición de coches.
     * @param opcion solución inicial a instanciar.
     */
    public boolean generadorSolucionesIniciales(Usuarios listaUsuarios, int opcion) {
        num_usuarios = listaUsuarios.size();
        ArrayList<Pair<Integer, Usuario>> conductores = new ArrayList<>();
        ArrayList<Pair<Integer, Usuario>> pasajeros = new ArrayList<>();
        filtrar(listaUsuarios,conductores, pasajeros);

        switch (opcion) {
            case 0:
                solucionInicialAleatoria(conductores, pasajeros);
                return true;
            case 1:
                return solucionInicial2(conductores, pasajeros);
            case 2:
                return solucionInicial3(conductores, pasajeros);
            default:
                System.out.println("Metodo de resolucion no valido.");
                return false;
        }
    }
    /**
     *
     * Función auxiliar que genera dos vectores con la lista de conductores y usuarios
     * @param listUsuarios listaUsuarios contiene la lista de usuarios del servicio de compartición de coches.
     * @param conductores contiene la lista de usuarios que hacen de conductores.
     * @param pasajeros   contiene la lista de usuarios que hacen pasajeros
     */
    public void filtrar(Usuarios listUsuarios, ArrayList<Pair<Integer,Usuario>> conductores
                                             , ArrayList<Pair<Integer,Usuario>> pasajeros) {

        for(Usuario usuario: listUsuarios) {
            Pair<Integer, Usuario> u = new Pair<>(id, usuario);
            if(usuario.isConductor()) conductores.add(u);
            else pasajeros.add(u);
            ++id;
        }
    }

    /**
     * Método que genera una asignación de forma aleatoria de conductores y pasajeros
     * @param conductores contiene la lista de conductores
     * @param pasajeros contiene la lista de pasajeros
     */
    public void solucionInicialAleatoria(ArrayList<Pair<Integer,Usuario>> conductores,
                                     ArrayList<Pair<Integer,Usuario>> pasajeros) {
        System.out.println("Solucion inicial aleatoria");
        ArrayList<Pair<Integer,Usuario>> auxPas;
        auxPas = pasajeros;
        int numP =  pasajeros.size()/ conductores.size(); //numero de pasajeros por coche
        System.out.println(numP);

        for (Pair<Integer, Usuario> conductor : conductores) {

            ArrayList<Pair<Integer, Usuario>> aux = new ArrayList<>();
            for (int k = 0; k < numP; ++k) {

                int a = (int) (Math.random() * (auxPas.size() - 1));
                aux.add(auxPas.get(a));
                aux.add(auxPas.get(a));
                auxPas.remove(a);
            }
            Datos d = new Datos(conductor, aux);
            itinerario.add(d);
        }

        //sobran
        for(Pair<Integer,Usuario> pasajero : pasajeros) {
            int i = (int) (Math.random()* (itinerario.size()-1));

            /*int newDistance = itinerario.get(i).getDistancia();
            Usuario lastPasajero = itinerario.get(i).getPasajeros().get(itinerario.get(i).getPasajeros().size() - 1).getSecond();
            Usuario conductor = itinerario.get(i).getConductor().getSecond();
            newDistance -= distance(lastPasajero.getCoordDestinoX(), lastPasajero.getCoordDestinoY(), conductor.getCoordDestinoX(), conductor.getCoordDestinoY());*/
            int size = itinerario.get(i).getPasajeros().size();
            int newDistance = Calc.distAdd(itinerario.get(i),pasajero.getSecond(),size,size+1);

            itinerario.get(i).getPasajeros().add(new Pair<>(pasajero.getFirst(),pasajero.getSecond()));
            itinerario.get(i).getPasajeros().add(new Pair<>(pasajero.getFirst(),pasajero.getSecond()));

            /*newDistance += distance(lastPasajero.getCoordDestinoX(), lastPasajero.getCoordDestinoY(), pasajero.getSecond().getCoordOrigenX(), pasajero.getSecond().getCoordOrigenY());
            newDistance += distance(pasajero.getSecond().getCoordOrigenX(), pasajero.getSecond().getCoordOrigenY(), pasajero.getSecond().getCoordDestinoX(), pasajero.getSecond().getCoordDestinoY());
            newDistance += distance(pasajero.getSecond().getCoordDestinoX(), pasajero.getSecond().getCoordDestinoY(), conductor.getCoordDestinoX(), conductor.getCoordDestinoY());*/

            itinerario.get(i).setDistancia(newDistance);
        }

    }
    /**
     * Método devuelve id de itinerario al cual pertenece un conductor.
     * @param id identificador de un conductor.
     * @return devuelve el identificador al cual pertenece el conductor con identificador id.
     */
    private int indexById(int id) {
        for (int i = 0; i < itinerario.size(); ++i) {
            if (itinerario.get(i).getConductor().getFirst() == id) {
                return i;
            }
        }
        return -1;
    }




    /**
     * Método que general la solución Inicial asignamos un pasajero al conductor mas cercano.
     * @param conductores contiene la lista de conductores.
     * @param pasajeros   contiene la lista de pasajeros.
     */
    public boolean solucionInicial2(ArrayList<Pair<Integer,Usuario>> conductores
            , ArrayList<Pair<Integer,Usuario>> pasajeros) {

        System.out.println("Solucion inicial 2");

        //Inicializar itinerario con todos los conductores
        for (Pair<Integer,Usuario> conductor: conductores) {
            Datos d = new Datos(conductor,new ArrayList<>());
            itinerario.add(d);
            usuario_itinerario.put(conductor.getFirst() , itinerario.indexOf(d));
        }

        //Asignar pasajeros a su conductor más cercano posible
        for (Pair<Integer,Usuario> pasajero: pasajeros) {
            int minDist = 300;
            int idCond = -1;
            for (int i = 0; i < itinerario.size(); ++i) {
                int aux = Calc.calcDistUsuarios(pasajero.getSecond(), itinerario.get(i).getConductor().getSecond());
                if ((Calc.distAdd(itinerario.get(i),pasajero.getSecond(),itinerario.get(i).getPasajeros().size(),itinerario.get(i).getPasajeros().size()+1) < MAX) && (aux < minDist)) { minDist = aux; idCond = itinerario.get(i).getConductor().getFirst(); }
            }
            if (idCond == -1) return false;

            int index = usuario_itinerario.get(idCond);
            int newDistance = Calc.distAdd(itinerario.get(index),pasajero.getSecond(),itinerario.get(index).getPasajeros().size(),itinerario.get(index).getPasajeros().size()+1);
            itinerario.get(index).getPasajeros().add(pasajero);
            itinerario.get(index).getPasajeros().add(pasajero);
            usuario_itinerario.put(pasajero.getFirst(),index);
            itinerario.get(index).setDistancia(newDistance);
        }
        return true;
    }

    public boolean solucionInicial3(ArrayList<Pair<Integer,Usuario>> conductores,
                                    ArrayList<Pair<Integer,Usuario>> pasajeros) {

        System.out.println("================== Solucion inicial 3 ================ ");
        System.out.println("====================================================== ");
        int num_pasajeros = pasajeros.size();
        int num_conductores = conductores.size();
        int numP =  num_pasajeros/ num_conductores; //num de pasajeros por coche
        for (Pair<Integer, Usuario> conductor : conductores) {

            ArrayList<Pair<Integer, Usuario>> aux = new ArrayList<>();
            Datos d = new Datos(conductor, aux);
            itinerario.add(d);
            int index = itinerario.indexOf(d);
            usuario_itinerario.put(conductor.getFirst(),index);

            int dist = 0;
            int k = 0;
            boolean excede_capacidad = false;
            while(!excede_capacidad && k < numP ) {
                int a = Calc.generarNumAleatorio(pasajeros.size()-1);
                Pair<Integer, Usuario> pasajero = pasajeros.get(a);
                Pair<Integer, Integer> distTrayecto = Calc.calculaDistanciaTrayecto(dist, d, conductor, pasajero);
                dist = distTrayecto.getFirst();
                if(dist + distTrayecto.getSecond() > 300) {
                    if(d.getPasajeros().isEmpty()) conductoresNoUsados.add(conductor);
                    excede_capacidad = true;
                }
                else  {
                    aux.add(pasajero);
                    aux.add(pasajero);
                    usuario_itinerario.put(pasajero.getFirst(), index);
                    pasajeros.remove(a);
                    itinerario.get(index).setDistancia(dist + distTrayecto.getSecond());
                }
                ++k;
            }
        }
        //Intentamos colocar pasajeros a itinerarios vacios para no tener que crear nuevos conductores.
        int i = 0;
        while( i < pasajeros.size()) {
            Pair<Integer, Usuario> pasajero = pasajeros.get(i);
            int dist = 0;
            int j = 0;
            while(j < conductoresNoUsados.size()) {
                Pair<Integer, Usuario> conductor = conductoresNoUsados.get(j);
                int idItinerario = usuario_itinerario.get(conductor.getFirst());
                Datos d =  itinerario.get(idItinerario);
                Pair<Integer, Integer> disTrayecto = Calc.calculaDistanciaTrayecto(dist,d, conductor, pasajero);
                dist = disTrayecto.getFirst();

                if(dist + disTrayecto.getSecond() <= 300) {
                    d.getPasajeros().add(pasajero);
                    pasajeros.remove(pasajero);
                    conductoresNoUsados.remove(conductor);
                    itinerario.get(idItinerario).setDistancia(dist + disTrayecto.getSecond());
                }
                ++j;
            }
            ++i;
        }

        int index = -1;
        int dist = 0;
        Usuario u_conductor = new Usuario(-1,-1,-1, -1,false);

        ArrayList<Pair<Integer, Usuario>> aux = new ArrayList<>();
        Pair<Integer, Usuario> conductor = new Pair<>();
        Datos d = new Datos();

        if(pasajeros.size()> 0) {
            u_conductor = addCar();
            conductor = new Pair<>(id, u_conductor);
            conductores.add(conductor);
            d = new Datos(conductor, aux);
            ++id;
            ++num_conductores;
            itinerario.add(d);
            index =  itinerario.indexOf(d);
        }

        while(pasajeros.size() > 0) {

            if(num_pasajeros <= num_conductores) return false;

            int a = Calc.generarNumAleatorio (pasajeros.size() - 1);

            Pair<Integer, Usuario> pasajero  = pasajeros.get(a);
            Pair<Integer, Integer> distTrayecto = Calc.calculaDistanciaTrayecto(dist, d, conductor, pasajero);
            dist = distTrayecto.getFirst();

            if(dist + distTrayecto.getSecond() > 300) {

                dist = 0;
                u_conductor = addCar();
                conductor = new Pair<>(id, u_conductor);
                conductores.add(conductor);
                aux = new ArrayList<>();
                d = new Datos(conductor, aux);
                ++id;
                ++num_conductores;
                itinerario.add(d);
                index =  itinerario.indexOf(d);

            } else {
                aux.add(pasajero);
                aux.add(pasajero);
                usuario_itinerario.put(pasajero.getFirst(), index);
                pasajeros.remove(a);
                itinerario.get(index).setPasajeros(aux);
                itinerario.get(index).setDistancia(dist + distTrayecto.getSecond());
            }
        }
        System.out.println("#Itinerarios: " + itinerario.size());
        System.out.println("#Pasajeros: " + num_pasajeros + " #conductores: " + num_conductores);
        System.out.println("#num P restantes: " + pasajeros.size());

        return true;
    }
    public Usuario addCar() {
        int semilla = (int) (Math.random() * 50);
        return new Usuarios(1,1,semilla).get(0);
    }

    //OPERADORES
    public void insertar_pasajero(int idConductor, Pair<Integer, Usuario> pasajero) {
        //
    }
    public void cambiarPasajero(Pair<Integer, Usuario> pasajero, Datos itinerarioInicial, Datos itinerarioFinal){
        if (!itinerarioInicial.getConductor().equals(pasajero)){
            itinerarioInicial.removePasajero(pasajero);
            itinerarioFinal.addPasajero(pasajero);
        }
    }

    /**
     * Operador mover pasajero.
     * @param pasajero contiene un pasajero asigando a un coche de compartición.
     * @param conductorIni contiene el conductor inicial.
     * @param conductorFin contiene el conductor final.
     * @param indiceIni contiene el indice inicial.
     * @param indiceFin contiene el indice final.
     */
    public void moverPasajero(Pair<Integer, Usuario> pasajero, Pair<Integer, Usuario> conductorIni, Pair<Integer, Usuario> conductorFin, int indiceIni, int indiceFin) {
        int indexCondIni = indexById(conductorIni.getFirst());
        int indexCondFin = indexById(conductorFin.getFirst());
        int sizeFin = itinerario.get(indexCondFin).getPasajeros().size();

        //Quitar de posicion actual
        itinerario.get(indexById(conductorIni.getFirst())).getPasajeros().remove(pasajero);
        itinerario.get(indexById(conductorIni.getFirst())).getPasajeros().remove(pasajero);
        int newDist = Calc.distQuitar(itinerario.get(indexCondIni),pasajero.getSecond(),pasajero.getFirst());
        itinerario.get(indexCondIni).setDistancia(newDist);

        //Poner en posicion nueva
        itinerario.get(indexById(conductorFin.getFirst())).getPasajeros().add(indiceIni, pasajero);
        itinerario.get(indexById(conductorFin.getFirst())).getPasajeros().add(indiceFin, pasajero);
        newDist = Calc.distAdd(itinerario.get(indexCondFin),pasajero.getSecond(), sizeFin, sizeFin+1);
        itinerario.get(indexCondFin).setDistancia(newDist);
    }

    public void moverPasajeroTest() {
        Pair<Integer, Usuario> pasajero = itinerario.get(0).getPasajeros().get(0);
        Pair<Integer, Usuario> conductorIni = itinerario.get(0).getConductor();
        Pair<Integer, Usuario> conductorFin = itinerario.get(1).getConductor();
        moverPasajero(pasajero,conductorIni,conductorFin,0,1);
        System.out.print(itinerarioToString());
    }

    /**
     * Operador anadir conductor.
     * @param id identificador del conductor que queremos anadir a la solucion.
     */
    public void addConductor(Integer id) {
        //Inicializacion no importante
        Usuario condAdd = new Usuario(0,0,0,0,false);

        //Encontrar conductor con id = parametro
        for (Pair<Integer,Usuario> c : conductoresNoUsados) {
            if (c.getFirst() == id) { condAdd = c.getSecond(); break; }
        }

        //Eliminar conductor como pasajero
        int index = usuario_itinerario.get(id);
        int newDist = Calc.distQuitar(itinerario.get(index),condAdd, id);
        itinerario.get(index).getPasajeros().remove(new Pair(id,condAdd));
        itinerario.get(index).setDistancia(newDist);
        conductoresNoUsados.remove(new Pair(id,condAdd));

        //Add como conductor
        itinerario.add(new Datos(new Pair(id, condAdd),new ArrayList<>()));  //Calculo de distancia se hace en el constructor
        usuario_itinerario.put(id, itinerario.size() - 1);
    }

    /**
     * Operador quitar conductor.
     * @param id1 identificador del conductor que queremos quitar a la solucion.
     * @param id2 identificador del conductor al que le anadiremos el usuario id1.
     */
    public void eliminarConductor(Integer id1, Integer id2) {
        Pair<Integer, Usuario> cond = itinerario.get(usuario_itinerario.get(id1)).getConductor(); //Conductor que quitamos
        int index = usuario_itinerario.get(id2);  //index del conductor al que anadimos un nuevo pasajero

        //Eliminar conductor (no tiene pasajeros)
        itinerario.remove(usuario_itinerario.get(id1));

        //Add como pasajero
        int size = itinerario.get(index).getPasajeros().size();
        int newDistance = Calc.distAdd(itinerario.get(index),cond.getSecond(),size,size+1);
        itinerario.get(index).setDistancia(newDistance);
        itinerario.get(index).getPasajeros().add(cond);  //Recoger pasajero
        itinerario.get(index).getPasajeros().add(cond);  //Dejar pasajero
    }

    public int getTotalDistance () {
        int dist_sum = 0;
        for (Datos d : itinerario)
            dist_sum += d.getDistancia();
        return dist_sum;
    }

    public int getNumCoches () {
        return itinerario.size();
    }

    public String itinerarioToString() {
        Heuristica h = new Heuristica();
        String output = "Solucion (coste: " + (round(h.getHeuristicValue(this)*1e4)/1e4) +
                        " #itin: " + getNumCoches() + " dist total: " + getTotalDistance() + "):\n";
        for (Datos d : itinerario) {
            output +=   "*  Itinerario: " + String.valueOf(itinerario.indexOf(d)) + "\n" +
                        "   Ruta: " + d.toString() + "\n" +
                        "   Conductor:\n" +
                        "      " + usuarioToString(d.getConductor()) + "\n" +
                        "   Pasajeros: \n";
            ArrayList<Integer> mostrados = new ArrayList<>();
            for (Pair<Integer, Usuario> pasajero : d.getPasajeros()) {
                if (!mostrados.contains(pasajero.getFirst())) {
                    output += "      " + usuarioToString(pasajero) + "\n";
                    mostrados.add(pasajero.getFirst());
                }
            }
            output += "\n";
        }
        return output;
    }


    public String usuarioToString (Pair<Integer,Usuario> pasajero) {
        return "[id: " + pasajero.getFirst() + " (" + pasajero.getSecond().getCoordOrigenX() + "," +
                                                    pasajero.getSecond().getCoordOrigenY() + ") -> (" +
                                                    pasajero.getSecond().getCoordDestinoX() + "," +
                                                    pasajero.getSecond().getCoordDestinoY() + ")]";
    }

    public String toString() {
        return this.itinerarioToString();
    }
}
