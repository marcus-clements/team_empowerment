package ultimate;

/**
 * Created by mc on 10/02/2016.
 */
public class ScenarioOppATwoTeamsOfTwoPlayersSpread extends Scenario {
  public ScenarioOppATwoTeamsOfTwoPlayersSpread() {
    super();
    description = "Two players on each team. Lookahead 5 means only pass to team members";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Pitch.setMaxThrowDistance(100);
      Pitch.setThrowRange(1);
      Pitch.setDiscVelocity(10);

      Player p0 = new Player(0,0,0,0);
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,15,11,0));

      pitch.addPlayer(new Player(2,3,1,1));
      pitch.addPlayer(new Player(3,17,9,1));
    }
  }
}
