package ultimate;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * The app.
 */
public class Main {
  static GameForm gameForm;
  private static Pitch pitch;

  public static void main(String[] args) {
    ArrayList<Scenario> scenarios = new ArrayList<>();
    scenarios.add(new ScenarioASinglePlayerFarFromDisc());
    scenarios.add(new ScenarioBSinglePlayerNearDisc());
    scenarios.add(new ScenarioCTwoPlayers());
    scenarios.add(new ScenarioDTwoPlayersShortThrow());
    scenarios.add(new ScenarioEThreePlayers());
    scenarios.add(new ScenarioOppATwoTeamsOfTwoPlayersSpread());
    scenarios.add(new ScenarioOppBCornerCluster());
    scenarios.add(new ScenarioOppCCentralCluster4Lookahead());
    scenarios.add(new ScenarioOppDEncourageTurnover());
    scenarios.add(new ScenarioOppESixPLayers());

    gameForm = new GameForm(scenarios);

    pitch = Pitch.create(gameForm);
    pitch.initialise(scenarios.get(2));

    long time;
    try {
      int n = 0;
      while (n < 1000) {
        time = System.currentTimeMillis();
        if (gameForm.isSimRunning()) {
          pitch.update();
          n++;
          gameForm.displayStep(n);
        }
        long lag = time - System.currentTimeMillis();
        lag = lag < 500 ? 500 : lag;
        log("\n***************** Lag: " + lag);
        if (lag > 0) {
          sleep(lag);
        }
      }
    } catch (Exception e) {
      log(e.toString());
      log(e.getMessage());
      e.printStackTrace();
    }
  }

  public static void log(String msg) {
    System.out.println(msg);
  }
}
