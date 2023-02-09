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
        Map<Character,Long> frequencyMap = new TreeMap<Character, Long>();
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
                    Long currCount = frequencyMap.get(character);
                    frequencyMap.put(character, 1 + currCount);
                }
                // if character has not yet been identified in file, add to map
                else {
                    if(charInt != -1) {
                        frequencyMap.put(character, 1L);
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
        PriorityQueue<BinaryTree<CodeTreeElement>> priority = new PriorityQueue<BinaryTree<CodeTreeElement>>(TreeComparator);
        System.out.println(frequencies.get('\n'));
        for (Character key : frequencies.keySet()){

            System.out.println("Key " + key + " appears " + frequencies.get(key) + " time(s).");

            BinaryTree<CodeTreeElement> ele = new BinaryTree<>( new CodeTreeElement(frequencies.get(key), key));
            priority.add(ele);
        }
        while (priority.size() != 1) {
            //Extract the two lowest-frequency trees T1 and T2 from the priority queue.
            BinaryTree<CodeTreeElement> leftData =  priority.poll();
            BinaryTree<CodeTreeElement> left = new BinaryTree(leftData);
            BinaryTree<CodeTreeElement> rightData = priority.poll();
            BinaryTree<CodeTreeElement> right = new BinaryTree(rightData);
            //Create a new tree T by creating a new root node r, attaching T1 as r's left subtree, and attaching T2 as r's right subtree.
            //Assign to the new tree T a frequency that equals the sum of the frequencies of T1 and T2.
            BinaryTree<CodeTreeElement> root = new BinaryTree<CodeTreeElement>(new CodeTreeElement(leftData.data.myFrequency + rightData.data.myFrequency, null), left, right);
            //Insert the new tree T into the priority queue (which will base its priority on the frequency value it holds).
            priority.add(root);
        }
        return (BinaryTree<CodeTreeElement>) priority.poll();
    }

    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> codes = new TreeMap<Character, String>();
        String pathSoFar = "";
        codes = computeCodesHelper(codeTree, codes, pathSoFar);
        System.out.println("hehe" + codes);
        return codes;
    }
    public Map<Character, String> computeCodesHelper(BinaryTree<CodeTreeElement> codeTree, Map<Character, String> codes,  String pathSoFar) {

        if(codeTree.isLeaf()) {
            Character peep = codeTree.getData().getChar();
            codes.put(peep, pathSoFar);
        }
        if (codeTree.hasLeft()) {
            computeCodesHelper(codeTree.getLeft(), codes, pathSoFar + 0);
        }
        if (codeTree.hasRight()) {
            computeCodesHelper(codeTree.getRight(), codes, pathSoFar + 1);
        }
        return codes;

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
            BinaryTree<CodeTreeElement> codeTree = new BinaryTree<CodeTreeElement>(test0.makeCodeTree(test0.countFrequencies("test1.txt")));
            test0.computeCodes(codeTree);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
