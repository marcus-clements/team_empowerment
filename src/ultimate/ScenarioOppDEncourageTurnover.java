package ultimate;

/**
 * Created by mc on 10/02/2016.
 */
public class ScenarioOppDEncourageTurnover extends Scenario {
  public ScenarioOppDEncourageTurnover() {
    super();
    description = "Two players on each team. Lookahead 5. Players arranged to encourage turnover";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Pitch.setWidth(20);
      Pitch.setHeight(20);
      Player p0 = new Player(0,0,10,0);
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,19,10,0));

      pitch.addPlayer(new Player(2,4,9,1));
      pitch.addPlayer(new Player(3,14,14,1));
    }
  }
}
