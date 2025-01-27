import IA.Comparticion.Usuarios;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

/**
 * Clase principal
 * @author IA
 * @version X
 */
public class Main {
    /**
     * Clase principal de la Demo
     * @param args (opcional)
     */
    public static void main(String[] args) {
        // posible configuración de los solvers como por ejemplo
        // SucesorHC.cambiarConjuntoOperadores(0);
        Heuristica.setPonderaciones(1, 0);
        Heuristica.setPenalizacion(0);

        Scanner s = new Scanner(System.in);
        System.out.print("Quieres ejecutar un experimento? [O: No, 1: Sí]: ");
        if (s.nextInt() == 1) {
            System.out.print("Introducir el número de experimento [1:8]: ");
            switch (s.nextInt()) {
                case 1:
                    test1();
                    break;
                case 2:
                    ejecutarExp2();
                    break;
                case 3:
                    testSA();
                    break;
                case 4:
                    test4();
                    break;
                case 5:
                    test5();
                    break;
                case 6:
                    test6();
                    break;
                case 7:
                    test7();
                    break;
                case 8:
                    ejecutarSolver(200, 100, 1234, 1, true, false);
                    break;
            }
        } else {
            solverConInputUsuario();
        }


    }

    private static void solverConInputUsuario () {
        Scanner s = new Scanner(System.in);
        System.out.print("Introducir el número de usuarios: ");
        int numUsuarios = s.nextInt();
        System.out.print("Introducir el número de conductores: ");
        int numConductores = s.nextInt();
        System.out.print("Introducir la semilla: ");
        int semilla = s.nextInt();
        System.out.print("Método de resolución [1: random, 2: metodo2, 3: metodo3]: ");
        int sol_type = s.nextInt()-1;
        System.out.println("\n");

        ejecutarSolver(numUsuarios, numConductores, semilla, sol_type, true, true);
    }

    private static String ejecutarSolver (int N, int M, int seed, int sol_type, boolean HC, boolean SA) {
        Usuarios listaUsuarios = new Usuarios(N,M,seed);
        Estado e = new Estado();
        Heuristica.setMaxCoches(M);

        if (e.generadorSolucionesIniciales(listaUsuarios, sol_type)) {
            System.out.print(e.itinerarioToString());

            System.out.println("==================== Final solution ================== ");
            System.out.println("====================================================== ");
            System.out.println();
            String s = "notFound";
            if (HC)
                hillClimbingSearch(e);
            s = String.valueOf(e.getEntropia());
            if (HC && SA)
                System.out.println("====================================================== ");
            if (SA)
                simulatedAnnealingSearch(e);
            return s;
        } else System.out.println("SOLUCION INICIAL NO ENCONTRADA");
        return "NotFound";
    }

    private static void test1() {
        List<String> sol1 = new ArrayList<String>();
        List<String> sol2 = new ArrayList<String>();
        String r1;
        String r2;

        int numExperimentos = 50;

        for (int i = 0; i < numExperimentos; i++) {
            Random random = new Random();
            int semillaAleatoria = random.nextInt(2000);
            SucesorHC.cambiarConjuntoOperadores(0);
            r1 = ejecutarSolver(200, 100, semillaAleatoria, 0, true, false);
            sol1.add(r1);
            SucesorHC.cambiarConjuntoOperadores(1);
            r2 = ejecutarSolver(200, 100, semillaAleatoria, 0, true, false);
            sol2.add(r2);
            System.out.println(""+i + "," + r1 + "," + r2);
        }
        escribirReporte4("Data", sol1, sol2, numExperimentos);
    }

    private static void ejecutarExp2() {
        List<String> sol1 = new ArrayList<String>();
        List<String> sol2 = new ArrayList<String>();
        String r1;
        String r2;

        SucesorHC.cambiarConjuntoOperadores(1);
        int numExperimentos = 50;

        for (int i = 0; i < numExperimentos; i++) {
            Random random = new Random();
            int semillaAleatoria = random.nextInt(2000);
            r1 = ejecutarSolver(200, 100, semillaAleatoria, 0, true, false);
            sol1.add(r1);
            r2 = ejecutarSolver(200, 100, semillaAleatoria, 1, true, false);
            sol2.add(r2);
            System.out.println(""+i + "," + r1 + "," + r2);
        }
        escribirReporte("Data", sol1, sol2, numExperimentos);
    }

