# Elevator Simulation System

## Overview
This project simulates a collection of elevators that operate asynchronously and independently. Each elevator runs on its own thread and simulates timing and movement between floors.

## Technologies Used
- **Language:** Java 25
- **Libraries:** Only built-in Java libraries

## Design Goals
The main design goals were to:
- Achieve scalability by supporting any number of elevators
- Maintain realistic behavior through time-based simulation
- Apply object-oriented principles for clean and modular structure

## System Behavior

### Floors and Limits
When creating an instance of `ElevatorCommand`, you define the top floor. Any requests above that ceiling are automatically blocked.  
The first floor is always assumed to be 1.

### Elevator Assignment
Requests are assigned using a point-based cost system. The system considers:
- How many requests an elevator is currently handling
- The distance from the requested floor
- The elevator’s status (idle or moving)

The elevator with the lowest total cost handles the request.

### Request Management
- **Hall calls** (external requests) are delegated to the most suitable elevator using the point system.
- **Car calls** (internal elevator requests) are sent directly to the specified elevator by ID, this assumes a real elevator would have access to its own ID when an internal button is pressed.
- Requests are stored in a min-heap for upward requests or max-heap for downward requests, allowing elevators to efficiently prioritize stops.

### Elevator State Tracking
Each elevator maintains its state using an enum field that tracks:
- Current floor
- Status (Idle, Moving Up, Moving Down, Opening/Closing Doors)

### Time Simulation
Elevators update their positions and handle timing using polling every one second to simulate movement between floors.

## Architecture

### Main Components
| Class              | Responsibility                                                      |
|--------------------|---------------------------------------------------------------------|
| `Elevator`         | Represents an individual elevator and handles movement and requests |
| `ElevatorCommand`  | Acts as the central dispatch for all elevators                      |
| `ElevatorRequest`  | Stores the request data sent to the elevator to be processed        |
| `Status` (enum)    | Defines the state of an elevator (Idle, Moving, etc.)               |
| `Direction` (enum) | Tracks movement direction (up, down, stationary)                    |

### ElevatorCommand Class Overview
The ElevatorCommand class is responsible for dispatching and managing all elevators within the system.

| Constructor / Method                                               | Description                                                                                                                                |
|--------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| ElevatorCommand(int elevatorCount, int topFloor)                   | Initializes the elevator system with a user defined number of elevators and a maximum floor limit.                                         |
| void carCallElevator(int floor, int elevatorId)                    | Sends a car call (internal request) to a specific elevator by ID.                                                                          |
| void hallCallElevator(int floor, Direction direction)              | Handles a hall call (external request) made from a given floor and direction, delegating it to the best elevator using the cost algorithm. |
| void addElevator()                                                 | Dynamically adds a new elevator to the system, automatically assigning it the next ID.                                                     |
| void setElevatorOperational(int elevatorId, boolean isOperational) | Toggles an elevator’s operational state, preventing broken elevators from being assigned new requests.                                     |

Internally, the ElevatorCommand class uses a cost-based algorithm (calculateCostOfElevator) to determine which elevator should handle a request. This algorithm factors in:
- Distance to the requested floor
- Elevator direction and request direction
- Current queue size
- Elevator operational status

## User Interaction
The system uses a console interface that:
- Displays the current status and floor of all elevators
- Accepts user commands to make requests or control elevators

*Assumed one was needed for testing the operability of the elevators.*

## Limitations
- Autonomous error detection is not fully implemented, though there is a method to toggle elevator operability.

## Running the Program
1. Ensure Java 25 (or compatible version) is installed.
2. Compile and run the program as usual:
   ```bash
   #within ./elevatorSystem_Hudson_Maiorana
   
   javac ./requests/*.java
   javac ./enums/*.java
   javac ./*.java
   java Main
