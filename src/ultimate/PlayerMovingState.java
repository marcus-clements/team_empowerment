package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class PlayerMovingState implements PlayerState {
  private static PlayerMovingState theInstance;

  public static PlayerMovingState getInstance() {
    if (theInstance == null) {
      theInstance = new PlayerMovingState();
    }
    return theInstance;
  }

  public void act(Player player) {

  }

  public String toString() {
    return "MOVING";
  }

  public long getKey() {
    return 0;
  }
}