    private static void hillClimbingSearch(Estado estado) {
        System.out.println("\nHillClimbing  -->");
        try {
            Problem problem = new Problem(estado, new SucesorHC(), new TestObjetivo(), new Heuristica());
            Search search = new HillClimbingSearch();
            long t_start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem, search);
            long t_end = System.currentTimeMillis();

            System.out.println();
            printActions(agent.getActions());
            System.out.println("\n");
            printInstrumentation(agent.getInstrumentation());
            System.out.print("Duracion de la busqueda: ");
            System.out.print(t_end-t_start);
            System.out.println(" ms.\n");
            System.out.println("Solucion encontrada:\n");
            System.out.println(SucesorHC.last);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void simulatedAnnealingSearch(Estado estado) {
        System.out.println("\nSimulated Annealing  -->");
        try {
            Problem problem =  new Problem(estado, new SucesorSA(), new TestObjetivo(),new Heuristica());
            SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.001);
            //search.traceOn();
            long t_start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem,search);
            long t_end = System.currentTimeMillis();

            System.out.println();
            printActions(agent.getActions());

            System.out.println("\n");
            System.out.print("Duracion de la busqueda: ");
            System.out.print(t_end-t_start);
            System.out.println(" ms.");
            printInstrumentation(agent.getInstrumentation());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSA() {
        for (int i = 0; i < 50; ++i) {
            System.out.println("===============SEED " + i + "==================");
            Usuarios listaUsuarios = new Usuarios(200,100,i);
            Estado e = new Estado();

            if(e.generadorSolucionesIniciales(listaUsuarios, 0)) {
                System.out.println("\nSimulated Annealing steps 5000 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(5000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing steps 10000 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(10000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing steps 20000 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing steps 30000 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(30000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing steps 50000 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(50000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
            else System.out.println("SOLUCION INICIAL NO ENCONTRADA");

            if(e.generadorSolucionesIniciales(listaUsuarios, 0)) {
                System.out.println("\nSimulated Annealing stiter 25 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,25,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing wtiter 50 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing stiter 100 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,100,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing stiter 200 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,200,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing stiter 300 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,300,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
            else System.out.println("SOLUCION INICIAL NO ENCONTRADA");


            if(e.generadorSolucionesIniciales(listaUsuarios, 0)) {
                System.out.println("\nSimulated Annealing k 1 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,1,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing k 5 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing k 15 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,15,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing k 50 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,50,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                System.out.println("\nSimulated Annealing k 100 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,100,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();
                    System.out.println();
                    printActions(agent.getActions());
                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
            else System.out.println("SOLUCION INICIAL NO ENCONTRADA");

            if(e.generadorSolucionesIniciales(listaUsuarios, 0)) {
                System.out.println("\nSimulated Annealing k 0.01 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.01);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();

                    System.out.println();
                    printActions(agent.getActions());

                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());

                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                System.out.println("\nSimulated Annealing lambd 0.001 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();

                    System.out.println();
                    printActions(agent.getActions());

                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());

                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                System.out.println("\nSimulated Annealing lambd 0.0001 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.0001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();

                    System.out.println();
                    printActions(agent.getActions());

                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());

                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                System.out.println("\nSimulated Annealing lambd 0.00001 -->");
                try {
                    Problem problem =  new Problem(e, new SucesorSA(), new TestObjetivo(),new Heuristica());
                    SimulatedAnnealingSearch search =  new SimulatedAnnealingSearch(20000,50,5,0.00001);
                    //search.traceOn();
                    long t_start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem,search);
                    long t_end = System.currentTimeMillis();

                    System.out.println();
                    printActions(agent.getActions());

                    System.out.println("\n");
                    System.out.print("Duracion de la busqueda: ");
                    System.out.print(t_end-t_start);
                    System.out.println(" ms.");
                    printInstrumentation(agent.getInstrumentation());

                } catch (Exception exc) {
                    exc.printStackTrace();
                }

            }
            else System.out.println("SOLUCION INICIAL NO ENCONTRADA");
        }
    }

    private static void test4() {
        for (int i = 0; i < 7; ++i) {
            System.out.println("===============SEED " + i + "==================");

            for (int N = 200; N <= 500; N += 100) {
                System.out.println("N = " + N);
                Usuarios listaUsuarios = new Usuarios(N,N/2,i);
                Estado e = new Estado();

                if(e.generadorSolucionesIniciales(listaUsuarios, 0)) {
                    try {
                        Problem problem = new Problem(e, new SucesorHC(), new TestObjetivo(), new Heuristica());
                        Search search = new HillClimbingSearch();
                        long t_start = System.currentTimeMillis();
                        SearchAgent agent = new SearchAgent(problem, search);
                        long t_end = System.currentTimeMillis();

                        System.out.print("Duracion de la busqueda: ");
                        System.out.print(t_end-t_start);
                        System.out.println(" ms.\n");
                        System.out.println("Solucion encontrada:\n");
                        System.out.println(SucesorHC.last);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
                else System.out.println("SOLUCION INICIAL NO ENCONTRADA");
            }

        }
    }

    private static void test5() {
        List<ArrayList<String>> sol1 = new ArrayList<>();
        List<ArrayList<String>> sol2 = new ArrayList<>();
        ArrayList<String> a;
        String distancia;
        long tiempoInicio,tiempoFin, duracion;
        double segundos;

        SucesorHC.cambiarConjuntoOperadores(1);
        int numExperimentos = 50;

        for (int i = 0; i < numExperimentos; i++) {
            System.out.println("Exp No. " + i + "\n");
            //Utils
            Random random = new Random();
            int semillaAleatoria = random.nextInt(2000);

            //Primera heuristica
            a = new ArrayList<>();
            Heuristica.setPonderaciones(1,0);
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, 100, semillaAleatoria, 0, true, false);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos primera heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol1.add(a);
            System.out.println("a.");
            System.out.println(""+distancia + "," + segundos);

            //Segunda Heuristica
            a = new ArrayList<>();
            Heuristica.setPonderaciones(0,1);
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, 100, semillaAleatoria, 0, true, false);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos segunda heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol2.add(a);
            System.out.println("b.");
            System.out.println(""+distancia + "," + segundos);

        }

        escribirReporte2("Data", sol1,sol2, numExperimentos);
    }

    private static void test6() {
        List<ArrayList<String>> sol1 = new ArrayList<>();
        List<ArrayList<String>> sol2 = new ArrayList<>();
        ArrayList<String> a;
        String distancia;
        long tiempoInicio,tiempoFin, duracion;
        double segundos;

        SucesorHC.cambiarConjuntoOperadores(1);
        int numExperimentos = 50;

        for (int i = 0; i < numExperimentos; i++) {
            System.out.println("Exp No. " + i + "\n");
            //Utils
            Random random = new Random();
            int semillaAleatoria = random.nextInt(2000);

            //Primera heuristica
            a = new ArrayList<>();
            Heuristica.setPonderaciones(1,0);
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, 100, semillaAleatoria, 0, false, true);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos primera heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol1.add(a);
            System.out.println("a.");
            System.out.println(""+distancia + "," + segundos);

            //Segunda Heuristica
            a = new ArrayList<>();
            Heuristica.setPonderaciones(0,1);
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, 100, semillaAleatoria, 0, false, true);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos segunda heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol2.add(a);
            System.out.println("b.");
            System.out.println(""+distancia + "," + segundos);

        }

        escribirReporte2("Data", sol1,sol2, numExperimentos);
    }

    private static void test7() {
        List<ArrayList<String>> sol1 = new ArrayList<>();
        List<ArrayList<String>> sol2 = new ArrayList<>();
        ArrayList<String> a;
        String distancia;
        long tiempoInicio,tiempoFin, duracion;
        double segundos;

        SucesorHC.cambiarConjuntoOperadores(1);
        int numExperimentos = 50;

        for (int i = 0; i < numExperimentos; i++) {
            System.out.println("Exp No. " + i + "\n");
            //Utils
            Random random = new Random();
            int semillaAleatoria = random.nextInt(2000);
            Heuristica.setPonderaciones(1,0);

            //Primera heuristica
            a = new ArrayList<>();
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, (int) Math.round(200*0.37), semillaAleatoria, 1, false, true);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos primera heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol1.add(a);
            System.out.println("a.");
            System.out.println(""+distancia + "," + segundos);

            //Segunda Heuristica
            a = new ArrayList<>();
            tiempoInicio = System.currentTimeMillis();
            distancia = ejecutarSolver(200, 100, semillaAleatoria, 1, false, true);
            tiempoFin = System.currentTimeMillis(); // Obtener el tiempo de finalización
            duracion = tiempoFin - tiempoInicio; // Calcular la duración en milisegundos
            segundos = duracion / 1000.0;

            //Guardar datos segunda heurística
            a.add(distancia);
            a.add(String.valueOf(segundos));
            sol2.add(a);
            System.out.println("b.");
            System.out.println(""+distancia + "," + segundos);

        }

        escribirReporte3("Data", sol1,sol2, numExperimentos);
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = actions.get(i).toString();
            System.out.println(action);
        }
    }

    private static void escribirReporte(String archivo, List<String> sol1, List<String> sol2, int numExperimentos) {
        try {
            FileWriter writer = new FileWriter(archivo);

            // Escribir el encabezado del archivo CSV
            writer.write("No.,Solucion Inicial 1,Solucion Inicial 2\n");

            // Escribir algunas filas de datos
            for (int i = 0; i < numExperimentos; i++) {
                writer.write(i + "," + sol1.get(i) + "," + sol2.get(i) + "\n");
            }
            writer.close();
            System.out.println("Archivo CSV con los resultados creado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al crear archivo CSV: " + e.getMessage());
        }
        System.out.println("Resultados:");
        System.out.println(sol1);
        System.out.println(sol2);
    }

    private static void escribirReporte2(String archivo,  List<ArrayList<String>> sol1, List<ArrayList<String>> sol2, int numExperimentos) {
        try {
            FileWriter writer = new FileWriter(archivo);

            // Escribir el encabezado del archivo CSV
            writer.write("Heuristica, distancia, tiempo\n");

            // Escribir algunas filas de datos
            for (int i = 0; i < numExperimentos; i++) {
                writer.write( "1," + sol1.get(i).get(0) + "," + sol1.get(i).get(1) + "\n");
                writer.write( "2," + sol2.get(i).get(0) + "," + sol2.get(i).get(1) + "\n");
            }
            writer.close();
            System.out.println("Archivo CSV con los resultados creado exitosamente.");
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error al crear archivo CSV: " + e.getMessage());
        }

        List<String> lst = new ArrayList<>();
        System.out.println("Resultados: Heuristica 1: distancia ");


        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol1.get(i).get(0));
        }

        System.out.println(lst);
        System.out.println("Resultados: Heuristica 1: Tiempo ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol1.get(i).get(1));
        }

        System.out.println(lst);
        System.out.println("Resultados: Heuristica 2: distancia ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol2.get(i).get(0));
        }

        System.out.println(lst);
        System.out.println("Resultados: Heuristica 2: Tiempo ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol2.get(i).get(1));
        }

        System.out.println(lst);
    }

