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

import phases.CycleBreakingLayerPhase;
import phases.LayerAssignmentLayerPhase;
import phases.LayerPhase;
import trees.options.TreesOptions;

/**
 * A simple layout algorithm class. This algorithm already supports a number of layout options, places nodes, and
 * routes edges.
 */
public class TreesLayoutProvider extends AbstractLayoutProvider {

    LayerPhase[] basePhases = new LayerPhase[] { 
            new CycleBreakingLayerPhase(), 
            new LayerAssignmentLayerPhase()
            };

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {
        // Start progress monitor
        progressMonitor.begin("LayeredLayouting", basePhases.length);
        progressMonitor.log("Algorithm began");
        
        // Set Start Size
        layoutGraph.setWidth(2000);
        layoutGraph.setHeight(2000);
        
        // Apply all phases
        for (LayerPhase p : basePhases) {
            // Create a sub monitor
            IElkProgressMonitor monitor = progressMonitor.subTask(1);
            monitor.begin(p.getClass().getName(), 1);
            monitor.logGraph(layoutGraph, "Start: " + p.getClass().getName());
            
            try {
                p.apply(layoutGraph, monitor);
            } catch (Exception e) {
                progressMonitor.log(p.getClass().getName() + " had an error!");
                e.printStackTrace();
                break;
            }
            
            // Close the sub monitor
            monitor.done();
            monitor.logGraph(layoutGraph, p.getClass().getName() + " done!");
        }
        
        // Set the size of the final diagram dynamically
        var nodes = layoutGraph.getChildren();
        var padding = layoutGraph.getProperty(TreesOptions.PADDING);
        layoutGraph.setWidth(nodes.stream().map(x -> x.getX() + x.getWidth()).max(Double::compare).get() + 
                padding.left + padding.right);
        layoutGraph.setHeight(nodes.stream().map(y -> y.getY() + y.getHeight()).max(Double::compare).get() + 
                padding.top + padding.bottom);
        
        // End the progress monitor
        progressMonitor.log("Algorithm executed");
        progressMonitor.logGraph(layoutGraph, "Final graph");
        progressMonitor.done();
    }
}
