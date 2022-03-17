package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public class PlayerCatchingState implements PlayerState {
  private static PlayerCatchingState theInstance;

  public static PlayerCatchingState getInstance() {
    if (theInstance == null) {
      theInstance = new PlayerCatchingState();
    }
    return theInstance;
  }

  public void act(Player player) {
    //
  }

  public String toString() {
    return "CATCHING";
  }

  public long getKey() {
    return 1;
  }
}
