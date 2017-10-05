import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Krishna Tippur Gururaj on 5/3/17.
 * Used Reed-Solomon implementation by Backblaze released under the MIT license
   https://github.com/Backblaze/JavaReedSolomon
 */
public class DFSLogic {

    static int numberOfBlocks = 4 ;
    static int numberOfRedundantBlocks = 2 ;

    /**
     * This method breaks up the input file and stores the individual block files for subsequent upload to clients
     * @param inputFile this is the file that is to be encoded using the Reed-Solomon code
     */
    public static void putFile(File inputFile) {
        int fileSize = (int)inputFile.length() ;
        int blockSize = (fileSize + (Integer.SIZE / Byte.SIZE) + numberOfBlocks - 1) / numberOfBlocks ;
        int bufferSize = blockSize * numberOfBlocks ;
        byte allBytes[] = new byte[bufferSize] ;
        ByteBuffer.wrap(allBytes).putInt(fileSize) ;
        try {
            InputStream in = new FileInputStream(inputFile) ;
            int bytesRead = in.read(allBytes, (Integer.SIZE/Byte.SIZE), fileSize) ;
            if (bytesRead != fileSize) {
                System.err.println("not enough bytes read") ;
            }
            in.close() ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[][] blocks = new byte[numberOfBlocks + numberOfRedundantBlocks][blockSize] ;
        for (int i = 0 ; i < numberOfBlocks ; i++) {
            System.arraycopy(allBytes, i * blockSize, blocks[i], 0, blockSize);
        }

        ReedSolomon rs = ReedSolomon.create(numberOfBlocks, numberOfRedundantBlocks) ;
        rs.encodeParity(blocks, 0, blockSize);
        for (int i = 0 ; i < (numberOfBlocks + numberOfRedundantBlocks); i++) {
            File outputFile = new File(inputFile.getParentFile(), inputFile.getName() + "." + i) ;
            try {
                OutputStream out = new FileOutputStream(outputFile) ;
                out.write(blocks[i]);
                out.close();
                System.out.println("wrote: " + outputFile) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("successful putFile()") ;
    }

    /**
     * This method combines the blocks of data obtained from the clients so that the Reed-Solomon decoding can be applied
     * @param inputFile this is the file that is requested by the user
     */
    public static void getFile(File inputFile) {
        byte[][] blocks = new byte[numberOfRedundantBlocks + numberOfBlocks][] ;
        boolean[] blockPresent = new boolean[numberOfBlocks + numberOfRedundantBlocks] ;
        int blockSize = 0 ;
        int blockCount = 0 ;
        for (int i = 0 ; i < (numberOfRedundantBlocks + numberOfBlocks) ; i++) {
            File blockFile = new File("/tmp/retrieved/", inputFile.getAbsolutePath() + "." + i) ;
            if (blockFile.exists()) {
                blockSize = (int)blockFile.length() ;
                blocks[i] = new byte[blockSize] ;
                blockPresent[i] = true ;
                blockCount += 1 ;
                try {
                    InputStream in = new FileInputStream(blockFile) ;
                    in.read(blocks[i], 0, blockSize) ;
                    in.close();
                    System.out.println("read: " + blockFile) ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (blockCount < numberOfBlocks) {
            System.err.println("not enough blocks available") ;
            return;
        }

        for (int i = 0 ; i < (numberOfBlocks + numberOfRedundantBlocks) ; i++) {
            if (!blockPresent[i]) {
                blocks[i] = new byte[blockSize] ;
            }
        }

        ReedSolomon rs = ReedSolomon.create(numberOfBlocks, numberOfRedundantBlocks) ;
        rs.decodeMissing(blocks, blockPresent, 0, blockSize);

        byte[] allBytes = new byte[blockSize * (numberOfBlocks + numberOfRedundantBlocks)] ;
        for (int i = 0 ; i < (numberOfBlocks + numberOfRedundantBlocks) ; i++) {
            System.arraycopy(blocks[i], 0, allBytes, blockSize * i, blockSize);
        }

        int fileSize = ByteBuffer.wrap(allBytes).getInt() ;

        File decodedFile = new File(inputFile.getParentFile(), inputFile.getName() + ".decoded") ;
        try {
            OutputStream out = new FileOutputStream(decodedFile) ;
            out.write(allBytes, (Integer.SIZE / Byte.SIZE), fileSize);
            System.out.println("decoded: " + decodedFile) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("successful getFile()") ;
    }
}
