import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Krishna Tippur Gururaj on 5/4/17.
   Used the Backblaze implementation of Reed-Solomon algorithm: https://github.com/Backblaze/JavaReedSolomon
   That code is released under the MIT license
 */
public class Client {
    public static void main(String args[]) {
        if (args.length == 0 || Integer.parseInt(args[0]) <= 0) {
            System.err.println("Usage: java Client <clientID>") ;
            System.exit(1);
        }

        int port = 4000 + Integer.parseInt(args[0]) ;
        try {
            LocateRegistry.createRegistry(port) ;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String name = "//localhost:" + port + "/DFSObj" + Integer.parseInt(args[0]) ;
        try {
            FileTransferImpl obj = new FileTransferImpl() ;
            Naming.rebind(name, obj);
            System.out.println("Bound in registry: " + name) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
