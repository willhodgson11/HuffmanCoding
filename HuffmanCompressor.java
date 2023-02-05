import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class HuffmanCompressor implements Huffman{

    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map frequencyMap = new TreeMap<Character, Long>();
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        String line;
        while ((line = input.readLine()) != null) {
               Character character = (char) input.read();
               if (frequencyMap.containsKey(character)) {
                  frequencyMap.put(character, (Long)frequencyMap.get(character) +1);
               }else{
                   frequencyMap.put(character, 1);
            }
        }
        return frequencyMap;
    }

    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
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
}
