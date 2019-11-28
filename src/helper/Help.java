package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;

import properties.EdgeProperty;
import properties.GraphProperty;
import properties.NodeProperty;

public class Help {
    public static NodeProperty getProp(ElkNode e) {
        Property<NodeProperty> prop = new Property<NodeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new NodeProperty());
        return e.getProperty(prop);
    }
    public static EdgeProperty getProp(ElkEdge e) {
        Property<EdgeProperty> prop = new Property<EdgeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new EdgeProperty());
        return e.getProperty(prop);
    }
    public static GraphProperty getGraphProp(ElkNode n) {
        Property<GraphProperty> prop = new Property<GraphProperty>("graph-prop");
        
        if (n.hasProperty(prop))
            return n.getProperty(prop);
        
        n.setProperty(prop, new GraphProperty());
        return n.getProperty(prop);
    }
    
    public static List<ElkNode> getChilds(ElkNode n) {
        var outs = n.getOutgoingEdges();
        List<ElkNode> re = new ArrayList<ElkNode>();
        for (var out : outs) {
            for (var target : out.getTargets())
                if (ElkNode.class.isAssignableFrom(target.getClass())) {
                    re.add((ElkNode)target);
                }
        }
        re = re.stream().distinct().collect(Collectors.toList());
        return re;
    }
    public static List<ElkNode> getParents(ElkNode n) {
        var outs = n.getIncomingEdges();
        List<ElkNode> re = new ArrayList<ElkNode>();
        for (var out : outs) {
            for (var target : out.getSources())
                if (ElkNode.class.isAssignableFrom(target.getClass())) {
                    re.add((ElkNode)target);
                }
        }
        re = re.stream().distinct().collect(Collectors.toList());
        return re;
    }
}
