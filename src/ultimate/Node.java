package ultimate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * A node in a directed acyclic graph (search tree) of potential states for a player given the range
 * of possible actions.
 *
 * Created by mc on 26/01/2016.
 */
public class Node {
  // ultimate.Player location
  private Player player;
  // ultimate.Disc location
  private Disc disc;
  // How many steps down the tree
  private int step;
  // player 2
  private ArrayList<Player> players;
  //Avoids recalculation of log base 10 of 2.
  private static double log_10_2 = Math.log(2);
  private int numLeafNodes;
  private Player lastThrower;
  private Node parent = null;
  private ArrayList<Node> bestSuccessors = new ArrayList<>();
  private  ArrayList<Node> successors = new ArrayList<>();

  public Node(Player player, Disc disc, ArrayList<Player> players, int step, Player lastThrower) {
    this(player, disc, players, step, lastThrower, null);
  }

  public Node(Player player, Disc disc, ArrayList<Player> players, int step, Player lastThrower, Node parent) {
    this.player = player;
    this.disc = disc;
    this.players = players;
    this.step = step;
    this.lastThrower = lastThrower;
    this.parent = parent;
  }


  public int getNumLeafNodes() {
    return numLeafNodes;
  }

  public Player getPlayer() {
    return player;
  }

  public Player getPlayer(int num) {
    if (num == 0) return player;
    return (players.get(num - 1));
  }

  public Disc getDisc() {
    return disc;
  }

  public Player getLastThrower() {
    return lastThrower;
  }

  public void setLastThrower(Player lastThrower) {
    this.lastThrower = lastThrower;
  }

  public ArrayList<Node> getBestSuccessors() {
    return bestSuccessors;
  }

  /**
   * Returns a string representation of the node State.
   *
   * @return String representation of node State
   */
  @Override
  public String toString() {
    String ret = "S" + step + " " + disc.toString() + " ";
    ret += player.toString();
    for (Player p : players) {
      ret += " " + p.toString();
    }
    return ret;
  }

  @Override
  public int hashCode() {
    return Objects.hash(step, disc, player, players);
  }

  @Override
  public boolean equals(Object obj) {
    Node comp = (Node) obj;
    return comp.getStep() == step && 
            comp.getPlayer().equals(player) &&
            comp.getDisc().equals(disc) &&
            comp.getPlayers().equals(players);
  }

  public int getStep() {
    return step;
  }

  public ArrayList<Player> getPlayers() {
    return players;
  }

  /**
   * Recursive method to generate a set of successors for the node and add to the graph.
   * A HashMap is used for the graph to efficiently maintain uniqueness.
   *
   * @param endStates HashMap of possible end states
   * @return ArrayList of successors with highest empowerment
   */
  public ArrayList<Node> generateSuccessors(HashSet<Node> endStates) {
    // Find all the possible actions.
    HashSet<Node> options = getOptions();
    // If the lookahead has not been reached, find the next generation of successor states.
    int bestLeafCount = 0;
    if (step < Pitch.getEmpowermentLookahead()) {
      HashSet<Node> childLeaves = new HashSet<>();
      for (Node n : options) {
        successors.add(n);
        n.generateSuccessors(childLeaves);
        if (n.getNumLeafNodes() > bestLeafCount) {
          bestSuccessors.clear();
          bestSuccessors.add(n);
          bestLeafCount = n.getNumLeafNodes();
        }
        else if (n.getNumLeafNodes() == bestLeafCount) {
          bestSuccessors.add(n);
        }
      }
      numLeafNodes = childLeaves.size();
      endStates.addAll(childLeaves);
    }
    else {
      endStates.add(this);
    }
    return bestSuccessors;
  }

  /**
   * Returns a set of successors based on the current players action options.
   *
   * The options must be generated for each of the teammates actions if they are admissible in the
   * empowerment calculation
   *
   * @return
   */
  public HashSet<Node> getOptions() {
    HashSet<Node> options = new HashSet<>();
    player.notifyDisc(disc);
    if (player.canMove()) {
      moveOptions(options, player, step + 1);
      for (Node n: options) {
        n.getDisc().act();
      }
    }
    getPlayerOptions(options, player, step + 1);
    if (players.size() > 0) {
      HashSet<Node> otherPlayerOptions = new HashSet<>();
      for (Node n : options) {
        for (Player otherPlayer : players) {
          // only for teammates
          if (otherPlayer.getTeam() == player.getTeam()) {
            otherPlayer.notifyDisc(disc);
            n.getPlayerOptions(otherPlayerOptions, otherPlayer, step + 1);
            // there is a UI setting to include movement options for teammates
            if (n.getPlayer().getState() != PlayerMovingState.getInstance() &&
                    otherPlayer.canMove() &&
                    Pitch.getIncludeTeamMoves()) {
              n.moveOptions(otherPlayerOptions, otherPlayer, step + 1);
            }
          }
        }
      }
      options.addAll(otherPlayerOptions);
    }

    return options;
  }

