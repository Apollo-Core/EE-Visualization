package at.uibk.dps.ee.visualization.process;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import at.uibk.dps.ee.model.graph.EnactmentGraphProvider;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.persistance.MappingsTransformer;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.ElementSelection;
import net.sf.opendse.visualization.GraphPanel;
import net.sf.opendse.visualization.MappingPanel;

/**
 * The {@link EnactmentPanel} visualizes the enactment process via the enactment
 * and the resource graph.
 * 
 * @author Fedor Smirnov
 *
 */
public class EnactmentPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  protected final EnactmentSpecification actualSpec;
  protected final EnactmentGraphProvider eGraphProvider;

  public EnactmentPanel(EnactmentSpecification specification,
      EnactmentGraphProvider eGraphProvider) {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.actualSpec = specification;
    this.eGraphProvider = eGraphProvider;
    updateSpecification();
  }

  protected void updateSpecification() {
    this.removeAll();

    Mappings<Task, Resource> mappings = MappingsTransformer.toOdse(actualSpec.getMappings());
    ElementSelection selection = new ElementSelection();

    GraphPanel panelEnactmentGraph =
        new GraphPanel(new GraphPanelEnactment(actualSpec, selection, eGraphProvider), selection);
    GraphPanel panelResourceGraph =
        new GraphPanel(new GraphPanelResources(actualSpec, selection), selection);
    MappingPanel mappingPanel = new MappingPanel(mappings, selection);

    JSplitPane splitG = new JSplitPane(HORIZONTAL_SPLIT, panelEnactmentGraph, panelResourceGraph);
    JSplitPane split = new JSplitPane(HORIZONTAL_SPLIT, mappingPanel, splitG);

    this.setLayout(new BorderLayout());
    this.add(split);

    revalidate();
    repaint();
  }

  public void update() {
    updateSpecification();
  }
}
