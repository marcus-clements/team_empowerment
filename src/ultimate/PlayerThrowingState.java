package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class PlayerThrowingState implements PlayerState {
  private static PlayerThrowingState theInstance;

  public static PlayerThrowingState getInstance() {
    if (theInstance == null) {
      theInstance = new PlayerThrowingState();
    }
    return theInstance;
  }
  public void act(Player player) {

  }
  public String toString() {
    return "THROWING";
  }

  public long getKey() {
    return 3;
  }
}
