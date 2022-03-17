package ultimate;

/**
 * Experimental ultimate.Scenario B.
 *
 * Created by mc on 28/01/2016.
 */
public class ScenarioASinglePlayerFarFromDisc extends Scenario {
  public ScenarioASinglePlayerFarFromDisc() {
    super();
    description = "Single static disc randomly positioned. Single player randomly positioned.";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      pitch.addPlayer(new Player(0, 0, 0));
      pitch.setDisc(new Disc(15,15));
    }
  }
}
