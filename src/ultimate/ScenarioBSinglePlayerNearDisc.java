package ultimate;

/**
 * Experimental ultimate.Scenario A.
 *
 * Created by mc on 28/01/2016.
 */
public class ScenarioBSinglePlayerNearDisc extends Scenario {
  public ScenarioBSinglePlayerNearDisc() {
    super();
    description = "Single player in the corner. Single disc close by.";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Pitch.setThrowRange(0);
      pitch.addPlayer(new Player(0, 0, 0));
      pitch.setDisc(new Disc(2,2));
    }
  }
}
