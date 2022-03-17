package ultimate;

/**
 * ultimate.Notify interface for subjects to call on observers.
 *
 * Created by mc on 25/01/2016.
 */
public interface Notify {
  void notifyDisc(Disc disc);
  void notifyPlayer(Player player);
}