/**
 * Created by Krishna Tippur Gururaj on 5/3/17.
 */
public interface FileTransferInt extends java.rmi.Remote {
    void saveFile(String filename, byte[] data, int len) throws java.rmi.RemoteException ;
    boolean doesFileExist(String filename) throws java.rmi.RemoteException ;
    int getFileDataLength(String filename) throws java.rmi.RemoteException ;
    byte[] getFileDataContent(String filename) throws java.rmi.RemoteException ;
}