    private static void escribirReporte3(String archivo,  List<ArrayList<String>> sol1, List<ArrayList<String>> sol2, int numExperimentos) {
        try {
            FileWriter writer = new FileWriter(archivo);

            // Escribir el encabezado del archivo CSV
            writer.write("Heuristica, distancia, tiempo\n");

            // Escribir algunas filas de datos
            for (int i = 0; i < numExperimentos; i++) {
                writer.write( "1," + sol1.get(i).get(0) + "," + sol1.get(i).get(1) + "\n");
                writer.write( "2," + sol2.get(i).get(0) + "," + sol2.get(i).get(1) + "\n");
            }
            writer.close();
            System.out.println("Archivo CSV con los resultados creado exitosamente.");
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error al crear archivo CSV: " + e.getMessage());
        }

        List<String> lst = new ArrayList<>();
        System.out.println("Resultados: M=0.37*N: distancia ");


        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol1.get(i).get(0));
        }

        System.out.println(lst);
        System.out.println("Resultados: M=0.37*N: Tiempo ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol1.get(i).get(1));
        }

        System.out.println(lst);
        System.out.println("Resultados: M=N/2: distancia ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol2.get(i).get(0));
        }

        System.out.println(lst);
        System.out.println("Resultados: M=N/2: Tiempo ");

        lst = new ArrayList<>();
        for(int i = 0; i < numExperimentos; i ++){
            lst.add(sol2.get(i).get(1));
        }

        System.out.println(lst);
    }

    private static void escribirReporte4(String archivo, List<String> sol1, List<String> sol2, int numExperimentos) {
        try {
            FileWriter writer = new FileWriter(archivo);

            // Escribir el encabezado del archivo CSV
            writer.write("No.,Operadores 1,Operadores 2\n");

            // Escribir algunas filas de datos
            for (int i = 0; i < numExperimentos; i++) {
                writer.write(i + "," + sol1.get(i) + "," + sol2.get(i) + "\n");
            }
            writer.close();
            System.out.println("Archivo CSV con los resultados creado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al crear archivo CSV: " + e.getMessage());
        }
        System.out.println("Resultados:");
        System.out.println(sol1);
        System.out.println(sol2);
    }

}
