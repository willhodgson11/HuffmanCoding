import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class HuffmanCompressor implements Huffman{

    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map frequencyMap = new TreeMap<Character, Long>();
        BufferedReader input;

        // Open the file, if possible
        try {
            input = new BufferedReader(new FileReader(pathName));
        }
        catch (FileNotFoundException e) {
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
                Character character = (char)charInt;
                System.out.println(character);
                // if character has already been identified, increment frequency by 1
                if (frequencyMap.containsKey(character)) {
                    frequencyMap.put(character, 1 + (int)frequencyMap.get(character));
                }
                // if character has not yet been identified in file, add to map
                else {
                    if(charInt != -1) {
                        frequencyMap.put(character, 1);
                    }
                }
            }
        }
        catch (IOException e){
            System.err.println("IO error while reading.\n" + e.getMessage());
        }

        // Close the file, if possible
        try {
            input.close();
        }
        catch (IOException e) {
            System.err.println("Cannot close file.\n" + e.getMessage());
        }

        return frequencyMap;
    }

    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        Comparator TreeComparator = new TreeComparator();
        PriorityQueue priority = new PriorityQueue<CodeTreeElement>(TreeComparator);
        for (Character key : frequencies.keySet()){
            System.out.println("Key " + key + " appears " + frequencies.get(key) + " time(s).");
            CodeTreeElement temp = new CodeTreeElement(frequencies.get(key), key);
            priority.add(temp);
        }
        while (priority.size() != 0) {
            BinaryTree left = new BinaryTree(priority.poll());
            BinaryTree right = new BinaryTree(priority.poll());
            BinaryTree root = new BinaryTree<CodeTreeElement>(null, left, right);
        }
        return null;
    }

    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        return null;
    }

    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {

    }

    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {

    }

    public static void main(String[] args) {
        HuffmanCompressor test0 = new HuffmanCompressor();
        try {
            test0.makeCodeTree(test0.countFrequencies("test1.txt"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
