package phases;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

public class FixNodeCoordsPhase implements Phase {

    @Override
    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        var nodes = layoutGraph.getChildren();
        
        var minX = nodes.stream().
                mapToDouble(x -> x.getX()).min().
                getAsDouble();
        var minY = nodes.stream().
                mapToDouble(y -> y.getY()).min().
                getAsDouble();
        
        monitor.logGraph(layoutGraph, "MinX: " + minX + " MinY: " + minY);
        
        for (var n : nodes) {
            n.setX(n.getX() - minX);
            n.setY(n.getY() - minY);
        }
    }

}
