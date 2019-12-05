package trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import phases.BinaryTreeCheckPhase;
import phases.EdgeRoutingLayerPhase;
import phases.FixNodeCoordsPhase;
import phases.InorderLayoutPhase;
import phases.LeftyLayoutPhase;
import phases.Phase;
import phases.RTLayoutPhase;
import trees.options.TreesOptions;

/**
 * A simple layout algorithm class. This algorithm already supports a number of layout options, places nodes, and routes
 * edges.
 */
public class TreesLayoutProvider extends AbstractLayoutProvider {
    /*
     * This algorithm assumes that the the order of child nodes is given by their position in
     * the result of Help.getChildren() which in turn gets the positions by concatenating all
     * the target node lists from all outgoing edges onto each other.
     */
    
    Phase[] prePhases = new Phase[] { 
            new BinaryTreeCheckPhase() 
            };
    Phase[] postPhases = new Phase[] { 
            new FixNodeCoordsPhase(), 
            new EdgeRoutingLayerPhase() 
            };

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("TreeLayouting", prePhases.length);
        progressMonitor.log("Algorithm began");
        
        // Set Start Size
        layoutGraph.setWidth(2000);
        layoutGraph.setHeight(2000);

        // Apply all prePhases
        for (Phase p : prePhases) 
            if (!applyPhase(layoutGraph, progressMonitor, p))
                break;
        
        // Apply the chosen phases
        switch (layoutGraph.getProperty(TreesOptions.LAYOUT_ALGORITHM)) {
            case 0:
                applyPhase(layoutGraph, progressMonitor, new LeftyLayoutPhase());
                break;
            case 1:
                applyPhase(layoutGraph, progressMonitor, new InorderLayoutPhase());
                break;
            case 2:
                applyPhase(layoutGraph, progressMonitor, new RTLayoutPhase());
                break;
            default:
                System.out.println("Thats not a valid algorithm id!");
        }

        // Apply all postPhases
        for (Phase p : postPhases) 
            if (!applyPhase(layoutGraph, progressMonitor, p))
                break;

        // Set the size of the final diagram dynamically
        var nodes = layoutGraph.getChildren();
        var padding = layoutGraph.getProperty(TreesOptions.PADDING);
        layoutGraph.setWidth(nodes.stream().map(x -> x.getX() + x.getWidth()).max(Double::compare).get() + padding.left
                + padding.right);
        layoutGraph.setHeight(nodes.stream().map(y -> y.getY() + y.getHeight()).max(Double::compare).get() + padding.top
                + padding.bottom);

        // End the progress monitor
        progressMonitor.log("Algorithm executed");
        progressMonitor.logGraph(layoutGraph, "Final graph");
        progressMonitor.done();
    }

    public boolean applyPhase(ElkNode layoutGraph, IElkProgressMonitor progressMonitor, Phase p) {
        // Create a sub monitor
        IElkProgressMonitor monitor = progressMonitor.subTask(1);
        monitor.begin(p.getClass().getName(), 1);
        monitor.logGraph(layoutGraph, "Start: " + p.getClass().getName());

        try {
            p.apply(layoutGraph, monitor);
        } catch (Exception e) {
            progressMonitor.log(p.getClass().getName() + " had an error!");
            e.printStackTrace();
            return false;
        }

        // Close the sub monitor
        monitor.done();
        monitor.logGraph(layoutGraph, p.getClass().getName() + " done!");
        return true;
    }
}
