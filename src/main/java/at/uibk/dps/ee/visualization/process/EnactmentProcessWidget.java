package at.uibk.dps.ee.visualization.process;

import javax.swing.JPanel;

import org.opt4j.viewer.Viewport;
import org.opt4j.viewer.Widget;

import com.google.inject.Inject;
import at.uibk.dps.ee.model.graph.EnactmentGraphProvider;
import at.uibk.dps.ee.model.graph.SpecificationProvider;

/**
 * The widget to display the state of the enactment.
 * 
 * @author Fedor Smirnov
 *
 */
public class EnactmentProcessWidget implements Widget {

  protected EnactmentPanel panel;

  @Inject
  public EnactmentProcessWidget(SpecificationProvider specProvider,
      EnactmentGraphProvider eGraphProvider) {
    this.panel = new EnactmentPanel(specProvider.getSpecification(), eGraphProvider);
  }

  @Override
  public JPanel getPanel() {
    return panel;
  }

  @Override
  public void init(Viewport viewport) {
    // Nothing to do here
  }

  public void update() {
    panel.update();
  }
}
