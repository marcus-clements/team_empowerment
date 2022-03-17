package ultimate;

/**
 * Created by mc on 08/02/2016.
 */
public class ScenarioEThreePlayers extends Scenario {
  public ScenarioEThreePlayers() {
    super();
    description = "Three players, player 0 with disc";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Player p0 = new Player(0,0,0);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,19,19));
      pitch.addPlayer(new Player(2,9,8));
      Disc disc = new Disc(10, 10);
      pitch.setDisc(disc);
    }
  }
}
