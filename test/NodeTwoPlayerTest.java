import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ultimate.Disc;
import ultimate.DiscFlyingState;
import ultimate.DiscHeldState;
import ultimate.DiscLandedState;
import ultimate.DiscThrownState;
import ultimate.Entity;
import ultimate.Node;
import ultimate.Pitch;
import ultimate.Player;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mc on 08/02/2016.
 */
public class NodeTwoPlayerTest {

  @Before
  public void setUp() throws Exception {
    Pitch.setHeight(20);
    Pitch.setWidth(20);
    Pitch.setDiscVelocity(3);
  }

  public static Node getNode(int dx, int dy, int p0x, int p0y, int p1x, int p1y) {
    Player p0 = new Player(0, p0x, p0y);
    Player p1 = new Player(1, p1x, p1y);
    ArrayList<Player> pls = new ArrayList<>();
    pls.add(p1);
    return new Node(p0, new Disc(dx, dy), pls, 0, null);
  }

  @Test
  public void testDiscountPlayer1MovementWhenMoving() throws Exception {
    Pitch.setEmpowermentLookahead(1);
    Node node = getNode(10, 10, 5, 5, 15, 15);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    // only player 0 moves count because p1 can't be affected
    assertEquals(5, endStates.size());
  }

  @Test
  public void testPlayer0PIcksUp() throws Exception {
    Node node = getNode(5,5,5,5,15,15);
    node.getDisc().setState(DiscLandedState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,0);

    assertTrue((int) states.get(PlayerCatchingState.getInstance().toString()) > 0);
  }

