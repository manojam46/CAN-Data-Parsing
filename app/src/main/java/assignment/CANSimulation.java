package assignment;

public class CANSimulation {
    public static void main(String[] args) {
        // if(args.length <= 0){
        //     throw new Error("File location cannot be empty");
        // }

        String trcFileLocToParse = "C:/Users/Manoj A M/Desktop/Assignment/SEM 1/Internet Embeded System/18 CANmessages.trc";//args[0];

        CANTraceParser.CANTraceFile(trcFileLocToParse);
    }
}
