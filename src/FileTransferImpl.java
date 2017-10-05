import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Krishna Tippur Gururaj on 5/3/17.
 */
public class FileTransferImpl extends UnicastRemoteObject implements FileTransferInt {


    public FileTransferImpl() throws RemoteException {
    }

    @Override
    public void saveFile(String filename, byte[] data, int len) throws RemoteException {
        String temp = "/tmp" + new File(filename).getParent() ;
        if (!(new File(temp).exists())) {
            new File(temp).mkdirs() ;
        }
        File file = new File("/tmp" + filename) ;
        try {
            FileOutputStream out = new FileOutputStream(file, true) ;
            out.write(data, 0, len);
            out.flush();
            out.close();
            System.out.println("done writing data") ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean doesFileExist(String filename) throws RemoteException {
        File file = new File(filename) ;
        if (file.exists()) {
            return true ;
        }
        System.out.println("missing: " + file.getAbsolutePath()) ;
        return false;
    }

    @Override
    public int getFileDataLength(String filename) throws RemoteException {
        File file = new File("/tmp" + filename) ;
        if (file.exists()) {
            return (int)file.length() ;
        }
        return 0;
    }

    @Override
    public byte[] getFileDataContent(String filename) throws RemoteException {
        File file = new File("/tmp" + filename) ;
        if (file.exists()) {
            try {
                return Files.readAllBytes(Paths.get("/tmp" + filename)) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

}
