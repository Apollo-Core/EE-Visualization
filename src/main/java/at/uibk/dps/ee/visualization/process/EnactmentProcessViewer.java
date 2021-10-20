package at.uibk.dps.ee.visualization.process;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opt4j.core.start.Constant;
import org.opt4j.viewer.ToolBar;
import org.opt4j.viewer.Viewport;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import at.uibk.dps.ee.visualization.constants.ConstantsVisulization;
import at.uibk.dps.ee.core.ModelModificationListener;
import at.uibk.dps.ee.core.function.EnactmentStateListener;

/**
 * The {@link EnactmentProcessViewer} is used to visualize the enactment
 * process.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class EnactmentProcessViewer implements EnactmentStateListener, ModelModificationListener {

  // config attributes
  protected final boolean closeOnStop;

  // graphic elements
  protected JFrame frame = null; // the overall window
  protected final Viewport viewPort; // the viewport for the visualizations
  protected final ToolBar toolBar; // the bar with the buttons

  protected final EnactmentProcessWidget widget;
  protected final ScheduledExecutorService updateScheduler;
  protected final int updatePeriod;

  @Inject
  public EnactmentProcessViewer(Viewport viewport, ToolBar toolBar,
      EnactmentProcessWidget enactmentWidget,
      @Constant(namespace = EnactmentProcessViewer.class,
          value = "closeOnTerminate") boolean closeOnStop,
      @Constant(namespace = EnactmentProcessViewer.class,
          value = "updatePeriod") int updatePeriod) {
    this.closeOnStop = closeOnStop;
    this.viewPort = viewport;
    this.toolBar = toolBar;
    this.widget = enactmentWidget;
    this.updateScheduler = Executors.newSingleThreadScheduledExecutor();
    this.updatePeriod = updatePeriod;
  }

  @Override
  public void enactmentStarted() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException
        | InstantiationException exc) {
      throw new IllegalStateException(
          "Exception thrown when setting the look & feel of the enactment viewer.");
    }
    EnactmentProcessViewer.this.frame = new JFrame();
    final JFrame frame = EnactmentProcessViewer.this.frame;

    // TODO Adjust when we have a logo (preferably Apollo)
    // ImageIcon logo = Icons.getIcon(Icons.OPT4J);
    // frame.setIconImage(logo.getImage());
    frame.setTitle(ConstantsVisulization.ProcessViewerTitle);
    frame.setLayout(new BorderLayout());
    frame.setPreferredSize(new Dimension(800, 600));

    viewPort.init();
    toolBar.init();
    frame.add(viewPort.get(), BorderLayout.CENTER);
    frame.add(toolBar.get(), BorderLayout.NORTH);

    // TODO we could add an actual termination of the enactment if the window is
    // closed (as well as add the option for a termination)

    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    viewPort.addWidget(widget);

    updateScheduler.scheduleAtFixedRate(this::updateFrame, 0, updatePeriod, TimeUnit.MILLISECONDS);
  }

  @Override
  public void enactmentTerminated() {
    if (closeOnStop && frame != null) {
      frame.dispose();
    }
    updateFrame();
    updateScheduler.shutdown();
  }

  protected void updateFrame() {
    if (frame != null) {
      //widget.update();
      frame.repaint();
    }
  }

  @Override
  public void reactToModelModification() {
    widget.update();
    if (frame != null) {
      frame.repaint();
    }    
  }
}
