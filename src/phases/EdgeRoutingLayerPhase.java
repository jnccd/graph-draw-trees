package phases;

import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import helper.EdgeRoutingProvider;
import helper.Help;

public class EdgeRoutingLayerPhase implements Phase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) {
        EdgeRoutingProvider.straightEdgeRouting(layoutGraph, monitor);
    }
}
