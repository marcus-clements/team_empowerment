import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ultimate.Disc;
import ultimate.Node;
import ultimate.Pitch;
import ultimate.Player;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;


import static org.junit.Assert.assertEquals;

/**
 * Created by mc on 29/01/2016.
*/
public class NodeTest {
  @Before
  public void setUp() throws Exception {
    Pitch.setHeight(20);
    Pitch.setWidth(20);
    Pitch.setDiscVelocity(3);
  }

  public static Node getNode(int px, int py, int dx, int dy) {
    return new Node(
            new Player(0, px, py),
            new Disc(dx, dy),
            new ArrayList<>(),
            0,
            null);
  }

  public static Map<String, Integer> mapEndStates(HashSet<Node> endStates, int playerNum) {
    Map<String, Integer> states = new HashMap<>();

    for (Node n: endStates) {
      String key = n.getPlayer(playerNum).getState().toString();
      int count = states.containsKey(key) ? states.get(key) : 0;
      states.put(key, count + 1);

      key = n.getDisc().getState().toString();
      count = states.containsKey(key) ? states.get(key) : 0;
      states.put(key, count + 1);
    }
    states.put("all", endStates.size());
    return states;
  }

//  @Test
//  public void testKeyGen() throws Exception {
//    Node n = getNode(1,2,3,4);
//    long k = n.generateKey();
//    /*
//                 0          step
//                00          disc state
//               300          disc x
//             40000          disc y
//           0000000          disc dest X
//         000000000          disc dest y
//       00000000000          player state
//     1000000000000          player x
//   200000000000000          player y
//      */
//    assertEquals(201000000040300L, n.generateKey());
//  }
  
  @Test
  public void testToString() throws Exception {
    Node node = getNode(10, 10, 0, 0);
    assertEquals("S0 D(0,0,LANDED) P0(10,10,MOVING)", node.toString());
  }

  @Test
  public void testGenerateSuccessors1StepsMiddleOfPitch() throws Exception {
    Pitch.setEmpowermentLookahead(1);
    Node node = getNode(10, 10, 0, 0);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player long way from disc, 1 step lookahead so can move in one of four directions
    // or stay still
    assertEquals(5, endStates.size());
  }

  @Test
  public void testGenerateSuccessors1StepsMiddleOfPitchByDisc() throws Exception {
    Pitch.setEmpowermentLookahead(1);
    Node node = getNode(10, 10, 11, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player is next to disc
    assertEquals(5, endStates.size());
  }

  @Test
  public void testGenerateSuccessors1StepAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(1);
    Node node = getNode(10, 10, 10, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player has 5 move/wait options
    // 1 option to pick up the disc
    assertEquals(6, endStates.size());
  }

  @Test
  public void testGenerateSuccessors2StepAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(2);
    Node node = getNode(10, 10, 10, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player can reach 13 end states by moving
    // One option to wait, then pick up disc (Catching state)
    assertEquals(13 + 1 , endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    Node node = getNode(10, 10, 10, 10);

    //Player can reach 25 states by moving/waiting
    //Player can move/stay - move/stay - pickup disc (PlayerCatchingState)
    // no throwing because no other players
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    Map<String, Integer> states = mapEndStates(endStates, 0);

    assertEquals(1, (int) states.get(PlayerCatchingState.getInstance().toString()));
    assertEquals(25, (int) states.get(PlayerMovingState.getInstance().toString()));

    assertEquals(1 + 25, (int) states.get("all"));
  }

  @Test
  public void testGenerateSuccessors2StepsMiddleOfPitchByDisc() throws Exception {
    Pitch.setEmpowermentLookahead(2);

    Node node = getNode(10, 10, 11, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player has 13 move/stay options
    // And one option to move then pickup disc
    assertEquals(14, endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsCornerOfPitchByDisc() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    Node node = getNode(0, 0, 1, 0);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    // Player has 15 move/stay options
    // move / move / move / pickup
    assertEquals(15 + 1 , endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsMiddleOfPitchByDisc() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    Node node = getNode(10, 10, 11, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    // Player has 41 move/stay options
    // move / move / move / pickup

    assertEquals(41 + 1, endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepsMiddleOfPitchAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    Node node = getNode(10, 10, 10, 10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player has 25 move / stay options
    // move / move / pickup
    assertEquals(25 + 1 , endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepsMiddleOfPitch() throws Exception {
    Pitch.setEmpowermentLookahead(3);

    // middle of the pitch
    Node node = getNode(10, 10, 0, 0);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(25, endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepsCornerOfPitch1() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    // corner
    Node node = getNode(0,0,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(10, endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepsCornerOfPitch2() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    // corner
    Node node = getNode(19,19,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(10, endStates.size());
  }

  @Test
  public void testGenerateSuccessors3StepsSideOfPitch() throws Exception {
    Pitch.setEmpowermentLookahead(3);
    // corner
    Node node = getNode(0,10,0,0);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(16, endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsMiddleOfPitch() throws Exception {
    Pitch.setEmpowermentLookahead(4);

    // middle of the pitch
    Node node = getNode(10, 10, 0, 0);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(41, endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsCornerOfPitch1() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    // corner
    Node node = getNode(0,0,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(15, endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsCornerOfPitch2() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    // corner
    Node node = getNode(19,19,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);
    assertEquals(15, endStates.size());
  }

  @Test
  public void testGenerateSuccessors4StepsMiddleOfPitchAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(4);
    // corner
    Node node = getNode(10,10,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player can reach 41 states by moving/waiting
    // move/stay - move/stay - move/stay - pickup disc
    assertEquals(41 + 1, endStates.size());
  }

  @Test
  public void testGenerateSuccessors5StepsMiddleOfPitchAtDisc() throws Exception {
    Pitch.setEmpowermentLookahead(5);
    // corner
    Node node = getNode(10,10,10,10);
    HashSet<Node> endStates = new HashSet<>();
    node.generateSuccessors(endStates);

    //Player can reach 61 states by moving/waiting
    // move/stay - move/stay - move/stay - pickup disc

    assertEquals(61 + 1 , endStates.size());
  }
}
