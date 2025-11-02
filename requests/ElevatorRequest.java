package requests;

public class ElevatorRequest implements Comparable<ElevatorRequest> {
    private int requestFloor;

    //hall calls constructor
    public ElevatorRequest(int floor) {
        requestFloor = floor;
    }

    //getters and setters

    public int getRequestFloor() {
        return requestFloor;
    }

    public void setRequestFloor(int floor) {
        requestFloor = floor;
    }

    @Override
    public int compareTo(ElevatorRequest other){
        return Integer.compare(this.requestFloor, other.getRequestFloor());
    }
}
