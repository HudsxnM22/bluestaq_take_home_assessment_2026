import enums.Direction;
import requests.ElevatorRequest;
import java.util.ArrayList;
import java.util.List;

//responsible for sending out requests to elevators
public class ElevatorCommand {
    private int topFloor;
    private final List<Elevator> elevators = new ArrayList<>();

    public ElevatorCommand(int elevatorCount, int topFloor) {
        this.topFloor = topFloor;

        for (int i = 0; i < elevatorCount; i++) {
            elevators.add(new Elevator(i + 1)); //that elevators id will == 1 indexed value for readability.
        }
    }

    public void carCallElevator(int floor, int elevatorId) {
        if (floor > topFloor) {
            System.out.println("Floor requested exceeds top floor");
            return;
        }
        if (floor < 1){// 1 is the bottom most floor.
            System.out.println("Floor cannot be less than 1");
            return;
        }

        for (Elevator elevator : elevators) {
            if (elevator.getElevatorId() == elevatorId) {
                elevator.addElevatorRequest(new ElevatorRequest(floor));
                return;
            }
        }

        System.out.println("Elevator id " + elevatorId + " not found");
    }

    public void hallCallElevator(int floor, Direction direction) {
        if (floor > topFloor) {
            System.out.println("Floor requested exceeds top floor");
            return;
        }
        if (floor < 1){ //1 is the bottom most floor.
            System.out.println("Floor cannot be less than 1");
            return;
        }

        findBestElevatorForCall(floor, direction);
    }

    //returns index of the best possible elevator for that call.
    private void findBestElevatorForCall(int floor, Direction direction) {
        //calculate best elevator for the request and stores its id
        int lowestCostElevatorId = 0;
        int lowestCost = Integer.MAX_VALUE;
        for (Elevator elevator : elevators) {
            int cost = calculateCostOfElevator(elevator, floor, direction);
            if(lowestCost > cost) {
                lowestCost = cost;
                lowestCostElevatorId = elevator.getElevatorId();
            }
        }

        //add the hall call request to that elevator
        for(Elevator elevator : elevators) {
            if(elevator.getElevatorId() == lowestCostElevatorId) {
                elevator.addElevatorRequest(new ElevatorRequest(floor));
            }
        }
    }

    //uses a point system that could be adjusted to load balance requests
    private int calculateCostOfElevator(Elevator elevator, int requestedFloor, Direction requestedDirection) {
        int sizeOfElevatorRequests = elevator.getSizeOfElevatorRequests(); //the events already queued
        int floorDifference = Math.abs(elevator.getCurrentFloor() - requestedFloor);
        Direction elevatorDirection = elevator.getDirection();

        //prevents requests from being delegated to broken elevators.
        if (!elevator.isOperational()) {
            return Integer.MAX_VALUE;
        }

        int totalCost = floorDifference; //base cost

        //idle elevators are cheaper
        if (elevatorDirection == Direction.STATIONARY) {
            totalCost -= 2;
        }

        //elevator already moving toward the request = good fit
        else if ((elevatorDirection == Direction.UP && requestedFloor >= elevator.getCurrentFloor() && requestedDirection == Direction.UP)
                || (elevatorDirection == Direction.DOWN && requestedFloor <= elevator.getCurrentFloor() && requestedDirection == Direction.DOWN)) {
            totalCost -= 1; // slightly cheaper if it’s along the way
        }

        //elevator moving away = bad fit
        else {
            totalCost += 3;
        }

        //penalize elevators with longer queues
        totalCost += sizeOfElevatorRequests;

        //can’t go below zero
        return Math.max(totalCost, 0);
    }

    // getters and setters

    public int getTopFloor() {
        return topFloor;
    }

    public void setTopFloor(int topFloor) {
        this.topFloor = topFloor;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void addElevator(){
        elevators.add(new Elevator(elevators.size() + 1)); //1 indexed elevator array
    }

    public void setElevatorOperational(int elevatorId, boolean isOperational) {
        for (Elevator elevator : elevators) {
            if (elevator.getElevatorId() == elevatorId) {
                elevator.setOperational(isOperational);
            }
        }
    }


}
