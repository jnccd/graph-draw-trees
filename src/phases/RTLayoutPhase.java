package phases;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

import helper.Help;
import trees.options.TreesOptions;

public class RTLayoutPhase implements Phase {
    double minSep = 25;

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        var nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = layoutGraph.getProperty(TreesOptions.SPACING_NODE_NODE);
        var padding = layoutGraph.getProperty(TreesOptions.PADDING);
        
        var root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        
        phase1(root);
        root.setX(-phase2(root) + padding.left);
        phase3(root, root.getX(), 0, nodeNodeSpacing, padding);
    }

    void phase1(ElkNode n) {
        var childs = Help.getChilds(n);
        ElkNode leftChild = null, rightChild = null;
        if (childs.size() > 0) 
            leftChild = childs.get(0);
        if (childs.size() > 1) 
            rightChild = childs.get(1);
        for (var c : childs)
            phase1(c);
        
        if (leftChild != null) 
            Help.getProp(leftChild).xOffset = -minSep;
        if (rightChild != null)
            Help.getProp(rightChild).xOffset = minSep;
        
        double dv = minSep * 2, dl = 0, dr = 0;
        if (leftChild != null && rightChild != null) { // TODO: Add proper contourTraversal
            ElkNode l = leftChild, r = rightChild;
            while (Help.getChilds(l).size() > 1 && Help.getChilds(r).size() > 0) {
                dl += Help.getProp(Help.getChilds(l).get(1)).xOffset;
                dr += Help.getProp(Help.getChilds(r).get(0)).xOffset;
                
                if (dr - dl - l.getWidth() + dv - minSep < 0) 
                    dv = dl + l.getWidth() - dr + minSep;
                
                l = Help.getChilds(l).get(1);
                r = Help.getChilds(r).get(0);
            }
        }
        
        if (leftChild != null) 
            Help.getProp(leftChild).xOffset = -dv / 2;
        if (rightChild != null)
            Help.getProp(rightChild).xOffset = dv / 2;
    }
    
    double phase2(ElkNode r) {
        double re = 0.0;
        while (Help.getChilds(r).size() > 0) {
            re += Help.getProp(r).xOffset;
            r = Help.getChilds(r).get(0);
        }
        return re;
    }
    
    void phase3(ElkNode r, double rootOffset, int depth, 
            double nodeNodeSpacing, ElkPadding padding) {
        var childs = Help.getChilds(r);
        ElkNode leftChild = null, rightChild = null;
        if (childs.size() > 0) 
            leftChild = childs.get(0);
        if (childs.size() > 1) 
            rightChild = childs.get(1);
        
        r.setX(Help.getProp(r).xOffset + rootOffset);
        r.setY(depth * (r.getHeight() + nodeNodeSpacing) + padding.top);
        
        for (var c : childs)
            phase3(c, r.getX(), depth + 1, nodeNodeSpacing, padding);
    }
}
