import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Topology {

    private static int MaxRange = 10;
    private static int MinLatency = 1;
    private static int MaxLatency = 5;
    private static int MaxPos = 80;
    private static int MinPos = -80;
    private static int MaxCapacity = 100;
    private static int MinCapacity = 10;
    private static int size = 200;
    private static int MinInRange = 5;
    private static int MaxMovement = 5;
    private static int MinMovement = -5;
    static ArrayList<Node> topology = new ArrayList<Node>();

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static double getRandomDoubleNumber(int min, int max) {
        return ((Math.random() * (max - min)) + min);
    }

    public static void main(String[] args) {
        generateTopology(size);
        //importTopology();
    }   

    

    // random generate nodes and save to external file
    public static void generateTopology(int size){
        for (int i = 0; i < size; i++) {
            topology.add(new Node(getRandomNumber(MinPos, MaxPos), getRandomNumber(MinPos, MaxPos),
                     getRandomDoubleNumber(1, MaxRange), getRandomNumber(MinLatency, MaxLatency),
                    getRandomNumber(MinCapacity, MaxCapacity), getRandomNumber(MinMovement, MaxMovement)
                    , getRandomNumber(MinMovement, MaxMovement)));
        }
        fixTopologyRange();
        File myObj = new File("nodes.txt");
        try {
            myObj.createNewFile();
            try {
                FileWriter myWriter = new FileWriter("nodes.txt");
                for(Node n : topology)
                    myWriter.write(n.toString() + "\n");
                myWriter.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fixTopologyRange(){
        for(Node n : topology){
            while(n.nodesInRange(topology).size() < MinInRange ){
                n.setRange(n.getRange()+1);
            }
        }
    }
}