  /**
   * Generate successors for players based on catch and throw options.
   *
   * @param successors
   * @param checkPlayer
   * @param newStep
   */
  private void getPlayerOptions(HashSet<Node> successors, Player checkPlayer, int newStep) {
    if (checkPlayer.canCatchDisc(lastThrower) || checkPlayer.canFetchDisc(lastThrower)) {
      catchOptions(successors, checkPlayer, newStep);
    }
    if (checkPlayer.canThrowDisc(lastThrower)) {
      throwDiscOptions(successors, checkPlayer, newStep);
    }
  }

  /**
   * Generate successors based on holding the disc.
   *
   * @param successors
   * @param checkPlayer
   * @param newStep
   */
  private void holdOption(HashSet<Node> successors, Player checkPlayer, int newStep) {
    // once a player has caught the disc, they must wait a turn in the holding state.
    Player newPlayer = checkPlayer.getClone();
    newPlayer.setState(PlayerHoldingState.getInstance());
    Disc newDisc = disc.getClone();
    disc.setState(DiscHeldState.getInstance());
    newPlayer.notifyDisc(disc);
    addNode(successors, newPlayer, newDisc, lastThrower, newStep);
  }

   /**
   * @param arg argument
   * @return log base 2 of argument
   */
  public static double log2(double arg) {
    return Math.log(arg) / log_10_2;
  }

  /**
   * Generates possible states after throwing the disc and adds them to the graph.
   * When the player has the disc they may throw it but they may not move.
   *
   * @param successors ArrayList of options
   */
  private void throwDiscOptions(HashSet<Node> successors, Player checkPlayer, int newStep) {
    // If this is a future where the opposition has the disc then it's a turnover
    // and play has stopped so there are no futures.
    if (this.player.getTeam() != checkPlayer.getTeam()) {
      return;
    }
    Set<Entity> throwLocations = getAllThrowLocations(checkPlayer);
    for (Entity e: throwLocations) {
      if (e.getX() == disc.getX() && e.getY() == disc.getY()) {
        continue;
      }
      Disc newDisc = disc.getClone();
      Player newPlayer = checkPlayer.getClone();
      newPlayer.notifyDisc(newDisc);
      newPlayer.throwDisc(e.getX(), e.getY());
      addNode(successors, newPlayer, newDisc, newPlayer, newStep);
    }
  }

  /**
   * Get the list of locations that the player can throw to.
   *
   * @param checkPlayer
   * @return
   */
  public Set<Entity> getAllThrowLocations(Player checkPlayer) {
    Set<Entity> throwLocations = new HashSet<>();
    if (Pitch.getThrowRange() == 0) {
      throwLocations.addAll(getThrowLocations(checkPlayer, null));
    }
    else {
      ArrayList<Player> allPlayers = new ArrayList<>();
      allPlayers.addAll(players);
      allPlayers.add(player);
      for (Player otherPlayer : allPlayers) {
        if (otherPlayer != checkPlayer && otherPlayer.getTeam() == checkPlayer.getTeam()) {
          throwLocations.addAll(getThrowLocations(checkPlayer, otherPlayer));
        }
      }
    }
    return throwLocations;
  }

  /**
   * Find the list of locations optimised by the throw range if set. If the throw range is not zero
   * the teamMate must be supplied.
   *
   * @return
   */
  private Set<Entity> getThrowLocations(Player checkPlayer, Player teamMate) {
    // Move to a legal square avoiding players including this one
    // as we have already handled not moving above.
    Set<Entity> throwLocations = new HashSet<>();
    // Player may throw to configurable range around team mate.
    // There is a setting in the UI for throw range.
    // Initialise with the range covering whole board.
    int minY = 0;
    int minX = 0;
    int maxY = Pitch.getHeight() - 1;
    int maxX = Pitch.getWidth() - 1;
    int throwRange = Pitch.getThrowRange();
    // If as range has been set, set the bounds accordingly, but stay within the pitch for efficiency.
    if (Pitch.getThrowRange() > 0) {
      if (teamMate.getY()-throwRange > minY) {
        minY = teamMate.getY() - throwRange;
      }
      if (teamMate.getY()+throwRange < maxY) {
        maxY = teamMate.getY() + throwRange;
      }
      if (teamMate.getX()-throwRange > minX) {
        minX = teamMate.getX() - throwRange;
      }
      if (teamMate.getX()+throwRange < maxX) {
        maxX = teamMate.getX() + throwRange;
      }
    }
    for (int y=minY; y<=maxY; y++) {
      for (int x=minX; x<=maxX; x++) {
        // Only add locations that are within the max throwing distance
        if (Math.sqrt(Math.pow(checkPlayer.getX() - x, 2) + Math.pow(checkPlayer.getY() - y, 2)) <= Pitch.getMaxThrowDistance()) {
          throwLocations.add(new Entity(x, y));
        }
      }
    }
    return throwLocations;
  }

