import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ultimate.Disc;
import ultimate.DiscHeldState;
import ultimate.Node;
import ultimate.Pitch;
import ultimate.Player;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mc on 08/02/2016.
 */
public class NodeOppositionTest {

  @Before
  public void setUp() throws Exception {
    Pitch.setHeight(20);
    Pitch.setWidth(20);
    Pitch.setDiscVelocity(3);
  }

  public static Node getNode(int dx, int dy,
                             int p0x, int p0y,
                             int p1x, int p1y,
                             int p2x, int p2y,
                             int p3x, int p3y) {
    Player p0 = new Player(0, p0x, p0y, 0);
    Player p1 = new Player(1, p1x, p1y, 0);
    Player p2 = new Player(2, p2x, p2y, 1);
    Player p3 = new Player(3, p3x, p3y, 1);
    ArrayList<Player> pls = new ArrayList<>();
    pls.add(p1);
    pls.add(p2);
    pls.add(p3);
    return new Node(p0, new Disc(dx, dy), pls, 0, null);
  }

  @Test
  public void testThrowToTeammate() throws Exception {
    Pitch.setEmpowermentLookahead(6);
    // Throw right across the pitch because only one throw will hit the target
    Node node = getNode(0,0, 0,0, 19,19, 2,10, 10, 2);
    Pitch.setDiscVelocity(100);
    Pitch.setMaxThrowDistance(100);
    Player p0 = node.getPlayer();
    p0.setState(PlayerHoldingState.getInstance());
    Disc d = node.getDisc();
    d.setState(DiscHeldState.getInstance());

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);
    Map<String, Integer> p0states = NodeTest.mapEndStates(endStates, 0);
    Map<String, Integer> p1states = NodeTest.mapEndStates(endStates, 1);

    for (Node n : bestSuccessors) {
      assertEquals(PlayerThrowingState.getInstance(), n.getPlayer().getState());
    }
    assertTrue(p1states.get(PlayerCatchingState.getInstance().toString()) > 0);
  }

  @Test
  public void testCornerCluster() throws Exception {
    Pitch.setEmpowermentLookahead(5);
    Node node = getNode(0, 0, 0, 0, 0, 1, 1, 0, 1, 1);

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    assertEquals(1, bestSuccessors.size());
    for (Node n : endStates) {
      if (n.getPlayer().getPlayerNum() == 0 &&
              n.getPlayer().getState() == PlayerCatchingState.getInstance() &&
              n.getLastThrower() == n.getPlayer(1)) {
        System.out.println("yeah");
      }
    }
  }

  @Test
  public void testInterceptionCatch() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    // Set up so disc is flying across square near player 0
    Node node = getNode(1,1, 5,5, 19,19, 1,1, 6, 6);
    Pitch.setDiscVelocity(100);
    Player p2 = node.getPlayer(2);
    p2.notifyDisc(node.getDisc());
    p2.throwDisc(6,6);
    node.getDisc().act();

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    // Player 0 should see that intercepting and catching the disc gives maximum empowerment
    // from throwing options
    assertEquals(1, bestSuccessors.size());
    assertEquals(PlayerCatchingState.getInstance(), bestSuccessors.get(0).getPlayer().getState());
  }

  @Test
  public void testInterceptionMoveCatch() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    // Set up the root node with four players and disc to provoke an interception.
    Node node = getNode(0,10, 5,11, 15,15, 0,10, 19, 10);
    Pitch.setDiscVelocity(100);
    Player p2 = node.getPlayer(2);
    p2.notifyDisc(node.getDisc());
    p2.throwDisc(19,10);

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    // Player 0 should see that moving into the disc flightpath will lead to a catch and
    // max empowerment from throwing
    assertEquals(1, bestSuccessors.size());
    Node bestSuccessor = bestSuccessors.get(0);
    assertEquals(PlayerMovingState.getInstance(), bestSuccessor.getPlayer().getState());
    assertEquals(5, bestSuccessors.get(0).getPlayer().getX());
    assertEquals(10, bestSuccessors.get(0).getPlayer().getY());
    Node bestStep2Successor = bestSuccessor.getBestSuccessors().get(0);
    assertEquals(PlayerCatchingState.getInstance(), bestStep2Successor.getPlayer().getState());
    Node bestStep3Successor = bestStep2Successor.getBestSuccessors().get(0);
    assertEquals(PlayerThrowingState.getInstance(), bestStep3Successor.getPlayer().getState());
  }

}