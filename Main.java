import enums.Direction; // Make sure this import is correct
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        startElevatorSimulation();
    }

    private static void startElevatorSimulation() {
        System.out.println("Elevator Simulation\n");
        Scanner input = new Scanner(System.in);

        int floorsCount = 5;
        int elevatorCount = 2;
        try {
            System.out.println("Enter the number of floors: ");
            floorsCount = input.nextInt();
            System.out.println("Enter the number of elevators: ");
            elevatorCount = input.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Using defaults (5 floors, 2 elevators).");
        }

        //creates instance of elevator command/dispatcher
        ElevatorCommand elevatorCommand = new ElevatorCommand(elevatorCount, floorsCount);


        while (true) {

            //display status once before the prompt in command mode
            clearScreen();
            System.out.println("--- ELEVATOR STATUS ---");
            displayAllElevators(elevatorCommand);
            System.out.println("-------------------------");
            System.out.println("Type 'watch' for continuous updates, 'help' for commands, or 'exit' to quit.");
            System.out.print("> ");

            String line = input.nextLine();
            if (line == null) {
                continue;
            }

            //commands arguments seperated by spaces eg hall 2
            String[] parts = line.trim().toLowerCase().split("\\s+");
            String command = parts[0];

            try {
                switch (command) {
                    case "hall":
                        //usage: hall <floor> <up|down>
                        int hallFloor = Integer.parseInt(parts[1]);
                        Direction direction;
                        if (parts[2].equals("up")) {
                            direction = Direction.UP;
                        } else if (parts[2].equals("down")) {
                            direction = Direction.DOWN;
                        } else {
                            System.out.println("Invalid direction. Use 'up' or 'down'.");
                            break;
                        }

                        elevatorCommand.hallCallElevator(hallFloor, direction);
                        break;

                    case "car":
                        //usage: car <elevatorId> <targetFloor>
                        int elevatorId = Integer.parseInt(parts[1]);
                        int targetFloor = Integer.parseInt(parts[2]);

                        elevatorCommand.carCallElevator(targetFloor, elevatorId);
                        break;

                    case "watch":
                        enterWatchMode(input, elevatorCommand);
                        break;

                    case "help":
                        pauseAndDisplayCommands(input);
                        break;

                    case "exit":
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Unknown command. Type 'help' for a list of commands.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number in command.");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Missing arguments for command.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            //pause to allow messages to show
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void pauseAndDisplayCommands(Scanner input) {
        clearScreen();

        displayCommands();

        System.out.println("\nPress ENTER to continue.");

        input.nextLine();//pauses main loop
    }

    private static void enterWatchMode(Scanner input, ElevatorCommand elevatorCommand) {

        while (true) {
            clearScreen();
            System.out.println("--- WATCH MODE ---");
            displayAllElevators(elevatorCommand);
            System.out.println("------------------------------------");
            System.out.println("Press 'q' followed by ENTER to exit...");

            try {
                Thread.sleep(500);

                //watches for q pressed
                if (System.in.available() > 0) {
                    String watchLine = input.nextLine().trim().toLowerCase();
                    if (watchLine.equals("q")) {
                        return;
                    }
                }
            } catch (java.io.IOException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearScreen() {
        //requires ANSI compatible console.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void displayAllElevators(ElevatorCommand elevatorCommand) {
        for (Elevator elevator : elevatorCommand.getElevators()) {
            System.out.print(elevator.toString() + " | ");
        }
        System.out.println();
    }

    private static void displayCommands() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("  watch                      - Enter continuous display mode (Exit with 'q' + ENTER).");
        System.out.println("  hall <floor> <up|down>   - Make a request from a floor.");
        System.out.println("                           (e.g., 'hall 3 up')");
        System.out.println("  car <elevatorId> <floor> - Make a request from inside an elevator.");
        System.out.println("                           (e.g., 'car 1 5')");
        System.out.println("  help                       - Show this help menu.");
        System.out.println("  exit                       - Quit the simulation.");
        System.out.println("----------------------------");
    }
}