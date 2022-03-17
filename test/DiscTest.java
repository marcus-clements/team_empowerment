import org.junit.Before;
import org.junit.Test;

import ultimate.Disc;
import ultimate.DiscFlyingState;
import ultimate.DiscHeldState;
import ultimate.DiscLandedState;
import ultimate.DiscThrownState;
import ultimate.Pitch;
import ultimate.Player;

import static org.junit.Assert.*;

/**
 * Created by mc on 05/02/2016.
 */
public class DiscTest {
  @Before
  public void setUp() throws Exception {
    Pitch.setDiscVelocity(3);
  }

  @Test
  public void testToString() throws Exception {
    Disc disc = new Disc(5,5);
    Pitch.setDiscVelocity(100);
    assertEquals("D(5,5,LANDED)", disc.toString());
    disc.setState(DiscHeldState.getInstance());
    assertEquals("D(5,5,HELD)", disc.toString());
    disc.throwTo(9,8);
    assertEquals("D(5,5,THROWN,9,8,9,8)", disc.toString());
    disc.setState(DiscFlyingState.getInstance());
    assertEquals("D(5,5,FLYING,9,8,9,8)", disc.toString());

  }

  @Test
  public void testThrowTo() throws Exception {
    Disc disc = new Disc(5,5);
    disc.throwTo(0, 0);
    assertSame(DiscThrownState.getInstance(), disc.getState());
    assertEquals(5, disc.getX());
    assertEquals(5, disc.getY());
  }

  @Test
  public void testFly() throws Exception {
    Disc disc = new Disc(5,5);
    disc.throwTo(0, 0);
    assertSame(DiscThrownState.getInstance(), disc.getState());
    disc.act();
    assertSame(DiscFlyingState.getInstance(), disc.getState());
    assertEquals(5, disc.getX());
    assertEquals(5, disc.getY());
  }

  @Test
  public void testLand() throws Exception {
    Disc disc = new Disc(6,4);
    Pitch.setDiscVelocity(100);
    disc.throwTo(10, 10);
    disc.act();
    disc.act();
    disc.act();
    assertSame(DiscLandedState.getInstance(), disc.getState());
    assertEquals(10, disc.getX());
    assertEquals(10, disc.getY());
  }

  @Test
  public void testCatch() throws Exception {
    Disc disc = new Disc(6,4);
    Pitch.setDiscVelocity(100);
    disc.throwTo(10, 10);
    disc.act();
    disc.caught(new Player(0,10,10));
    disc.act();
    assertSame(DiscHeldState.getInstance(), disc.getState());
    assertEquals(10, disc.getX());
    assertEquals(10, disc.getY());
  }

  /**
   * At a distance of 6 the disc should be caught after two steps
   *
   * @throws Exception
   */
  @Test
  public void testTwoStepFlight() throws Exception {
    Disc disc = new Disc(0,0);
    disc.throwTo(4,4);
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(2, disc.getNextX());
    assertEquals(2, disc.getNextY());
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(4, disc.getNextX());
    assertEquals(4, disc.getNextY());
    disc.act();
    assertEquals(DiscLandedState.getInstance().toString(), disc.getState().toString());
    assertEquals(4, disc.getX());
    assertEquals(4, disc.getY());
  }

  /**
   * With the standard velocity of 3 the disc should travel 3 diagonal squares each hop.
   *
   * @throws Exception
   */
  @Test
  public void testLongThrowPosXPosY() throws Exception {
    Disc disc = new Disc(0,0);
    disc.throwTo(6,6);
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(2, disc.getNextX());
    assertEquals(2, disc.getNextY());
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(4, disc.getNextX());
    assertEquals(4, disc.getNextY());
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(6, disc.getNextX());
    assertEquals(6, disc.getNextY());
    disc.act();
    assertEquals(DiscLandedState.getInstance().toString(), disc.getState().toString());
    assertEquals(6, disc.getX());
    assertEquals(6, disc.getY());
  }

  @Test
  public void testLongThrowNegXNegY() throws Exception {
    Disc disc = new Disc(19,19);
    disc.throwTo(13,13);
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(17, disc.getNextX());
    assertEquals(17, disc.getNextY());
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(15, disc.getNextX());
    assertEquals(15, disc.getNextY());
    disc.act();
    assertEquals(DiscFlyingState.getInstance().toString(), disc.getState().toString());
    assertEquals(13, disc.getNextX());
    assertEquals(13, disc.getNextY());
    disc.act();
    assertEquals(DiscLandedState.getInstance().toString(), disc.getState().toString());
    assertEquals(13, disc.getX());
    assertEquals(13, disc.getY());
  }
}