  /**
   * Generates possible states after a move and adds them to the graph.
   * ultimate.Player movement is restricted to forward, back, left, right - no diagonals.
   *
   * @param successors ArrayList of options.
   */
  private void moveOptions(HashSet<Node> successors, Player checkPlayer, int newStep) {
    // don't move
    stayOption(successors, checkPlayer, newStep);
    Player newP;
    Disc newD;
    // Move to a legal square avoiding players including this one
    // as we have already handled not moving above.
    for (int m=-1; m<2; m+=2) {
      int mx = checkPlayer.getX() + m;
      if (mx >= 0 && mx < Pitch.getWidth() && !playerAt(mx, checkPlayer.getY())) {
        newP = checkPlayer.getClone();
        newD = disc.getClone();
        newP.notifyDisc(disc);
        newP.setState(PlayerMovingState.getInstance());
        newP.setLocation(mx, checkPlayer.getY());
        addNode(successors, newP, newD, lastThrower, newStep);
      }
      int my = checkPlayer.getY() + m;
      if (my >= 0 && my < Pitch.getHeight() && !playerAt(checkPlayer.getX(), my)) {
        newP = checkPlayer.getClone();
        newD = disc.getClone();
        newP.notifyDisc(disc);
        newP.setState(PlayerMovingState.getInstance());
        newP.setLocation(checkPlayer.getX(), my);
        addNode(successors, newP, newD, lastThrower, newStep);
      }
    }
  }

  /**
   * Player can choose not to move.
   *
   * @param successors
   * @param checkPlayer
   * @param newStep
   */
  private void stayOption(HashSet<Node> successors, Player checkPlayer, int newStep) {
    Player newP = checkPlayer.getClone();
    Disc newD = disc.getClone();
    newP.notifyDisc(disc);
    newP.setState(PlayerMovingState.getInstance());
    addNode(successors, newP, newD, lastThrower, newStep);
  }

  /**
   * Checks if there is a player at the supplied location.
   *
   * @param x
   * @param y
   * @return
   */
  private boolean playerAt(int x, int y) {
    if (player.getX() == x && player.getY() == y) {
      return true;
    }
    for (Player op: players) {
      if (op.getX() == x && op.getY() == y) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a new node in the tree using the supplied player and clones of all other players.
   *
   * @param successors
   * @param newPlayer
   * @param newDisc
   * @param thrower
   */
  private void addNode(HashSet<Node> successors, Player newPlayer, Disc newDisc, Player thrower, int newStep) {
    // The supplied player might be the main player for this node.
    Player tempPlayer;
    if (player.getPlayerNum() == newPlayer.getPlayerNum()) {
      tempPlayer = newPlayer;
    }
    else {
      tempPlayer = player.getClone();
    }
    // The supplied player might be in the players list for this node.
    ArrayList<Player> pls = new ArrayList<>();
    for (Player p : players) {
      if (p.getPlayerNum() == newPlayer.getPlayerNum()) {
        pls.add(newPlayer);
      }
      else {
        pls.add(p.getClone());
      }
    }
    // We use this method to generate nodes from other nodes on the same level when generating
    // options for other players
    Node newParent = step == newStep ? parent : this;
    Node newNode = new Node(tempPlayer, newDisc, pls, newStep, thrower, newParent);
    // Handle the transition from throwing to moving for the other players
    for (Player p: newNode.getPlayers()) {
      if (p.getState() == PlayerThrowingState.getInstance() &&
              getPlayer(p.getPlayerNum()).getState() == PlayerThrowingState.getInstance()) {
        p.setState(PlayerMovingState.getInstance());
      }
    }
    successors.add(newNode);
  }

  /**
   * Generate successors for the catching action.
   *
   * @param successors
   * @param player
   * @param newStep
   */
  private void catchOptions(HashSet<Node> successors, Player player, int newStep) {
    Player newPlayer = player.getClone();
    Disc newDisc = disc.getClone();
    newPlayer.notifyDisc(newDisc);
    newPlayer.catchDisc();
    addNode(successors, newPlayer, newDisc, lastThrower, newStep);
  }

  /**
   * Generate the heat map showing relative empowerment of possible subsequent states.
   *
   * @param heatMap
   */
  public void heatMap(HashMap<Entity, Integer> heatMap) {
    if (step > 0) {
      if (!heatMap.containsKey((Entity) player)) {
        heatMap.put((Entity) player.getClone(), numLeafNodes);
      }
      else {
        heatMap.put((Entity) player,  heatMap.get((Entity) player) + numLeafNodes);
      }
    }
    if (step == 0) {
      for (Node successor: successors) {
        if (successor.getNumLeafNodes() > 0) {
          successor.heatMap(heatMap);
        }
      }
    }
  }
}
