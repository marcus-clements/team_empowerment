import org.junit.Test;

import java.util.ArrayList;

import ultimate.Disc;
import ultimate.DiscHeldState;
import ultimate.DiscThrownState;
import ultimate.Player;
import ultimate.Pitch;
import ultimate.PlayerCatchingState;
import ultimate.PlayerHoldingState;
import ultimate.PlayerMovingState;
import ultimate.PlayerThrowingState;

import static org.junit.Assert.*;

/**
 * Created by mc on 05/02/2016.
 */
public class PlayerTest {

  @Test
  public void testToString() throws Exception {
    Player p = new Player(8,4,7);
    assertEquals("P8(4,7,MOVING)", p.toString());
    p.setState(PlayerCatchingState.getInstance());
    assertEquals("P8(4,7,CATCHING)", p.toString());
    p.setState(PlayerHoldingState.getInstance());
    assertEquals("P8(4,7,HOLDING)", p.toString());
    p.setState(PlayerThrowingState.getInstance());
    assertEquals("P8(4,7,THROWING)", p.toString());

  }

  @Test
  public void testOnDiscPathHorizontalTrue() throws Exception {
    Disc disc = new Disc(0,5);
    Pitch.setDiscVelocity(100);
    disc.throwTo(15,5);
    Player player = new Player(0, 10, 5);
    assertTrue(player.onDiscPath(disc));
  }

  @Test
  public void testOnDiscPathHorizontalFalse() throws Exception {
    Disc disc = new Disc(0,5);
    disc.throwTo(15,5);
    Player player = new Player(0, 10, 4);
    assertFalse(player.onDiscPath(disc));
    player = new Player(0, 10, 6);
    assertFalse(player.onDiscPath(disc));
  }

  @Test
  public void testOnDiscPath45True() throws Exception {
    Disc disc = new Disc(0,0);
    disc.throwTo(10,10);
    Player player = new Player(0,0,0);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,1,0);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,0,1);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,1,1);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,2,1);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,1,2);
    assertTrue(player.onDiscPath(disc));
    player = new Player(0,2,2);
    assertTrue(player.onDiscPath(disc));
  }

  @Test
  public void testOnDiscPath45False() throws Exception {
    Disc disc = new Disc(0,0);
    disc.throwTo(10,10);
    Player player = new Player(0,2,0);
    assertFalse(player.onDiscPath(disc));
    player = new Player(0,2,0);
    assertFalse(player.onDiscPath(disc));
    player = new Player(0,0,2);
    assertFalse(player.onDiscPath(disc));
    player = new Player(0,3,1);
    assertFalse(player.onDiscPath(disc));
    player = new Player(0,1,3);
    assertFalse(player.onDiscPath(disc));
  }

  @Test
  public void testPickupAndThrowMulti() throws Exception {
    Disc disc = new Disc(10,10);
    Player player0 = new Player(0, 1, 1, 0);
    player0.notifyDisc(disc);
    Player player1 = new Player(0, 5, 1, 0);
    player1.notifyDisc(disc);
    Player player2 = new Player(0, 10, 1, 1);
    player2.notifyDisc(disc);
    Player player3 = new Player(0, 10, 10, 1);
    player3.notifyDisc(disc);
    ArrayList<Player> players = new ArrayList<>();
    players.add(player0);
    players.add(player1);
    players.add(player2);
    players.add(player3);

    // Disc not held yet, p3 can pickup
    Player lastThrower = null;
    assertFalse(player0.canFetchDisc(lastThrower));
    assertTrue(player3.canFetchDisc(lastThrower));

    // p3 cant pickup if he was the last thrower
    lastThrower = player3;
    assertFalse(player3.canFetchDisc(lastThrower));

    // p3 cant pickup if the last thrower was on the same team
    lastThrower = player2;
    assertFalse(player3.canFetchDisc(lastThrower));

    lastThrower = null;
    assertFalse(player3.canThrowDisc(lastThrower));

    player3.catchDisc();
    assertEquals(DiscHeldState.getInstance(), disc.getState());
    assertEquals(PlayerCatchingState.getInstance(), player3.getState());

    assertTrue(player3.canThrowDisc(lastThrower));
    player3.throwDisc(10,1);
    assertEquals(DiscThrownState.getInstance(), disc.getState());
    assertEquals(PlayerThrowingState.getInstance(), player3.getState());
  }

}