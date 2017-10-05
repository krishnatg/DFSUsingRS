Readme for using the DFS implementation
----------------------------------------

Current limitation of the implementation is that it can be run only on a single server. Minor changes need to be made to the code for it to run over the network.

1. Start the six Clients with incrementing user IDs, i.e. 1-6.
2. Start the Server. Select the file to be uploaded/downloaded.
3. If file is uploaded, check that the clients received the blocks and that they are saved in "/tmp/<parent directory of file>" on each.
4. If file is downloaded, check that the decoded file is present as "<parent directory of file>/<original file>.decoded".

This implementation uses RMI. Client internally starts the registry.

This implementation uses the open-source Reed-Solomon code developed by Backblaze Inc. (https://github.com/Backblaze/JavaReedSolomon)
