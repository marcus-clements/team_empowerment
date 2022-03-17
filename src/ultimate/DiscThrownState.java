package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class DiscThrownState implements DiscState {
  private static DiscThrownState theInstance;

  public static DiscState getInstance() {
    if (theInstance == null) {
      theInstance = new DiscThrownState();
    }
    return theInstance;
  }

  @Override
  public void act(Disc disc) {
    disc.setState(DiscFlyingState.getInstance());
  }

  public String toString() {
    return "THROWN";
  }

  public long getKey() {
    return 2;
  }
}
