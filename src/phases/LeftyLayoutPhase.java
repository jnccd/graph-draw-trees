package phases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

import helper.Help;
import trees.options.TreesOptions;

public class LeftyLayoutPhase implements Phase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        var nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = layoutGraph.getProperty(TreesOptions.SPACING_NODE_NODE);
        var padding = layoutGraph.getProperty(TreesOptions.PADDING);
        
        var root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        List<List<ElkNode>> layers = new ArrayList<List<ElkNode>>();
        
        layers.add(new ArrayList<ElkNode>());
        layers.get(0).add(root);
        for (int i = 0; i < layers.size(); i++) {
            List<ElkNode> layerChildren = new ArrayList<ElkNode>();
            for (var n : layers.get(i))
                layerChildren.addAll(Help.getChildren(n));
            
            if (layerChildren.size() > 0) {
                layers.add(new ArrayList<ElkNode>());
                layers.get(i + 1).addAll(layerChildren);
            }
        }
        
        double curX = padding.left, curY = padding.top;
        for (var layer : layers) {
            curX = padding.left;
            for (var n : layer) {
                if (n.getIdentifier().equals("n4"))
                    getClass();
                
                n.setX(curX);
                n.setY(curY);
                
                curX += n.getWidth() + nodeNodeSpacing;
            }
            curY += layer.stream().map(x -> x.getHeight()).max(Double::compare).get() + nodeNodeSpacing;
        }
    }
}
