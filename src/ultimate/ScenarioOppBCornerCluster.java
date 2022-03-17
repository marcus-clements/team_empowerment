package ultimate;

/**
 * Created by mc on 10/02/2016.
 */
public class ScenarioOppBCornerCluster extends Scenario {
  public ScenarioOppBCornerCluster() {
    super();
    description = "Two players on each team. All players clustered in the corner. 5 lookahead";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(5);
      Player p0 = new Player(0,0,0,0);
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,0,1,0));

      pitch.addPlayer(new Player(2,1,0,1));
      pitch.addPlayer(new Player(3,1,1,1));
    }
  }
}
