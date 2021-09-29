package at.uibk.dps.ee.visualization.process;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Random;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.ResourceGraph;
import at.uibk.dps.ee.model.persistance.EnactmentSpecTransformer;
import at.uibk.dps.ee.visualization.constants.GraphAppearance;
import at.uibk.dps.ee.visualization.utils.UtilsVizGraph;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.AbstractGraphPanelFormat;
import net.sf.opendse.visualization.ElementSelection;
import net.sf.opendse.visualization.Graphics;
import net.sf.opendse.visualization.LocalEdge;

import com.google.common.base.Function;

/**
 * The {@link GraphPanelResources} defines the way that the
 * {@link ResourceGraph} is displayed within the {@link EnactmentProcessWidget}.
 * 
 * @author Fedor Smirnov
 */
public class GraphPanelResources extends AbstractGraphPanelFormat {

  protected final Specification spec;
  protected final Architecture<Resource, Link> resGraph;
  protected final Mappings<Task, Resource> mappings;
  protected final Routings<Task, Resource, Link> routings;
  protected final ElementSelection selection;

  public GraphPanelResources(EnactmentSpecification apolloSpec, ElementSelection selection) {
    this.selection = selection;
    this.spec = EnactmentSpecTransformer.toOdse(apolloSpec);
    this.resGraph = spec.getArchitecture();
    this.mappings = spec.getMappings();
    this.routings = spec.getRoutings();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Graph<Node, Edge> getGraph() {
    Graph<?, ?> g = UtilsVizGraph.generateResGraphToDraw(resGraph);
    return (Graph<Node, Edge>) g;
  }

  @Override
  public Layout<Node, LocalEdge> getLayout(DirectedGraph<Node, LocalEdge> graph) {
    // A layout where the edges draw nodes together, while the nodes repulse each
    // other
    FRLayout<Node, LocalEdge> layout = new FRLayout<Node, LocalEdge>(graph);
    layout.setSize(new Dimension(600, 600));
    layout.setAttractionMultiplier(5);
    layout.setRepulsionMultiplier(0.1);

    final Dimension size = layout.getSize();
    final Random random = new Random(0);

    layout.setInitializer(new Function<Node, Point2D>() {
      @Override
      public Point2D apply(Node arg0) {
        return new Point2D.Double(size.getWidth() / 2 + random.nextDouble(),
            size.getHeight() / 2 + random.nextDouble());
      }
    });
    return layout;
  }

  @Override
  public Color getColor(Edge edge) {
    return Graphics.BLACK;
  }

  @Override
  public Color getColor(Node node) {
    return GraphAppearance.getResourceColor((Resource) node);
  }

  @Override
  public Shape getShape(Node node) {
    return shapes.getRoundRectangle(node);
  }

  @Override
  public int getSize(Node node) {
    return 25;
  }

  @Override
  public boolean isActive(Node node) {
    if (selection.isNull() || selection.isSelected(node)) {
      return true;
    } else if (selection.get() instanceof Task) {
      Task task = selection.get();
      if (selection.get() instanceof ICommunication) {
        return routings.get(task).containsVertex((Resource) node);
      } else {
        return mappings.getTargets(task).contains(node);
      }
    } else if (selection.get() instanceof Mapping<?, ?>) {
      Mapping<Task, Resource> m = selection.get();
      return node.equals(m.getTarget());
    }
    return false;
  }

  @Override
  public boolean isActive(Edge edge, Node n0, Node n1) {
    if (!selection.isNull() && selection.get() instanceof ICommunication) {
      Architecture<Resource, Link> routing = routings.get((Task) selection.get());
      return routing.containsVertex((Resource) n0) && routing.containsVertex((Resource) n1)
          && routing.findEdgeSet((Resource) n0, (Resource) n1).contains(edge);
    } else {
      Pair<Resource> endpoints = resGraph.getEndpoints((Link) edge);
      Resource r0 = endpoints.getFirst();
      Resource r1 = endpoints.getSecond();
      return isActive(r0) && isActive(r1);
    }
  }


  // The two methods below are necessary to distinguish between architecture and
  // routing resources.
  @Override
  public String getTooltip(Node node) {
    if (!selection.isNull() && selection.get() instanceof ICommunication) {
      Architecture<Resource, Link> routing = routings.get((Task) selection.get());
      return super.getTooltip(routing.getVertex((Resource) node));
    } else {
      return super.getTooltip(node);
    }
  }

  @Override
  public String getTooltip(Edge edge) {
    if (!selection.isNull() && selection.get() instanceof ICommunication) {
      Architecture<Resource, Link> routing = routings.get((Task) selection.get());
      return super.getTooltip(routing.getEdge((Link) edge));
    } else {
      return super.getTooltip(edge);
    }
  }

}
