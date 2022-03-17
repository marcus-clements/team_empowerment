package ultimate;

/**
 * Created by mc on 10/02/2016.
 */
public class ScenarioOppCCentralCluster4Lookahead extends Scenario {
  public ScenarioOppCCentralCluster4Lookahead() {
    super();
    description = "Two players on each team. Clustered in centre.";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Player p0 = new Player(0,9,9,0);
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,9,10,0));

      pitch.addPlayer(new Player(2,10,9,1));
      pitch.addPlayer(new Player(3,10,10,1));
    }
  }
}
