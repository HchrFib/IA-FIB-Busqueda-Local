import aima.search.framework.HeuristicFunction;

// la heuristica se calcula mediante una suma ponderada de la entropia, la distancia total recorrida por todos los cohes
// y el numero total de coches de la siguiente manera:
// heuristica = pond_entropia*entropia + pond_coches*num_coches + pond_distancia*distancia_total
public class Heuristica implements HeuristicFunction {
    static private double pond_entropia = 1, pond_distancia = 0;
    static private int max_coches = 1000000;
    static private double penalizacion = 10;

    static public void setPonderaciones (double pond_entropia, double pond_distancia) {
        Heuristica.pond_entropia = pond_entropia;
        Heuristica.pond_distancia = pond_distancia;
    }

    static public void setMaxCoches (int max_coches) {
        Heuristica.max_coches = max_coches;
    }

    static public void setPenalizacion (double penalizacion) {
        Heuristica.penalizacion = penalizacion;
    }
    public boolean equals (Object obj) {
        return super.equals(obj);
    }

    public double getHeuristicValue (Object state) {
        Estado estado = (Estado) state;
        double valor = pond_entropia*estado.getEntropia() + pond_distancia*estado.getTotalDistance();
        if (estado.getNumCoches() > max_coches)
            valor += penalizacion*(estado.getNumCoches()-max_coches);
        return valor;
    }
}
