import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>>{
    @Override
    public int compare(BinaryTree<CodeTreeElement> tree1, BinaryTree<CodeTreeElement> tree2) {
        if(tree1.data.myFrequency < tree2.data.myFrequency){return -1;}
        else if(tree1.data.myFrequency > tree2.data.myFrequency){return 0;}
        else{return 1;
        }
    }
}
