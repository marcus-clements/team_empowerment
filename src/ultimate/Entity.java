package ultimate;

import java.util.Objects;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Base class for disc and players.
 *
 * Created by mc on 25/01/2016.
 */
public class Entity {
  protected int x;
  protected int y;

  public Entity() {
    x = current().nextInt(0, Pitch.getWidth());
    y = current().nextInt(0, Pitch.getHeight());
  }

  public Entity(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Called each time slice.
   * Subclasses should implement this method and set x and y if the position of the entity changes.
   */
  public void act() throws GameException {
    // Overridable
  }

  public String toString() {
    return "" + x + "," + y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x,y);
  }

  @Override
  public boolean equals(Object obj) {
    Entity comp = (Entity) obj;
    return comp.getX() == x &&
            comp.getY() == y;
  }
}
