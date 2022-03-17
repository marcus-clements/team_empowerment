package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class PlayerHoldingState implements PlayerState {
  private static PlayerHoldingState theInstance;

  public static PlayerHoldingState getInstance() {
    if (theInstance == null) {
      theInstance = new PlayerHoldingState();
    }
    return theInstance;
  }

  public void act(Player player) {

  }

  public String toString() {
    return "HOLDING";
  }

  public long getKey() {
    return 2;
  }
}
