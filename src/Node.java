import java.util.ArrayList;

class Node{
    private int x, y, latency;
    private double range;
    private int capacity, movementX, movementY;

    public Node(int x, int y, double range, int latency, int capacity, int movementX, int movementY ){
        if (range <= 0 ) {
            throw new IllegalArgumentException("Range must be a positive value different from 0 but found "+ range);
        }
        setX(x);
        setY(y);
        setRange(range);
        setLatency(latency);
        setCapacity(capacity);
        setMovement(movementX,movementY);
    }


    // distance between node to goal node
    public double getDistance(Node other){
        return Math.sqrt(Math.pow(this.y - other.getY(), 2) + Math.pow(this.x - other.getX(), 2) );
    }

    // distance between node to goal node
    public double getDistanceAfterMove(Node other){
        return Math.sqrt(Math.pow(((this.y + this.movementY) - (other.getY() + other.getMovementY())), 2)
        + Math.pow(((this.x + this.movementX) - (other.getX() + other.getMovementX())), 2) );
        
    }

    // get nodes in range
    public ArrayList<Node> nodesInRange(ArrayList<Node> allNodes){
        ArrayList<Node> result = new ArrayList<Node>();
        for(Node n : allNodes){
            if (getDistance(n) <=  this.range && !n.equals(this))
                result.add(n);
        }
        return result;
    }

    public boolean isInRange(Node other){
        return getDistance(other) <= this.range;
    }

    public double movement(Node other){
        return getDistance(other) - getDistanceAfterMove(other);
    }

    // value function based on distance to goal node
    // cost = distanceToGoal
    public double getCost1(Node goal, int packagesize, Node sender){
        return getDistance(goal);
    }

    // value function based on distance to goal node and latency
    // cost = distanceToGoal*latency
    public double getCost2(Node goal, int packagesize, Node sender){
        return  getDistance(goal)*getLatency();
    }


    // value function based on distance to goal node, latency and capacity of node
    // if capacity > packageSize : cost = distanceToGoal*latency+(capacity-packageSize)
    // if capacity < packageSize : cost = distanceToGoal*latency*(Ceiling(packageSize/capacity))
    public double getCost3(Node goal, int packagesize, Node sender){
        return getCapacity() - packagesize < 0
                ? getDistance(goal)*getLatency()*Math.ceil(packagesize/getCapacity())   // if capcity is lower than package size
                : getDistance(goal)*getLatency()+(getCapacity() - packagesize) ;         // if capacity is greater than package size
    }


    // value function based on distance to goal node, latency, capacity of node and movement simulation
    // if capacity > packageSize : cost = distanceToGoal*latency+(capacity-packageSize)-(movement*10)
    // if capacity < packageSize : cost = distanceToGoal*latency*(Ceiling(packageSize/capacity))-(movement*10)
    public double getCost4(Node goal, int packagesize, Node sender){
        return getCapacity() - packagesize < 0
                ? getDistance(goal)*getLatency()*Math.ceil(packagesize/getCapacity()) - (movement(sender) * 10)   // if capcity is lower than package size
                : getDistance(goal)*getLatency()+(getCapacity() - packagesize) - (movement(sender) * 10);         // if capacity is greater than package size
    }

    //getters and setters
    public int getX(){
        return this.x;  
    }

    public int getY(){
        return this.y;
    }

    public double getRange(){
        return this.range;
    }

    public int getLatency(){
        return this.latency;
    }

    public int getMovementX(){
        return this.movementX;
    }

    public int getMovementY(){
        return this.movementY;
    }

    public int getCapacity(){
        return this.capacity;
    }

    private void setX(int x){
        this.x = x;
    }

    private void setY(int y){
        this.y = y;
    }

    public void setRange(double range){
        this.range = range;
    }

    private void setLatency(int latency){
        this.latency = latency;
    }

    private void setCapacity(int capacity){
        this.capacity = capacity;
    }

    private void setMovement(int movementX, int movementY){
        this.movementX = movementX;
        this.movementY = movementY;
    }

    //to string
    public String toString(){
        return String.valueOf(getX()) +"," + String.valueOf(getY()) + "," 
        + String.valueOf(getRange()) + "," + String.valueOf(getLatency()) + ","
        + String.valueOf(getCapacity()) + "," + String.valueOf(getMovementX()) + ","
        + String.valueOf(getMovementY()); 
    }
}