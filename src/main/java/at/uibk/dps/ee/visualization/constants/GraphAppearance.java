package at.uibk.dps.ee.visualization.constants;

import java.awt.Color;
import at.uibk.dps.ee.model.properties.PropertyServiceData;
import at.uibk.dps.ee.model.properties.PropertyServiceData.NodeType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction.UsageType;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.Graphics;

/**
 * Static method container for the configuration of the appearance of the graph
 * visualizations.
 * 
 * @author Fedor Smirnov
 *
 */
public final class GraphAppearance {

  /**
   * The node shapes used for the visualization of enactment graphs.
   * 
   * @author Fedor Smirnov
   *
   */
  public enum EGNodeShape {
    Ellipse, Rectangle
  }

  // shapes
  protected static final EGNodeShape shapeFunction = EGNodeShape.Ellipse;
  protected static final EGNodeShape shapeData = EGNodeShape.Rectangle;

  // colors

  // functions
  protected static final Color colorFunctionModel = Graphics.GREEN;
  protected static final Color colorFunctionWaiting = Graphics.GRAY;
  protected static final Color colorFunctionReady = Graphics.YELLOW;
  protected static final Color colorFunctionRunning = Graphics.BLUE;
  protected static final Color colorFunctionFinished = Graphics.GREEN;
  protected static final Color colorFunctionUtility = Graphics.VIOLET;
  protected static final Color colorFunctionSyntax = Graphics.CHOCOLATE;
  protected static final Color colorFunctionError = Graphics.RED;

  // data
  protected static final Color colorDataModel = Graphics.BLUE;
  protected static final Color colorDataUnavailable = Graphics.GRAY;
  protected static final Color colorDataAvailable = Graphics.GREEN;
  protected static final Color colorDataConstant = Graphics.LAWNGREEN;
  protected static final Color colorDecisionTrue = Graphics.BLUE;
  protected static final Color colorDecisionFalse = Graphics.RED;

  // resources
  protected static final Color colorResRunning = Graphics.GREEN;
  protected static final Color colorResIdle = Graphics.STEELBLUE;


  // sizes
  protected static final int sizeFunction = 20;
  protected static final int sizeData = 15;

  /**
   * No constructor.
   */
  private GraphAppearance() {}

  public static Color getResourceColor(Resource res) {
    if (PropertyServiceResource.getUsingTaskIds(res).isEmpty()) {
      return colorResIdle;
    } else {
      return colorResRunning;
    }
  }

  /**
   * Returns the shape enum for the given node.
   * 
   * @param node the given node
   * @return the shape enum for the given node
   */
  public static final EGNodeShape getShape(Node node) {
    if (node instanceof Communication) {
      return shapeData;
    } else if (node instanceof Task) {
      return shapeFunction;
    } else {
      throw new IllegalArgumentException("Unknown type of node: " + node.getId());
    }
  }


  /**
   * Returns the color for the given node when draws as model visualization.
   * 
   * @param node the given node
   * @return the color for the given node.
   */
  public static final Color getColorModel(Node node) {
    if (node instanceof Communication) {
      return colorDataModel;
    } else if (node instanceof Task) {
      return colorFunctionModel;
    } else {
      throw new IllegalArgumentException("Unknown type of node: " + node.getId());
    }
  }

  /**
   * Returns the color for the given node when draws as process visualization.
   * 
   * @param node the given node
   * @return the color for the given node.
   */
  public static final Color getColorProcess(Node node) {
    if (node instanceof Communication) {
      // data node
      Communication dataNode = (Communication) node;
      NodeType nodeType = PropertyServiceData.getNodeType(dataNode);
      switch (nodeType) {
        case Default:
          return PropertyServiceData.isDataAvailable(dataNode) ? colorDataAvailable
              : colorDataUnavailable;
        case Constant:
          return colorDataConstant;
        case Decision:
          if (PropertyServiceData.isDataAvailable(dataNode)) {
            return getDecisionVariableContent(dataNode) ? colorDecisionTrue : colorDecisionFalse;
          } else {
            return colorDataUnavailable;
          }
        case Sequentiality:
          return Graphics.BLACK;
//        case WhileStart:
//          return Graphics.PURPLE;
//        case WhileCounter:
//          return Graphics.PURPLE;
        default:
          throw new IllegalStateException(
              "No color known for data node of type " + nodeType.name());
      }
    } else if (node instanceof Task) {
      // function node
      Task taskNode = (Task) node;
      if (PropertyServiceFunction.getUsageType(taskNode).equals(UsageType.DataFlow)) {
        return colorFunctionSyntax;
      }
      if (PropertyServiceFunction.getUsageType(taskNode).equals(UsageType.Utility)) {
        return colorFunctionUtility;
      }
      if (isTaskFinished(taskNode)) {
        return colorFunctionFinished;
      } else if (isTaskReady(taskNode)){
        return colorFunctionReady;
      }else {
        return colorFunctionWaiting;
      }
      // State state = PropertyServiceFunction.getEnactable(taskNode).getState();
      // switch (state) {
      // case WAITING:
      // return colorFunctionWaiting;
      // case RUNNING:
      // return colorFunctionRunning;
      // case SCHEDULABLE:
      // return colorFunctionReady;
      // case LAUNCHABLE:
      // return colorFunctionReady; // TODO give it a different color
      // case FINISHED:
      // return colorFunctionFinished;
      // case STOPPED:
      // return colorFunctionError;
      // default:
      // throw new IllegalStateException("Unexpected enactable state " +
      // state.name());
      // }
    } else {
      throw new IllegalArgumentException("Unknown type of node: " + node.getId());
    }
  }

  protected static boolean isTaskReady(Task task) {
    return PropertyServiceFunction.isInputSet(task);
  }
  
  protected static boolean isTaskFinished(Task task) {
    return PropertyServiceFunction.isOutputSet(task);
  }

  /**
   * Returns the boolean value stored in the given decision variable task.
   * 
   * @param decisionVariableTask the given decision variable task.
   * @return the boolean value stored in the given decision variable task
   */
  protected static boolean getDecisionVariableContent(Task decisionVariableTask) {
    return PropertyServiceData.getContent(decisionVariableTask).getAsBoolean();
  }

  /**
   * Returns the size of the given node.
   * 
   * @param node the given node
   * @return the size of the given node
   */
  public static final int getSize(Node node) {
    if (node instanceof Communication) {
      return sizeData;
    } else if (node instanceof Task) {
      return sizeFunction;
    } else {
      throw new IllegalArgumentException("Unknown type of node: " + node.getId());
    }
  }
}
