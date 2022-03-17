package ultimate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

/**
 * Singleton providing the game environment and playing field. Keeps track of players and disc.
 * Uses GameForm to draw the visualisation.
 * Provides game dimensions and settings.
 *
 * The one and only ultimate.Pitch object is created at the beginning of an experiment. Other classes
 * should call getInstance() to get a reference to the singleton.
 *
 * Created by mc on 25/01/2016.
 */
public class Pitch {
  private static int width = 20;
  private static int height = 20;
  private static int empowermentLookahead = 4;
  private static boolean showHeatmap = false;
  private static Player lastThrower = null;
  // Configurable range around the teammate for throws.
  private static int throwRange = 4;
  private static Pitch theInstance;
  private ArrayList<Player> players;
  private Disc disc;
  private GameForm gameForm;
  private Scenario currentScenario;
  private static boolean includeTeamMoves = false;
  private static int maxThrowDistance = 20;
  private static int discVelocity = 3;

  private Pitch(GameForm gameForm) {
    this.players = new ArrayList<>();
    this.gameForm = gameForm;
  }

  private Pitch() {
    this.players = new ArrayList<>();
  }

  /**
   * Create a new pitch, called once at the start of an experiment.
   *
   * @param gameForm The one and only GameForm UI.
   *
   * @return The one and only instance of ultimate.Pitch.
   */
  public static Pitch create(GameForm gameForm) {
    theInstance = new Pitch(gameForm);
    return theInstance;
  }

  public static boolean getIncludeTeamMoves() {
    return includeTeamMoves;
  }

  public static void setIncludeTeamMoves(boolean teamMoves) {
    includeTeamMoves = teamMoves;
  }

  public static int getThrowRange() {
    return throwRange;
  }

  public static void setThrowRange(int throwRange) {
    Pitch.throwRange = throwRange;
  }

  public static int getMaxThrowDistance() {
    return maxThrowDistance;
  }

  public static void setMaxThrowDistance(int maxThrowDistance) {
    Pitch.maxThrowDistance = maxThrowDistance;
  }

  public Disc getDisc() {
    return disc;
  }

  /**
   * Get the one and only instance of ultimate.Pitch.
   *
   * @return
   */
  public static Pitch getInstance() {
    if (theInstance == null) {
      theInstance = new Pitch();
    }
    return theInstance;
  }

  public static int getDiscVelocity() {
    return discVelocity;
  }

  public static void setDiscVelocity(int discVelocity) {
    Pitch.discVelocity = discVelocity;
  }

  public static void setWidth(int width) {
    Pitch.width = width;
  }

  public static void setHeight(int height) {
    Pitch.height = height;
  }

  public static void setEmpowermentLookahead(int empowermentLookahead) {
    Pitch.empowermentLookahead = empowermentLookahead;
  }

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public static int getWidth() {
    return width;
  }

  public static int getHeight() {
    return height;
  }

  public static int getEmpowermentLookahead() {
    return empowermentLookahead;
  }

  public static boolean doShowHeatmap() {
    return showHeatmap;
  }

  public static Player getLastThrower() {
    return lastThrower;
  }

  public static void setLastThrower(Player lastThrower) {
    Pitch.lastThrower = lastThrower;
  }

  public Scenario getCurrentScenario() {
    return currentScenario;
  }

  /**
   * Updates entities on the pitch and draws everything.
   *
   * @throws GameException
   */
  public void update() throws GameException {
    draw();
    // Keep the old disc position so the heat map can redraw it.
    Disc oldDisc = disc.getClone();
    disc.act();
    Main.log(disc.toString());
    for (Player player : getOrderedPlayerList()) {
      // Keep the old player position so the heat map can redraw it.
      Player oldPlayer = player.getClone();
      player.act();
      gameForm.drawHeatMap(player.getHeatMap(), oldPlayer, oldDisc);
      if (player.getState() == PlayerCatchingState.getInstance() && lastThrower != null) {
        int teamWithPossession = lastThrower.getTeam();
        if (player.getTeam() != teamWithPossession) {
          Main.log("TURNOVER!!!!");
        }
      }
    }
  }

  /**
   * Get a list of players ordered according to the requirements of the model for player actions
   * in the game loop.
   *
   * @return
   */
  private ArrayList<Player> getOrderedPlayerList() {
    ArrayList<Player> shuffleList = (ArrayList<Player>) players.clone();
    Collections.shuffle(shuffleList);
    // First see if any players can catch the disc, and find the closest to the disc.
    double distToDisc = width * height;
    Player firstPlayer = shuffleList.get(0);
    for (Player player: players) {
      if (player.canThrowDisc(lastThrower)) {
        distToDisc = 0;
        firstPlayer = player;
        Main.log("First player " + firstPlayer.getPlayerNum());
      }
      if (player.canCatchDisc(lastThrower) && player.onDiscPath(disc) && player.distanceFromDisc() <= distToDisc) {
        distToDisc = player.distanceFromDisc();
        firstPlayer = player;
        Main.log("First player " + firstPlayer.getPlayerNum());
      }
    }
    // Put the closest catcher at the top of the list.
    if (firstPlayer != shuffleList.get(0)) {
      shuffleList.remove(firstPlayer);
      Player swap = shuffleList.get(0);
      shuffleList.set(0, firstPlayer);
      shuffleList.add(swap);
    }
    return shuffleList;
  }

  public void setDisc(Disc disc) {
    this.disc = disc;
  }

  /**
   * Draw players and disc.
   */
  public void draw() {
    gameForm.eraseAll();
    gameForm.drawGrid();
    for (Player p : players) {
      gameForm.drawPlayer(p);
    }
    gameForm.drawDisc(disc);
  }

  /**
   * Set up pitch at the beginning of an experiment. Notify players about each other and the disc.
   */
  public void initialise(Scenario currentScenario) {
    this.currentScenario = currentScenario;
    lastThrower = null;
    this.players = new ArrayList<>();
    currentScenario.initialise(this);

    // Notify players about each other and disc
    for (Player observer : players) {
      for (Player subject : players) {
        if (subject != observer) {
          observer.notifyPlayer(subject);
        }
      }
      observer.notifyDisc(disc);
    }

    gameForm.initialise();
    draw();
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

  /**
   * Checks if there is an entity on the vector defined by the parameters.
   *
   * @param location
   * @param startX
   * @param startY
   * @param endX
   * @param endY
   * @return
   */
  public static boolean entityOnVector(Entity location, int startX, int startY, int endX, int endY) {
    int ch = 20;
    int cw = 20;
    Rectangle rect = new Rectangle(location.getX() * cw, location.getY() * cw, cw, ch);
    double x1 = startX * cw + cw / 2;
    double y1 = startY * ch + ch / 2;
    double x2 = endX * cw + cw / 2;
    double y2 = endY * ch + ch / 2;
    return rect.intersectsLine(x1, y1, x2, y2);
  }
}
