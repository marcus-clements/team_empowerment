package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class DiscFlyingState implements DiscState {
  private static DiscFlyingState theInstance;

  public static DiscState getInstance() {
    if (theInstance == null) {
      theInstance = new DiscFlyingState();
    }
    return theInstance;
  }

  public void act(Disc disc) {
    disc.fly();
  }

  public String toString() {
    return "FLYING";
  }

  public long getKey() {
    return 3;
  }
}
