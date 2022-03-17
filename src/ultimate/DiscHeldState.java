package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class DiscHeldState implements DiscState {
  private static DiscHeldState theInstance;

  public static DiscState getInstance() {
    if (theInstance == null) {
      theInstance = new DiscHeldState();
    }
    return theInstance;
  }

  public void act(Disc disc) {
    // do nothing
  }

  public String toString() {
    return "HELD";
  }

  public long getKey() {
    return 1;
  }
}
