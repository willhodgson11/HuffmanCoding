import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;


/** Lossless Huffman compression using variable-length encoding
 *
 * @author willhodgson, Dartmouth CS 10, Winter 2023
 * @author cullumtwiss, Dartmouth CS 10, Winter 2023

 */

public class HuffmanCompressor implements Huffman {

    /**
     * Generate a Frequency Table of characters in file
     *
     * @param pathName - path to a file to read
     * @return map of characters and their respective frequencies
     * @throws IOException if issues arise opening, reading, or closing file
     */
    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map<Character, Long> frequencyMap = new TreeMap<Character, Long>();
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(pathName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return frequencyMap;
        }

        // Read file, if possible
        try {
            // loop through each character and add to Map
            int charInt = 0;
            // if the end of the file has not been reached, read each character
            while (charInt != -1) {
                charInt = input.read();
                Character character = (char) charInt;
                // if character has already been identified, increment frequency by 1
                if (frequencyMap.containsKey(character)) {
                    Long currCount = frequencyMap.get(character);
                    frequencyMap.put(character, 1 + currCount);
                }
                // if character has not yet been identified in file, add to map
                else {
                    if (charInt != -1) {
                        frequencyMap.put(character, 1L);
                    }
                }
            }

            // Edge case: if file is empty, throw exception
            if(frequencyMap.isEmpty()){
                throw new IOException("File " + pathName + " is empty!");
            }

        } catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
        return frequencyMap;

    }

    /**
     * Generate a Huffman codeTree using a priorityQueue to sort by frequency (highest at root)
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return Binary tree organized by frequency containing references to codeTreeElement objects
     */
    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies){
        Comparator TreeComparator = new TreeComparator();
        PriorityQueue<BinaryTree<CodeTreeElement>> priority = new PriorityQueue<BinaryTree<CodeTreeElement>>(TreeComparator);

        // add each key/value pair to the priorityQueue
        for (Character key : frequencies.keySet()) {
            BinaryTree<CodeTreeElement> ele = new BinaryTree<>(new CodeTreeElement(frequencies.get(key), key));
            priority.add(ele);
        }

        // Remove the lowest frequency trees, combining until one remains
        while (priority.size() != 1) {
            //Extract the two lowest-frequency trees T1 and T2 from the priority queue.
            BinaryTree<CodeTreeElement> leftData = priority.poll();
            BinaryTree<CodeTreeElement> left = leftData;
            BinaryTree<CodeTreeElement> rightData = priority.poll();
            BinaryTree<CodeTreeElement> right = rightData;
            //Create a new tree T by creating a new root node r, attaching T1 as r's left subtree, and attaching T2 as r's right subtree.
            //Assign to the new tree T a frequency that equals the sum of the frequencies of T1 and T2.
            BinaryTree<CodeTreeElement> root = new BinaryTree<CodeTreeElement>(new CodeTreeElement(leftData.data.myFrequency + rightData.data.myFrequency, null), left, right);
            //Insert the new tree T into the priority queue (which will base its priority on the frequency value it holds).
            priority.add(root);
        }
        // edge case: if only one letter, insert a parent with null value
        if(priority.size() == 1) {
            BinaryTree<CodeTreeElement> root = new BinaryTree<>(new CodeTreeElement(0L, null),  priority.poll(), null);
            priority.add(root);
        }
        return priority.poll();
    }

    /**
     * Creates a Map that pairs characters with their code words; i.e. pairs each
     * character with a string of '0's and '1's that describes the path from the root to that character.
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return Map of characters and their associated codes
     */
    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> codes = new TreeMap<Character, String>();
        String pathSoFar = "";
        // call helper function to obtain map of all character-code pairs
        codes = computeCodesHelper(codeTree, codes, pathSoFar);
        return codes;
    }

    /**
     * Helper function for computeCodes. Traverses tree and obtains a code for each
     * character by reaching downwards until a node is found, each time tacking on either
     * a '0' or '1' dependent on which node is retrieved.
     *
     * @param codeTree
     * @param codes
     * @param pathSoFar
     * @return
     */
    public Map<Character, String> computeCodesHelper(BinaryTree<CodeTreeElement> codeTree, Map<Character, String> codes, String pathSoFar) {

        // adds a character-string pair to the map
        if (codeTree.isLeaf()) {
            Character peep = codeTree.getData().getChar();
            codes.put(peep, pathSoFar);
        }

        // recurse with left node, attaching a '0' to the code
        if (codeTree.hasLeft()) {
            computeCodesHelper(codeTree.getLeft(), codes, pathSoFar + 0);
        }

        // recurse with right node, attaching a '1' to the code
        if (codeTree.hasRight()) {
            computeCodesHelper(codeTree.getRight(), codes, pathSoFar + 1);
        }

        return codes;

    }

    /**
     * repeatedly reads the next character in your text file, looks up its code
     * word in the code map, and then writes the sequence of 0's and 1's in that
     * code word as bits to an output file
     *
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException  if issues arise opening, reading, or closing input and output files
     */
    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
        BufferedBitWriter bitOutput;
        BufferedReader input;

        //Opens input file, if possible
        try {
            input = new BufferedReader(new FileReader(pathName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
            return;
        }

        //Opens compressed file, if possible
        try {
            bitOutput = new BufferedBitWriter(compressedPathName);
        } catch (IOException e) {
            System.err.println("Cannot open compressed file.\n" + e.getMessage());
            return;
        }


        int charInt = 0;

        // read each character in file and find associated code, writing bits in output file (if possible)
        try {
            // if the end of the file has not been reached, read each character and get its associated string
            while ((charInt = input.read()) != -1 && codeMap != null) {
                Character c = (char) charInt;
                String string = codeMap.get(c);
                // iterate through string, writing appropriate bit
                if(string != null){
                for (int i = 0; i < string.length(); i++) {
                    bitOutput.writeBit(string.charAt(i) == '0');
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Cannot write compressed file.\n" + e.getMessage());
        }

        // close input file, if possible
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }
        // close output file, if possible
        try {
            bitOutput.close();
        } catch (IOException e) {
            System.err.println("Cannot close compressed file.\n" + e.getMessage());
        }
    }

    /**
     * decodes compressed file by searching down tree for each bit until a leaf is reached
     *
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException if issues arise opening, reading, or closing output and compressed files
     */
    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        BufferedBitReader bitInput;
        BufferedWriter output;
        BinaryTree<CodeTreeElement> originalTree = codeTree;

        // Open compressed file, if possible
        try {
            bitInput = new BufferedBitReader(compressedPathName);
        } catch (IOException e) {
            System.err.println("Error opening compressed file");
            return;
        }

        // Open output file, if possible
        try {
            output = new BufferedWriter(new FileWriter(decompressedPathName));
        } catch (IOException e) {
            System.err.println("Error opening output file");
            return;
        }

        // Decode each character
        while (bitInput.hasNext()) {
            //Start at the root and read the first bit from the compressed file.
            while (codeTree.hasLeft()) {
                //If it is a '0' go left and if it is a '1' go right.
                boolean bit = bitInput.readBit();
                if (bit) {
                    codeTree = codeTree.getLeft();
                } else {
                    codeTree = codeTree.getRight();
                }
            }

            //If no left child, we have reached a leaf. Get the value of the node and write it to the file
            output.write(codeTree.getData().getChar());
            //Reset to root, rinse and repeat
            codeTree = originalTree;
        }
        // close output file, if possible
        try {
            output.close();
        } catch (IOException e) {
            System.err.println("Cannot close output file.\n" + e.getMessage());
        }
        //close input file, if possible
        try {
            bitInput.close();
        } catch (IOException e) {
            System.err.println("Cannot close compressed file.\n" + e.getMessage());
        }
    }

    /**
     * Main method. includes test cases for single letters, repeating letters, empty files, and two big ol files
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        HuffmanCompressor Compressor = new HuffmanCompressor();

        // Testing US Constitution
        BinaryTree<CodeTreeElement> USConstitutionTree = Compressor.makeCodeTree(Compressor.countFrequencies("USConstitution.txt"));
        Map USConstitutionMap = Compressor.computeCodes(USConstitutionTree);
        Compressor.compressFile(USConstitutionMap, "USConstitution.txt", "USConstitutionCompress.txt");
        Compressor.decompressFile("USConstitutionCompress.txt", "USConstitutionDecompress.txt", USConstitutionTree);

        // Testing War and Peace
        BinaryTree<CodeTreeElement> warAndPeaceTree = Compressor.makeCodeTree(Compressor.countFrequencies("WarAndPeace.txt"));
        Map warAndPeaceMap = Compressor.computeCodes(warAndPeaceTree);
        Compressor.compressFile(warAndPeaceMap, "WarAndPeace.txt", "WarAndPeaceCompress.txt");
        Compressor.decompressFile("WarAndPeaceCompress.txt", "WarAndPeaceDecompress.txt", warAndPeaceTree);


        // Testing file with single letter
        BinaryTree<CodeTreeElement> singleLetterTree = Compressor.makeCodeTree(Compressor.countFrequencies("singleLetter.txt"));
        Map singleLetterMap = Compressor.computeCodes(singleLetterTree);
        Compressor.compressFile(singleLetterMap, "singleLetter.txt", "singleLetterCompress.txt");
        Compressor.decompressFile("singleLetterCompress.txt", "singleLetterDecompress.txt", singleLetterTree);

        // Testing file with repeating single letter
        BinaryTree<CodeTreeElement> repeatLetterTree = Compressor.makeCodeTree(Compressor.countFrequencies("singleLetter.txt"));
        Map repeatLetterMap = Compressor.computeCodes(repeatLetterTree);
        Compressor.compressFile(repeatLetterMap, "repeatLetter.txt", "repeatLetterCompress.txt");
        Compressor.decompressFile("repeatLetterCompress.txt", "repeatLetterDecompress.txt", repeatLetterTree);

        // Testing empty file
        BinaryTree<CodeTreeElement> emptyTree = Compressor.makeCodeTree(Compressor.countFrequencies("empty.txt"));
        Map emptyMap = Compressor.computeCodes(emptyTree);
        Compressor.compressFile(emptyMap, "empty.txt", "emptyCompress.txt");
        Compressor.decompressFile("emptyCompress.txt", "emptyDecompress.txt", emptyTree);

    }
}
