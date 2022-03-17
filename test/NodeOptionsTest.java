import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import ultimate.DiscFlyingState;
import ultimate.DiscHeldState;
import ultimate.DiscLandedState;
import ultimate.DiscThrownState;
import ultimate.Node;
import ultimate.Pitch;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Created by mc on 08/02/2016.
 */
public class NodeOptionsTest {
  @Before
  public void setUp() throws Exception {
    Pitch.setHeight(20);
    Pitch.setWidth(20);
    Pitch.setDiscVelocity(3);
  }

  @Test
  public void testPickup() throws Exception {
    Node node = NodeTest.getNode(10, 10, 10, 10);
    node.getDisc().setState(DiscLandedState.getInstance());
    node.getPlayer().setState(PlayerMovingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    int numMoving = 0;
    int numCatching = 0;
    for (Node n: successors) {
      if (n.getPlayer().getState() == PlayerCatchingState.getInstance()) {
        assertEquals(DiscHeldState.getInstance(), n.getDisc().getState());
        numCatching++;
      }
      if (n.getPlayer().getState() == PlayerMovingState.getInstance()) {
        assertEquals(DiscLandedState.getInstance(), n.getDisc().getState());
        numMoving++;
      }
    }
    assertEquals(1, numCatching);
    assertEquals(5, numMoving);
    assertEquals(6, successors.size());
  }

  @Test
  public void testThrow() throws Exception {
    Node node = NodeTest.getNode(10, 10, 10, 10);
    node.getDisc().setState(DiscHeldState.getInstance());
    node.getPlayer().setState(PlayerHoldingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    // No throwing options because no other players
    assertEquals(0, successors.size());
  }

  @Test
  public void testFly() throws Exception {
    Node node = NodeTest.getNode(10, 10, 10, 10);
    node.getPlayer().notifyDisc(node.getDisc());
    node.getPlayer().throwDisc(15, 14);

    HashSet<Node> successors = node.getOptions();

    for (Node n: successors) {
      assertSame(n.getPlayer().getState(), PlayerMovingState.getInstance());
      assertSame(n.getDisc().getState(), DiscFlyingState.getInstance());
      assertEquals(15, n.getDisc().getDestX());
      assertEquals(14, n.getDisc().getDestY());
    }
    assertEquals(5, successors.size());
  }

  @Test
  public void testLand() throws Exception {
    Node node = NodeTest.getNode(10, 10, 10, 10);
    Pitch.setDiscVelocity(100);
    node.getPlayer().setState(PlayerMovingState.getInstance());
    node.getPlayer().notifyDisc(node.getDisc());
    node.getPlayer().throwDisc(15, 14);
    node.getDisc().setState(DiscFlyingState.getInstance());
    node.setLastThrower(node.getPlayer());

    HashSet<Node> successors = node.getOptions();

    for (Node n: successors) {
      assertSame(n.getPlayer().getState(), PlayerMovingState.getInstance());
      assertSame(n.getDisc().getState(), DiscLandedState.getInstance());
      assertEquals(15, n.getDisc().getX());
      assertEquals(14, n.getDisc().getY());
    }
    assertEquals(5, successors.size());
  }

  @Test
  public void testCatchAtDestination() throws Exception {
    Node node = NodeTest.getNode(10, 10, 13, 17);
    Pitch.setDiscVelocity(100);
    node.getPlayer().setState(PlayerMovingState.getInstance());
    node.getDisc().throwTo(10, 10);
    node.getDisc().setState(DiscFlyingState.getInstance());
    node.getDisc().act();

    HashSet<Node> successors = node.getOptions();

    int numMoving = 0;
    int numCatching = 0;
    for (Node n: successors) {
      if (n.getPlayer().getState() == PlayerCatchingState.getInstance()) {
        assertEquals(DiscHeldState.getInstance(), n.getDisc().getState());
        numCatching++;
      }
      if (n.getPlayer().getState() == PlayerMovingState.getInstance()) {
        assertEquals(DiscLandedState.getInstance(), n.getDisc().getState());
        numMoving++;
      }
    }
    assertEquals(1, numCatching);
    assertEquals(5, numMoving);
    assertEquals(6, successors.size());
  }

  @Test
  public void testCollision() throws Exception {
    Node node = NodeTwoPlayerTest.getNode(0, 0, 10, 10, 11, 10);
    node.getPlayer().setState(PlayerMovingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    assertEquals(4, successors.size());
  }

  @Test
  public void testCatchEnRoute() throws Exception {
    Node node = NodeTest.getNode(10, 10, 0, 10);
    node.getPlayer().setState(PlayerMovingState.getInstance());
    Pitch.setDiscVelocity(100);
    node.getDisc().throwTo(20, 10);
    node.getDisc().act();

    HashSet<Node> successors = node.getOptions();

    int numMoving = 0;
    int numCatching = 0;
    for (Node n: successors) {
      if (n.getPlayer().getState() == PlayerCatchingState.getInstance()) {
        assertEquals(DiscHeldState.getInstance(), n.getDisc().getState());
        numCatching++;
      }
      if (n.getPlayer().getState() == PlayerMovingState.getInstance()) {
        assertEquals(DiscLandedState.getInstance(), n.getDisc().getState());
        numMoving++;
      }
    }
    assertEquals(1, numCatching);
    assertEquals(5, numMoving);
    assertEquals(6, successors.size());
  }

}
