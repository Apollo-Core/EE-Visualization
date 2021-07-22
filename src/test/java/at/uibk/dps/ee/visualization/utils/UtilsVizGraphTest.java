package at.uibk.dps.ee.visualization.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Task;

public class UtilsVizGraphTest {

  @Test
  public void testElementCopyOkay() {

    Task task = new Task("t");
    task.setAttribute("attrT", 1);

    Communication comm = new Communication("c");
    comm.setAttribute("commT", "bla");

    Dependency dep = new Dependency("dep");
    dep.setAttribute("attrD", 3.2);

    Element result1 = UtilsVizGraph.copy(task);
    Element result2 = UtilsVizGraph.copy(comm);
    Element result3 = UtilsVizGraph.copy(dep);

    assertTrue(result1 instanceof Task);
    assertTrue(result2 instanceof Communication);
    assertTrue(result3 instanceof Dependency);

    assertEquals(1, (int) result1.getAttribute("attrT"));
    assertEquals("bla", result2.getAttribute("commT"));
    assertEquals(3.2, result3.getAttribute("attrD"), 0.0);
  }
}
