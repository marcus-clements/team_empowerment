package ultimate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 * Provides player State and movement.
 *
 * Created by mc on 25/01/2016.
 */
public class Player extends Entity implements Notify {
  private Disc disc;
  private ArrayList<Player> players = new ArrayList<>();
  private int playerNum;
  private PlayerState state = PlayerMovingState.getInstance();
  private int team;
  private HashMap<Entity, Integer> heatMap = new HashMap<>();

  public Player() {
    super();
  }

  public Player(int playerNum, int x, int y) {
    super(x, y);
    this.playerNum = playerNum;
  }

  public Player(int playerNum, int x, int y, int team) {
    super(x, y);
    this.playerNum = playerNum;
    this.team = team;
  }

  public String toString() {
    return "P" + playerNum + "(" + x + "," + y + "," + state + ")";
  }

  @Override
  public int hashCode() {
    return Objects.hash(x,y,state);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    Player comp = (Player) obj;
    return comp.getX() == x &&
            comp.getY() == y &&
            comp.getState() == state;
  }

  public int getTeam() {
    return team;
  }

  public int getPlayerNum() {
    return playerNum;
  }

  public void setPlayerNum(int playerNum) {
    this.playerNum = playerNum;
  }

  public PlayerState getState() {
    return state;
  }

  public void setState(PlayerState state) {
    this.state = state;
  }

  public Player getClone() {
    Player player = new Player(playerNum, x, y, team);
    player.setState(state);
    return player;
  }

  public HashMap<Entity, Integer> getHeatMap() {
    return heatMap;
  }

  /**
   * Called during each iteration of the game loop.
   *
   * Calculates empowerment and makes action choice based on the best successor state.
   *
   * @throws GameException
   */
  @Override
  public void act() throws GameException {
    // When catching the player has no options.
    if (state == PlayerCatchingState.getInstance()) {
      //state = PlayerHoldingState.getInstance();
      state = PlayerHoldingState.getInstance();
      Main.log(toString());
      //return;
    }
    HashSet<Node> endStates = new HashSet<>();
    Node root = new Node(this, disc, players, 0, Pitch.getLastThrower());
    ArrayList<Node> bestSuccessors = root.generateSuccessors(endStates);
    generateHeatMap(root);
    Main.log("Num end states: " + endStates.size());
    if (bestSuccessors.size() < 1) {
      throw new GameException("No Successors aaaaaaaargh!!!");
    }
    Random randomChoice = new Random();

    // Choose a pseudo-random successor state from the states with the highest empowerment.
    Node bestSuccessor = bestSuccessors.get(randomChoice.nextInt(bestSuccessors.size()));
    state = bestSuccessor.getPlayer().getState();

    // Best successor is throwing
    if (state == PlayerThrowingState.getInstance()) {
      if (canThrowDisc(Pitch.getLastThrower())) {
        throw new GameException("Successor says throw but I cant aaaaaargh");
      }
      Pitch.setLastThrower(this);
      disc.throwTo(bestSuccessor.getDisc().getDestX(), bestSuccessor.getDisc().getDestY());
    }

    // Best successor is catching
    else if (state == PlayerCatchingState.getInstance()) {
      if (!canCatchDisc(Pitch.getLastThrower()) && ! canFetchDisc(Pitch.getLastThrower())) {
        throw new GameException("Successor says catch but I cant aaaaaargh");
      }
      catchDisc();
    }

    // Best successor is moving
    else if (state == PlayerMovingState.getInstance()) {
      if (x == bestSuccessor.getPlayer().getX() &&
            y == bestSuccessor.getPlayer().getY()) {
        Main.log("P" + playerNum + " moving nowhere");
      }
      else {
        x = bestSuccessor.getPlayer().getX();
        y = bestSuccessor.getPlayer().getY();
      }
    }
    Main.log(toString());
  }

  @Override
  public void notifyDisc(Disc disc) {
    this.disc = disc;
  }

  @Override
  public void notifyPlayer(Player player) {
    if (!players.contains(player)) {
      players.add(player);
    }
  }

  public void catchDisc() {
    this.setState(PlayerCatchingState.getInstance());
    disc.caught(this);
  }

  public void throwDisc(int destX, int destY) {
    this.setState(PlayerThrowingState.getInstance());
    disc.throwTo(destX, destY);
  }

  private void moveUtility() {
    moveTowards(disc);
  }

  private void moveTowards(Entity entity) {
    int dX = entity.getX() - x;
    int dY = entity.getY() - y;
    if (Math.abs(dX) > Math.abs(dY)) {
      x += (int) Math.signum(dX);
    } else {
      y += (int) Math.signum(dY);
    }
  }

  public boolean onDiscPath(Disc checkDisc) {
    return Pitch.entityOnVector(this,
            checkDisc.getX(),
            checkDisc.getY(),
            checkDisc.getNextX(),
            checkDisc.getNextY());
  }

  public double distanceFromDisc() {
    return Math.sqrt(Math.pow((x - disc.getX()), 2) + Math.pow((y - disc.getY()), 2));
  }

  public boolean canCatchDisc(Player lastThrower) {
    // Player cannot catch disc if thrown by himself.
    if (lastThrower != null && playerNum == lastThrower.getPlayerNum()) {
      return false;
    }
    return (disc.getState() == DiscFlyingState.getInstance() && onDiscPath(disc));
  }

  public boolean canThrowDisc(Player lastThrower) {
    if (lastThrower != null && playerNum == lastThrower.getPlayerNum()) {
      return false;
    }
    return x == disc.getX() && y == disc.getY() &&
            disc.getState() == DiscHeldState.getInstance() &&
              (state == PlayerHoldingState.getInstance() ||
              state == PlayerCatchingState.getInstance());
  }

  public boolean canMove() {
    return state == PlayerMovingState.getInstance() ||
            state == PlayerThrowingState.getInstance();
  }

  public boolean canFetchDisc(Player lastThrower) {
    if (lastThrower != null && team == lastThrower.getTeam()) {
      return false;
    }
    return disc.getState() == DiscLandedState.getInstance() && x == disc.getX() && y == disc.getY();
  }

  public void generateHeatMap(Node root) {
    heatMap = new HashMap<>();
    root.heatMap(heatMap);
  }
}
