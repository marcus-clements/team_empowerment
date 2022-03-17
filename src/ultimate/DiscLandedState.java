package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class DiscLandedState implements DiscState {
  private static DiscLandedState theInstance;

  public static DiscState getInstance() {
    if (theInstance == null) {
      theInstance = new DiscLandedState();
    }
    return theInstance;
  }

  public long getKey() {
    return 0;
  }

  public void act(Disc disc) {
    // do nothing
  }

  public String toString() {
    return "LANDED";
  }
}
