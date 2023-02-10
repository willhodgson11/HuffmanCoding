import java.util.Comparator;


/** Tree comparator class for Huffman Compressor.
 *
 * @author willhodgson, Dartmouth CS 10, Winter 2023
 * @author cullumtwiss, Dartmouth CS 10, Winter 2023

 */
public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>>{

    /**
     *
     * @param tree1 the first object to be compared.
     * @param tree2 the second object to be compared.
     * @return  -1, 0, or 1 depending on whether the first has a smaller frequency count,
     * the counts are equal, or the second has the smaller frequency count
     */
    @Override
    public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {
        return (int)(tree1.data.myFrequency - tree2.data.myFrequency);
    }
}
