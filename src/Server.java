import java.io.*;
import java.rmi.Naming;
import java.util.Scanner;

/**
 * Created by Krishna Tippur Gururaj on 5/3/17.
 */
public class Server {

    static int numberOfBlocks = 4 ;
    static int numberOfRedundantBlocks = 2 ;
    static int port = 4000 ;

    /**
     * This method retrieves all available block files from the clients
     * @param fileBaseName absolute file path requested by user
     * @return true, if the Reed-Solomon decoding can be done, else, false
     */
    public static boolean retrieveAllFiles(String fileBaseName) {
        int count = 0 ;
        for (int i = 0 ; i < (numberOfBlocks + numberOfRedundantBlocks) ; i++) {
            String name = "//localhost:" + (port + i + 1) + "/DFSObj" + (i + 1) ;
            System.out.println("attempting: " + name) ;
            try {
                FileTransferInt obj = (FileTransferInt) Naming.lookup(name) ;
                if (obj.doesFileExist("/tmp/" + fileBaseName + "." + i)) {
                    /*
                    File file = obj.getFile(fileBaseName + "." + i) ;
                    OutputStream out = new FileOutputStream(file) ;
                    */
                    int len = obj.getFileDataLength(fileBaseName + "." + i) ;
                    byte[] data = obj.getFileDataContent(fileBaseName + "." + i) ;

                    File temp = new File(fileBaseName) ;
                    String fullPath = "/tmp/retrieved" + temp.getParent() ;
                    if (!(new File(fullPath)).exists()) {
                        new File(fullPath).mkdirs() ;
                    }

                    File file = new File("/tmp/retrieved" + temp.getAbsolutePath() + "." + i) ;
                    file.createNewFile() ;
                    FileOutputStream fos = new FileOutputStream(file) ;
                    fos.write(data, 0, len);
                    fos.flush();
                    fos.close();
                    System.out.println("retrieved file: " + fileBaseName + (i + 1)) ;
                    count++ ;
                }
                else {
                    System.err.println("file: " + fileBaseName + (i + 1) + " does not exist") ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (count < numberOfBlocks) {
            return false ;
        }
        else {
            return true ;
        }
    }

    /**
     * This method sends all the blocks of file to the clients
     * @param fileBaseName file being sent
     */
    public static void sendAllFiles(String fileBaseName) {
        for (int i = 0 ; i < (numberOfRedundantBlocks + numberOfBlocks) ; i++) {
            File tempFile = new File(fileBaseName + "." + i) ;
            String name = "//localhost:" + (port + i + 1) + "/DFSObj" + (i + 1) ;
            try {
                FileTransferInt obj = (FileTransferInt) Naming.lookup(name) ;
                FileInputStream fis = new FileInputStream(tempFile) ;
                byte[] data = new byte[(int)tempFile.length()] ;
                int len = fis.read(data) ;
                obj.saveFile(tempFile.getAbsolutePath(), data, len);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in) ;
        System.out.println("Enter file path: ") ;
        String fileBaseName = sc.nextLine() ;
        File inputFile = new File(fileBaseName) ;

        System.out.println("Enter 1 for uploading file, 2 for downloading file: ") ;
        int choice = sc.nextInt() ;
        switch (choice) {
            case 1:
                if (!inputFile.exists()) {
                    System.err.println("File does not exist!") ;
                    sc.close();
                    System.exit(1);
                }
                DFSLogic.putFile(inputFile);
                sendAllFiles(inputFile.getAbsolutePath());
                break ;
            case 2:
                //TODO: obtain all chunks of file from the clients before calling getFile()
                if (!retrieveAllFiles(inputFile.getAbsolutePath())) {
                    System.err.println("not enough blocks available to decode") ;
                }
                else {
                    DFSLogic.getFile(inputFile.getAbsoluteFile());
                }
                break ;
            default:
                System.err.println("invalid input") ;
                System.exit(1);
        }
        sc.close();
        System.exit(0);
    }
}
