package ultimate;

/**
 * Provides an experimental scenario. Sets up players and disc location and movement.
 *
 * Created by mc on 25/01/2016.
 */
public abstract class Scenario {
  protected String name;
  protected String description;
  protected Pitch pitch;

  public Scenario() {
    name = getClass().getName();
  }

  public abstract void initialise(Pitch pitch);
}
