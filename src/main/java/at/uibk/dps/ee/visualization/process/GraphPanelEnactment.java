package at.uibk.dps.ee.visualization.process;

import java.awt.Color;
import java.awt.Shape;
import at.uibk.dps.ee.model.graph.EnactmentGraph;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.visualization.constants.GraphAppearance;
import at.uibk.dps.ee.visualization.constants.GraphAppearance.EGNodeShape;
import at.uibk.dps.ee.visualization.utils.UtilsVizGraph;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;
import net.sf.opendse.visualization.AbstractGraphPanelFormat;
import net.sf.opendse.visualization.ElementSelection;
import net.sf.opendse.visualization.Graphics;
import net.sf.opendse.visualization.LocalEdge;
import net.sf.opendse.visualization.algorithm.DistanceFlowLayout;

/**
 * The {@link GraphPanelEnactment} defines the visualization of the
 * {@link EnactmentGraph} part of the {@link EnactmentProcessWidget}.
 * 
 * @author Fedor Smirnov
 */
public class GraphPanelEnactment extends AbstractGraphPanelFormat {

  protected final EnactmentGraph enactmentGraph;
  protected final Mappings<Task, Resource> typeMappings;
  protected final Routings<Task, Resource, Link> routings;
  protected final EnactmentSpecification spec;
  protected final ElementSelection selection;

  public GraphPanelEnactment(EnactmentSpecification spec, ElementSelection selection) {
    this.selection = selection;
    this.spec = spec;
    this.enactmentGraph = spec.getEnactmentGraph();
    this.typeMappings = spec.getMappings();
    this.routings = spec.getRoutings();
  }


  @SuppressWarnings("unchecked")
  @Override
  public Graph<Node, Edge> getGraph() {
    Graph<?, ?> result = UtilsVizGraph.generateGraphToDraw(enactmentGraph);
    return (Graph<Node, Edge>) result;
  }

  @Override
  public Color getColor(Node node) {
    return GraphAppearance.getColorProcess(node);
  }

  @Override
  public Layout<Node, LocalEdge> getLayout(DirectedGraph<Node, LocalEdge> graph) {
    final Layout<Node, LocalEdge> result = new DistanceFlowLayout<Node, LocalEdge>(graph);
    return result;
  }

  @Override
  public Color getColor(Edge edge) {
    return Graphics.BLACK;
  }

  @Override
  public Shape getShape(Node node) {
    return getShapeForEnum(GraphAppearance.getShape(node), node);
  }

  @Override
  public int getSize(Node node) {
    return GraphAppearance.getSize(node);
  }

  @Override
  public boolean isActive(Node node) {
    if (selection.isNull() || selection.isSelected(node)) {
      // Either nothing selection of this task is selected
      return true;
    } else if (selection.get() instanceof Resource) {
      Resource res = selection.get();
      // Selection is a resource => check whether the current node has a relation to
      // it
      if (node instanceof Task) {
        Task task = (Task) node;
        if (TaskPropertyService.isProcess(task)) {
          // check whether we are mapped on the resource
          return typeMappings.getSources(res).contains(task);
        } else if (TaskPropertyService.isCommunication(task)) {
          // check whether we are routed over the resource
          return routings.get(task).containsVertex(res);
        } else {
          throw new IllegalArgumentException("Unknown type of task: " + task.getId());
        }
      }
    } else if (selection.get() instanceof Mapping<?, ?>) {
      // mapping selected => check whether it is ours
      Mapping<Task, Resource> mapping = selection.get();
      return node.equals(mapping.getSource());
    }
    return false;
  }

  @Override
  public boolean isActive(Edge edge, Node n0, Node n1) {
    Pair<Task> endpoints = enactmentGraph.getEndpoints((Dependency) edge);
    Task t0 = endpoints.getFirst();
    Task t1 = endpoints.getSecond();
    return isActive(t0) && isActive(t1);
  }

  /**
   * Returns a shape object for the enum used by the configuration class.
   * 
   * @param shapeEnum the shape enum
   * @param node the node with the corresponding shape
   * @return the shape object
   */
  protected Shape getShapeForEnum(EGNodeShape shapeEnum, Node node) {
    switch (shapeEnum) {
      case Ellipse: {
        return shapes.getEllipse(node);
      }
      case Rectangle: {
        return shapes.getRectangle(node);
      }
      default:
        throw new IllegalArgumentException("Unexpected value: " + shapeEnum.name());
    }
  }

}
