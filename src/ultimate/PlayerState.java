package ultimate;

/**
 * Created by mc on 05/02/2016.
 */
public interface PlayerState {
  void act(Player player);

  long getKey();
}
