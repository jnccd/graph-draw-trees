package phases;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

import helper.Help;
import trees.options.TreesOptions;

public class InorderLayoutPhase implements Phase {
    List<ElkNode> inorderVisiting = new ArrayList<ElkNode>();

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        var nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = layoutGraph.getProperty(TreesOptions.SPACING_NODE_NODE);
        var padding = layoutGraph.getProperty(TreesOptions.PADDING);
        
        var root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        inorder(root);
        
        var curX = padding.left;
        for (var n : inorderVisiting) {
            n.setX(curX);
            n.setY(rootDistance(n, root) * (n.getHeight() + nodeNodeSpacing));
            
            curX += n.getWidth() + nodeNodeSpacing;
        }
    }
    
    int rootDistance(ElkNode n, ElkNode root) {
        int re = 0;
        while (n != root) {
            n = Help.getParents(n).get(0);
            re++;
        }
        return re;
    }
    
    void inorder(ElkNode n) {
        var childs = Help.getChilds(n);
        if (childs.size() > 0) {
            var leftChild = childs.get(0);
            inorder(leftChild);
        }
        
        inorderVisiting.add(n);
        
        if (childs.size() > 1) {
            var rightChild = childs.get(1);
            inorder(rightChild);
        }
    }
}
