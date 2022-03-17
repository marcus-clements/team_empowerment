package ultimate;

/**
 * Created by mc on 28/02/2016.
 */
public class ScenarioOppESixPLayers extends Scenario {
  public ScenarioOppESixPLayers() {
    super();
    description = "Two players on each team. Lookahead 5 means only pass to team members";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Player p0 = new Player(0,5,4,0);
      Disc disc = new Disc(p0.getX(), p0.getY());
      p0.setState(PlayerHoldingState.getInstance());
      disc.setState(DiscHeldState.getInstance());
      pitch.setDisc(disc);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,19,17,0));
      pitch.addPlayer(new Player(2,10,4,0));
      pitch.addPlayer(new Player(3,11,4,1));
      pitch.addPlayer(new Player(4,7,4,1));
      pitch.addPlayer(new Player(5,8,8,1));
    }
  }
}
