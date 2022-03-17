package ultimate;

public class ScenarioDTwoPlayersShortThrow extends Scenario {
  public ScenarioDTwoPlayersShortThrow() {
    super();
    description = "Two players, throw dist 8, lookahead 4";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(4);
      Pitch.setDiscVelocity(8);
      Pitch.setThrowRange(0);

      Player p0 = new Player(0,0,0);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,13,12));
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
    }
  }
}
