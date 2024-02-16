package assignment;

import java.util.Scanner;

public class CANSimulation {
    public static void main(String[] args) {
        // if(args.length <= 0){
        //     throw new Error("File location cannot be empty");
        // }

        // String trcFileLocToParse = "C:/Users/Manoj A M/Desktop/Assignment/SEM 1/Internet Embeded System/18 CANmessages.trc";
        String trcFileLocToParse = "/Users/manoj/Desktop/Assignment/Sem 1/Internet Embeded System/18 CANmessages.trc";
        
        CANTraceParser parser = new CANTraceParser();
        CANTrace canTrace = parser.ParseCANTraceFile(trcFileLocToParse);
        Scanner userChoice = new Scanner(System.in);

        for(;;){
            System.out.println("*************************");
            System.out.println("(1) Viewing All Data");
            System.out.println("(2) Get Next Message");
            System.out.println("(3) Reset Next Message");
            System.out.println("(4) To Exit");
            System.out.println("*************************");

            
            switch (userChoice.next()) {
                case "1":
                    canTrace.printTrace();
                break;

                case "2":
                    canTrace.getNextMessage();
                break;

                case "3":
                    canTrace.resetNextMessage();
                break;

                case "4":
                    System.exit(0);
                break;

                default:
                    System.out.println("Wrong choice entered");
                break;
            }
        }
    }
}
