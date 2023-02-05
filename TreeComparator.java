import java.util.Comparator;

public class TreeComparator implements Comparator<CodeTreeElement> {
    @Override
    public int compare(CodeTreeElement tree1, CodeTreeElement tree2) {
        if(tree1.myFrequency < tree2.myFrequency){return -1;}
        else if(tree1.myFrequency > tree2.myFrequency){return 0;}
        else{return 1;
        }
    }
}