  @Test
  public void testPlayer0Throws() throws Exception {
    Node node = getNode(5,5,5,5,15,15);
    node.getDisc().setState(DiscHeldState.getInstance());
    node.getPlayer().setState(PlayerHoldingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,0);

    assertTrue((int) states.get(PlayerThrowingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer0Catches() throws Exception {
    Node node = getNode(15,15,5,5,15,15);
    node.setLastThrower(node.getPlayer(1));
    Pitch.setDiscVelocity(100);
    node.getDisc().throwTo(5,5);
    node.getDisc().setState(DiscFlyingState.getInstance());
    node.getPlayer().setState(PlayerMovingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,0);

    assertTrue((int) states.get(PlayerCatchingState.getInstance().toString()) > 0);
  }

  @Test
  public void testPlayer0MovesPlayer1HoldingOrThrowing() throws Exception {
    Node node = getNode(5, 5, 15, 15, 5, 5);
    node.getPlayer(1).setState(PlayerHoldingState.getInstance());
    node.getDisc().setState(DiscHeldState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,0);

    assertTrue((int) states.get(PlayerMovingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer1PicksUp() throws Exception {
    Node node = getNode(15,15,5,5,15,15);
    node.getDisc().setState(DiscLandedState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,1);

    assertTrue((int) states.get(PlayerCatchingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer1Throws() throws Exception {
    Node node = getNode(15,15,5,5,15,15);
    node.getDisc().setState(DiscHeldState.getInstance());
    node.getPlayer(1).setState(PlayerHoldingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,1);

    assertTrue((int) states.get(PlayerThrowingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer1Catches() throws Exception {
    Node node = getNode(5,5,5,5,15,15);
    Pitch.setDiscVelocity(100);
    node.getDisc().throwTo(15,15);
    node.setLastThrower(node.getPlayer(0));
    node.getDisc().setState(DiscFlyingState.getInstance());
    node.getPlayer().setState(PlayerMovingState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,1);

    assertTrue((int) states.get(PlayerCatchingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer1MovesPlayer0Holding() throws Exception {
    Node node = getNode(5, 5, 5, 5, 15, 15);
    node.getPlayer(0).setState(PlayerHoldingState.getInstance());
    node.getDisc().setState(DiscHeldState.getInstance());

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,1);

    assertTrue((int) states.get(PlayerMovingState.getInstance().toString()) > 1);
  }

  @Test
  public void testPlayer0ThrowsThenMoves() throws Exception {
    Node node = getNode(5, 5, 5, 5, 15, 15);
    node.getPlayer(0).setState(PlayerThrowingState.getInstance());
    node.getDisc().setState(DiscThrownState.getInstance());
    node.setLastThrower(node.getPlayer(0));

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,0);

    assertTrue((int) states.get(PlayerMovingState.getInstance().toString()) > 1);
    boolean noThrowing = true;
    try {
      states.get(PlayerThrowingState.getInstance().toString());
    }
    catch(Exception e) {
      noThrowing = false;
    }
    assertTrue(noThrowing);
  }

  @Test
  public void testPlayer1ThrowsThenMoves() throws Exception {
    Node node = getNode(15, 15, 5, 5, 15, 15);
    node.getPlayer(1).setState(PlayerThrowingState.getInstance());
    node.getDisc().setState(DiscThrownState.getInstance());
    node.setLastThrower(node.getPlayer(1));

    HashSet<Node> successors = node.getOptions();

    Map<String, Integer> states = NodeTest.mapEndStates(successors,1);

    assertTrue((int) states.get(PlayerMovingState.getInstance().toString()) > 1);
    boolean noThrowing = true;
    try {
      states.get(PlayerThrowingState.getInstance().toString());
    }
    catch(Exception e) {
      noThrowing = false;
    }
    assertTrue(noThrowing);
  }

  @Test
  public void TestThrowLocationsLimitedRange() throws Exception {
    Node node = getNode(15, 15, 5, 5, 15, 15);
    Pitch.setThrowRange(4);
    Set<Entity> throwLocations = node.getAllThrowLocations(node.getPlayer(1));
    assertEquals(81, throwLocations.size());
  }

  @Test
  public void test5StepsByDisc() throws Exception {
    Pitch.setEmpowermentLookahead(5);
    Node node = getNode(5, 5, 5, 5, 15, 15);
    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);
    Map<String, Integer> p0states = NodeTest.mapEndStates(endStates,0);
    Map<String, Integer> p1states = NodeTest.mapEndStates(endStates,1);

    // best option looking far ahead should be for P0 to pickup the disc
    assertEquals(1, bestSuccessors.size());
    assertEquals(PlayerCatchingState.getInstance(), bestSuccessors.get(0).getPlayer().getState());
    // p0 pickup - hold - throw - disc fly - p1 catch - p1 hold - p1 throw
  }

  @Test
  public void test5StepsHasDisc() throws Exception {
    // set the shortest lookahead that encourages passing
    // p0 throw - disc fly - p1 catch - p1 hold - p1 throw
    Pitch.setEmpowermentLookahead(5);
    Pitch.setMaxThrowDistance(100);
    Node node = getNode(0, 0, 0, 0, 15, 15);
    Pitch.setDiscVelocity(100);
    HashSet<Node> endStates = new HashSet<>();
    node.getPlayer().setState(PlayerCatchingState.getInstance());
    node.getDisc().setState(DiscHeldState.getInstance());
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);
    Map<String, Integer> p0states = NodeTest.mapEndStates(endStates, 0);
    Map<String, Integer> p1states = NodeTest.mapEndStates(endStates, 1);

    // best option looking far ahead should be for P0 to throw the disc
    assertEquals(PlayerThrowingState.getInstance(), bestSuccessors.get(0).getPlayer().getState());
  }

  @Test
  public void testPlayer1CatchesTwoStepThrow() throws Exception {
    Node node = getNode(0,0,0,0,4,4);

    node.getDisc().throwTo(4,4);
    node.setLastThrower(node.getPlayer(0));
    node.getDisc().setState(DiscFlyingState.getInstance());
    node.getPlayer().setState(PlayerMovingState.getInstance());

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    assertEquals(PlayerCatchingState.getInstance().toString(),
            bestSuccessors.get(0).getBestSuccessors().get(0).getPlayer(1).getState().toString());
  }
}
