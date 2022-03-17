package ultimate;

public class ScenarioCTwoPlayers extends Scenario {
  public ScenarioCTwoPlayers() {
    super();
    description = "Two players, long throws, look ahead 3";
  }

  @Override
  public void initialise(Pitch pitch) {
    {
      Pitch.setEmpowermentLookahead(3);
      Pitch.setDiscVelocity(100);
      Pitch.setThrowRange(0);
      Pitch.setIncludeTeamMoves(true);

      Player p0 = new Player(0,0,0);
      pitch.addPlayer(p0);
      pitch.addPlayer(new Player(1,13,12));
      Disc disc = new Disc(p0.getX(), p0.getY());
      pitch.setDisc(disc);
    }
  }
}
