import enums.Status;
import enums.Direction;
import requests.ElevatorRequest;

import java.util.Comparator;
import java.util.PriorityQueue;

//elevator entity
public class Elevator {

    private int elevatorId;
    private int currentFloor = 1;
    private Status elevatorStatus = Status.IDLE;
    private boolean isOperational = true;

    private PriorityQueue<ElevatorRequest> upwardElevatorRequests = new PriorityQueue<>();
    private PriorityQueue<ElevatorRequest> downwardElevatorRequests = new PriorityQueue<>(Comparator.reverseOrder());


    public Elevator(int elevatorId) {
        this.elevatorId = elevatorId;
        new Thread(this::simulateElevatorMovement).start(); //run elevators in their own threads to be concurrent
    }

    //goes up and down floors incrementally. until it reaches the floor.
    private synchronized void moveElevator(ElevatorRequest elevatorRequest) {
        int requestedFloor = elevatorRequest.getRequestFloor();

        goUpOrDownToRequestFloor(requestedFloor);

        if (currentFloor == requestedFloor) {
            //elevator has reached the requested floor remove it from the event queue
            if(elevatorStatus == Status.MOVING_UP){
                upwardElevatorRequests.poll();

                //if no more upward request but there are still down requests change direction so it can be acted on. prevents the elevator from changing directions mid route.
                if(upwardElevatorRequests.isEmpty() && !downwardElevatorRequests.isEmpty()){
                    elevatorStatus = Status.MOVING_DOWN;
                }
            }else if(elevatorStatus == Status.MOVING_DOWN){
                downwardElevatorRequests.poll();


                if(downwardElevatorRequests.isEmpty() && !upwardElevatorRequests.isEmpty()){
                    elevatorStatus = Status.MOVING_UP;
                }
            }

            System.out.println("Elevator " + elevatorId + " has arrived at floor " + requestedFloor);
            openElevatorDoors();
        }
    }

    private void goUpOrDownToRequestFloor(int requestedFloor) {
        if(currentFloor < requestedFloor){
            currentFloor++;
            elevatorStatus =  Status.MOVING_UP;
        }
        else if(currentFloor > requestedFloor){
            currentFloor--;
            elevatorStatus =  Status.MOVING_DOWN;
        }
    }

    //this simulates the time it would take for the elevator to act on request moving up and down.
    private void simulateElevatorMovement() {
        while (true) {

            //polling simulates time passing/floors passing
            if(isOperational) {
                elevatorPriorityAlgorithm();
            }

            try {
                Thread.sleep(1000); //wait 1 second, simulates the time passing between the elevator moving
            } catch(InterruptedException e) {
                System.out.println("Elevator sleep interrupted error: " + e.getMessage());
            }
        }
    }

    private synchronized void elevatorPriorityAlgorithm(){
        switch (elevatorStatus) {
            case IDLE:
                //if idle, check if any requests are waiting.
                if (!upwardElevatorRequests.isEmpty()) {
                    elevatorStatus = Status.MOVING_UP;
                } else if (!downwardElevatorRequests.isEmpty()) {
                    elevatorStatus = Status.MOVING_DOWN;
                }
                break;

            case MOVING_UP:
                //if moving up only look at up queue
                if (!upwardElevatorRequests.isEmpty()) {
                    //service the next upward request
                    moveElevator(upwardElevatorRequests.peek());
                } else {
                    //upward queue is empty. switch to down or idle.
                    if (!downwardElevatorRequests.isEmpty()) {
                        elevatorStatus = Status.MOVING_DOWN;
                    } else {
                        elevatorStatus = Status.IDLE;
                    }
                }
                break;

            case MOVING_DOWN:
                //if moving down only look at down queue
                if (!downwardElevatorRequests.isEmpty()) {
                    //service the next downward request
                    moveElevator(downwardElevatorRequests.peek());
                } else {
                    //downward queue is empty. switch to up or idle.
                    if (!upwardElevatorRequests.isEmpty()) {
                        elevatorStatus = Status.MOVING_UP;
                    } else {
                        elevatorStatus = Status.IDLE;
                    }
                }
                break;

            //do nothing while doors are operating
            case OPENING_DOORS:
            case CLOSING_DOORS:
                break;
        }
    }

    public synchronized void addElevatorRequest(ElevatorRequest elevatorRequest) {
        if(currentFloor < elevatorRequest.getRequestFloor()){
            upwardElevatorRequests.offer(elevatorRequest);
        }else if(currentFloor > elevatorRequest.getRequestFloor()){
            downwardElevatorRequests.offer(elevatorRequest);
        }else { //just opens the door if currentFloor == requestFloor
            openElevatorDoors();
        }
    }

    private void openElevatorDoors(){
        //simulate opening of elevator doors and allowing users in.
        //assumes the users press the internal car button whilst in this phase.
        //else it will just start going back down to ground floor to idle. could make it park too.
        try {
            Status priorStatus = elevatorStatus;

            Thread.sleep(1000);
            System.out.println("Elevator " + elevatorId + " opening doors.");
            elevatorStatus = Status.OPENING_DOORS;

            Thread.sleep(4000);

            System.out.println("Elevator " + elevatorId + " closing doors.");
            elevatorStatus = Status.CLOSING_DOORS;
            Thread.sleep(1000);

            elevatorStatus = priorStatus;
        } catch (InterruptedException e) {
            System.out.println("Elevator sleep interrupted error: " + e.getMessage());
        }
    }

    //getters and setters...

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public Status getElevatorStatus() {
        return elevatorStatus;
    }

    public void setElevatorStatus(Status elevatorStatus) {
        this.elevatorStatus = elevatorStatus;
    }

    public boolean isOperational() {
        return isOperational;
    }

    public void setOperational(boolean operational) {
        isOperational = operational;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public synchronized int getSizeOfElevatorRequests() {
        return upwardElevatorRequests.size() + downwardElevatorRequests.size();
    }

    public synchronized Direction getDirection() {
        switch (elevatorStatus) {
            case IDLE:
                return Direction.STATIONARY;
            case MOVING_UP:
                return Direction.UP;
            case MOVING_DOWN:
                return Direction.DOWN;
            default:
                return Direction.STATIONARY;
        }
    }

    //for display in console interface.
    @Override
    public String toString() {
        return ("E" + elevatorId + ": floor " + currentFloor + " " + elevatorStatus);
    }
}