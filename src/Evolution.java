import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Evolution {

    static ArrayList<Node> topology = new ArrayList<Node>();
    static ArrayList<ArrayList<String>> population = new ArrayList<ArrayList<String>>();
    static int PopSize = 50;
    static Node start, goal;
    static int generation = 1;
    static int maxEpochs = 100;
    static Random rd = new Random();
    static double mutationProbability = 0.5;
    static int packagesize = 20;
    static ArrayList<Double> statistics = new ArrayList<Double>(); 

    public static void main(String[] args) {
        importTopology();
        ArrayList<Node> objectives = furthestNodes();
        start = objectives.get(0);
        goal = objectives.get(1);
        System.out.println("Start: " + printAsIndex(start) + "\nGoal: "+ printAsIndex(goal));
        generateFirstPopulation();
        evolution();
        logStats();
    }


    private static void logStats() {
        File myObj = new File("stats.txt");
        try {
            myObj.createNewFile();
            try {
                int counter = 1;
                FileWriter myWriter = new FileWriter("stats.txt");
                for(Double n : statistics){
                    myWriter.write(String.valueOf(counter) +"," +String.valueOf(n)+ "\n");
                    counter++;
                }
                myWriter.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File myObj2 = new File("results.txt");
        try {
            myObj2.createNewFile();
            try {
                FileWriter myWriter = new FileWriter("results.txt");
                myWriter.write(String.valueOf(fitness(population.get(0))));
                myWriter.write("\n");
                myWriter.write(population.get(0).toString());
                myWriter.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void evolution(){
        while(generation <= maxEpochs){
            //rank best fit individuals
            Collections.sort(population,(o1, o2) -> Double.compare(fitness(o2), fitness(o1)));

            //view population statistics: best individual, etc
            statistics.add(fitness(population.get(0)));

            viewPopulationStats();
            //viewPopulation();

            //increase generation
            generation++;

            //apply crossover reproduction
            reproduction();

        }
    }

    // reproduction of new population 
    public static void reproduction(){
        // make a list with the best 25% of the population 
        ArrayList<ArrayList<String>> newPop = new ArrayList<ArrayList<String>>(population.subList(0, population.size()/2));

        // fill new arrayList of offsprings with 75% size of population
        ArrayList<ArrayList<String>> offspring = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < (population.size())/2; i++) {
            int parent1 = rd.nextInt(newPop.size());
            int parent2 = rd.nextInt(newPop.size());
            offspring.add(cross(newPop.get(parent1), newPop.get(parent2)));
        }

        //replace old population with new population
        population.clear();       
        population.addAll(newPop);
        population.addAll(offspring);
    }

    // crossover bewteen parent 1 and parent 2 at crossover point
    private static ArrayList<String> cross(ArrayList<String> parent1, ArrayList<String> parent2) {
        ArrayList<String> result = new ArrayList<String>();
        int crossoverPoint;
        if(nothingInCommon(parent1, parent2)){
            return parent1;
        }
        do{
            crossoverPoint = rd.nextInt(parent1.size()-1);
        }while(crossoverPoint == 0 || !parent2.contains(parent1.get(crossoverPoint)));
        result.addAll(parent1.subList(0, crossoverPoint)) ;
        result.addAll(parent2.subList(parent2.indexOf(parent1.get(crossoverPoint)), parent2.size()));
            
        //apply mutation to newborn offspring
        if(rd.nextDouble() < mutationProbability)
            result = mutationBiased(result);

        return result;
    }

    private static boolean nothingInCommon(ArrayList<String> parent1, ArrayList<String> parent2) {
        for (int i = 1; i < parent1.size()-1; i++) {
            if(parent2.contains(parent1.get(i)))
                return false;
        }
        return true;
    }


    // change node and construct path from that note to the end
    private static ArrayList<String> mutationBiased(ArrayList<String> ind) {    
        int index;
        do{
            index = rd.nextInt(ind.size());
        }while(index == 0);

        ArrayList<String> result = new ArrayList<String>();
        ArrayList<Node> permutation = new ArrayList<Node>();
        Node currentNode = null;
        for (int i = 0; i < index; i++) {
            result.add(String.valueOf(ind.get(i)));
            currentNode =  topology.get(Integer.valueOf(ind.get(i)));
            permutation.add(currentNode);
        }
        while(freeNodesInRange(currentNode.nodesInRange(topology), permutation) && !permutation.contains(goal)){
            ArrayList<Node> currentInRange = currentNode.nodesInRange(topology);
            int rnd = rd.nextInt(currentInRange.size());
            if(!permutation.contains(currentInRange.get(rnd))){
                currentNode = currentInRange.get(rnd);
                permutation.add(currentNode);
                result.add(String.valueOf(topology.indexOf(currentNode)));
            }
        }
        return result;
    }


    public static void generateFirstPopulation(){
        for (int i = 0; i < PopSize; i++) {
            //population.add(generateIndividual(start,goal, rd.nextInt(topology.size()-2))); 
            population.add(generateBiasedIndividual()); 
        } 
    }

    // generate single individuals based on a permutation of nodes 
    private static String generateIndividual(int indivSize) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for(int i = 0; i < topology.size(); i++){
            numbers.add(i);
        }
        numbers.remove(Integer.valueOf(topology.indexOf(start)));
        numbers.remove(Integer.valueOf(topology.indexOf(goal)));
        String result = String.valueOf(topology.indexOf(start));
        for (int i = 0; i < indivSize; i++) {
            int rnd = rd.nextInt(numbers.size());
            result += String.valueOf(numbers.get(rnd));
            numbers.remove(rnd);
        }
        result += String.valueOf(topology.indexOf(goal));
        return result;
    }

    // generate biased individuals, picking a random node from range and that hasn't been used before
    // returns and individual once it reaches the goal node o
    private static ArrayList<String> generateBiasedIndividual() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(String.valueOf(topology.indexOf(start)));
        Node currentNode = start;
        ArrayList<Node> permutation = new ArrayList<Node>();
        permutation.add(currentNode); 
        while(freeNodesInRange(currentNode.nodesInRange(topology), permutation) && !permutation.contains(goal)){
            ArrayList<Node> currentInRange = currentNode.nodesInRange(topology);
            int rnd = rd.nextInt(currentInRange.size());
            if(!permutation.contains(currentInRange.get(rnd))){
                currentNode = currentInRange.get(rnd);
                permutation.add(currentNode);
                result.add(String.valueOf(topology.indexOf(currentNode)));
            }
        }
        return result;
    }

    static public boolean freeNodesInRange(ArrayList<Node> inRange, ArrayList<Node> inPermutation){
        for(Node a : inRange ){
            if(!inPermutation.contains(a))
                return true;
        }
        return false;
    }

    // get nodes with most distance
    public static ArrayList<Node> furthestNodes(){
        ArrayList<Node> furtherNodes = new ArrayList<Node>();
        double distance = 0;
        int index1 = 0, index2 = 0;
        for (int i = 0; i < topology.size(); i++) {
            for (int j = 0; j < topology.size(); j++) {
                if(i != j && topology.get(i).getDistance(topology.get(j)) > distance){
                    distance = topology.get(i).getDistance(topology.get(j));
                    index1 = i;
                    index2 = j;
                }
            }
        }
        //add furtherst nodes
        furtherNodes.add(topology.get(index1));
        furtherNodes.add(topology.get(index2));
        return furtherNodes;
    }

    // return fitness of individual based on sum of values between nodes like: 1/SUM(cost of nodes) * 100 
    // the idea is to minimize the cost of the nodes, while the multiplication by 100 is to ease visualization
    public static double fitness(ArrayList<String> individual){
        double result = 0;
        if(!individual.contains(printAsIndex(goal)))
            return 0;
        for (int i = 0; i < individual.size()-1; i++){
            if(!topology.get(Integer.valueOf(individual.get(i))).isInRange(topology.get(Integer.valueOf(individual.get(i+1)))))
                return 0;
            result += topology.get(Integer.valueOf(individual.get(i+1))).getCost3(goal, packagesize, topology.get(Integer.valueOf(individual.get(i))));
        }
        return 10/Math.log(result);
    }

    //print population + fitness
    public static void viewPopulation(){
        System.out.println("Generation: " + String.valueOf(generation));
        for (ArrayList<String> n : population) {
            System.out.println(n +": " + String.valueOf(fitness(n)));
        }
    }



    //print population + fitness
    public static void viewPopulationStats(){
        System.out.println("Generation: " + String.valueOf(generation));
        System.out.println("Best Individual: " + population.get(0) + "\nBest fitness: " + String.valueOf(fitness(population.get(0))));
        System.out.println("Percetange of population with non zero fitness: " + String.valueOf((int)nonZeroFit()) + "%");
        System.out.println("Percetange of population equal to best indiviual: " + String.valueOf((int)sameAsBest()) +"%");
        System.out.println("Population size:" + String.valueOf(population.size()) + "\n");
    }

    // returns % of indivuals with non zero fit 
    public static double nonZeroFit(){
        int count = 0;
        for (ArrayList<String> n : population) {
            if(fitness(n) > 0 )
                count++;
        }
        return ((float)count/(float)PopSize)*100;
    }

    // returns % of indivuals with same as the best result 
    public static double sameAsBest(){
        int count = 1;
        for (int i = 1; i < population.size(); i++) {
           if(population.get(i).equals(population.get(0)) && fitness(population.get(i)) != 0)
                count++;
        }
        return ((float)count/(float)PopSize)*100;
    }

    // import topology from file
    public static void importTopology(){
        try {
            File myObj = new File("nodes.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              String[] data = myReader.nextLine().split(",");
              topology.add(new Node(Integer.valueOf(data[0]), Integer.valueOf(data[1]), Double.valueOf(data[2]) , Integer.valueOf(data[3]), Integer.valueOf(data[4]), Integer.valueOf(data[5]), Integer.valueOf(data[6])));
            }
            myReader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
    }

    public static void printAsIndex(ArrayList<Node> toPrint){
        for (Node element : toPrint) {
            System.out.println(String.valueOf(topology.indexOf(element)));

        }
    }

    public static String printAsIndex(Node toPrint){
        return String.valueOf(topology.indexOf(toPrint));
    }
}
