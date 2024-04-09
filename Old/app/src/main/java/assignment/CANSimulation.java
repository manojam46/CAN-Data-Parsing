package assignment;

import java.util.Scanner;

public class CANSimulation {
    @SuppressWarnings("resource")
    public static void main(String args[]) {
        // Checking if the .trc file location has been submitted
        if(args.length <= 0){
            throw new Error("File location cannot be empty");
        }

        String fileLoc = args[0];
        
        
        CANTraceParser trcFileParser = new CANTraceParser();
        CANTrace canTrace = trcFileParser.ParseCANTraceFile(fileLoc);
        Scanner userInput = new Scanner(System.in);

        while(true){
            System.out.println("*************************");
            System.out.println("(1) Print trace");
            System.out.println("(2) Get Next Message");
            System.out.println("(3) Reset");
            System.out.println("(4) Exit");
            System.out.println("*************************");

            String choice =  userInput.next();
            
            if(choice.equals("1")){
                canTrace.printTrace();
                continue;
            }
            
            if(choice.equals("2")){
                canTrace.getNextMessage();
                continue;
            }
            
            if(choice.equals("3")){
                canTrace.resetNextMessage();
                continue;
            }
            
            if(choice.equals("4")){
                System.exit(0);
            }

            System.out.println("Invalid input!");
        }
    }
}
