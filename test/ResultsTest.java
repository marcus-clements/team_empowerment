/**
 * Created by mc on 05/04/2017.
 */

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import ultimate.Disc;
import ultimate.DiscHeldState;
import ultimate.DiscThrownState;
import ultimate.Node;
import ultimate.Pitch;
import ultimate.Player;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultsTest {

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

    return NodeOppositionTest.getNode(dx,dy,p0x,p0y,p1x,p1y,p2x,p2y,p3x,p3y);
  }

  @Test
  public void testPassFlightOneStep() throws Exception {
    // For passing a lookahead of three or more is required so that the empowerment calculation can
    // include the larger number of options when the receiving player can catch the disc
    // 1: throw, 2: fly 3: catch
    Pitch.setEmpowermentLookahead(3);
    // Players can throw to anywhere within their max distance
    Pitch.setThrowRange(0);
    // Maximum throw distance for any player.
    Pitch.setMaxThrowDistance(20);
    // Include all team players possible moves in empowerment calculation
    Pitch.getInstance().setIncludeTeamMoves(true);
    // Set up the disc and players - the disc will take one step to reach player 2
    Node node = getNode(0,0, 0,0, 4,4, 2,10, 10, 2);
    Pitch.setDiscVelocity(6);

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
  public void testPassTwoFlightSteps() throws Exception {
    // If the disc takes two steps to reach the destination, a lookahead of four or more is required
    // so that the empowerment calculation can
    // include the larger number of options when the receiving player can catch the disc
    // 1: throw, 2: fly 3: fly 4: catch
    Pitch.setEmpowermentLookahead(4);
    // Players can throw to anywhere within their max distance
    Pitch.setThrowRange(0);
    // Maximum throw distance for any player.
    Pitch.setMaxThrowDistance(20);
    // Include all team players possible moves in empowerment calculation
    Pitch.getInstance().setIncludeTeamMoves(true);
    // Set up the disc and players - the disc will take two steps to reach player 2
    Node node = getNode(0,0, 0,0, 4,4, 2,10, 10, 2);
    Pitch.setDiscVelocity(3);

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
  public void testInterceptionCatch() throws Exception {
    Pitch.setEmpowermentLookahead(2);
    // Players can throw to anywhere within their max distance
    Pitch.setThrowRange(0);
    // Maximum throw distance for any player.
    Pitch.setMaxThrowDistance(20);
    // Include all team players possible moves in empowerment calculation
    Pitch.getInstance().setIncludeTeamMoves(true);
    // Set up the disc and players - the disc will take one step to reach player 3
    Node node = getNode(0,0, 2,2, 10,3, 0,0, 4,4);
    Pitch.setDiscVelocity(6);

    // Player 2 has thrown the disc to player 3, we expect player 0, who is on the disc trajectory
    // to catch the disc
    Player p2 = node.getPlayer(2);
    p2.notifyDisc(node.getDisc());
    p2.throwDisc(4,4);
    node.getDisc().act();

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    // All the best successors should be player 0 catching the disc, player 1 has 5 movement options
    assertEquals(5, bestSuccessors.size());
    for (Node n: bestSuccessors) {
      assertEquals(PlayerCatchingState.getInstance(), n.getPlayer().getState());
    }
  }

  @Test
  public void testInterceptionMoveCatch() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    // Set up the root node with four players and disc to provoke an interception.
    Node node = getNode(0,10, 5,11, 15,15, 0,10, 19, 10);
    // Players can throw to anywhere within their max distance
    Pitch.setThrowRange(0);
    // Maximum throw distance for any player.
    Pitch.setMaxThrowDistance(20);
    // Include all team players possible moves in empowerment calculation
    Pitch.getInstance().setIncludeTeamMoves(true);
    Pitch.setDiscVelocity(3);
    Player p2 = node.getPlayer(2);
    p2.notifyDisc(node.getDisc());
    p2.throwDisc(19,10);

    HashSet<Node> endStates = new HashSet<>();
    ArrayList<Node> bestSuccessors = node.generateSuccessors(endStates);

    // Player 0 should see that moving into the disc flightpath will lead to a catch and
    // max empowerment from throwing
    assertEquals(1, bestSuccessors.size());

  }
}
