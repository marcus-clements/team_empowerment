package ultimate;

import java.util.Objects;

import javax.vecmath.Vector2d;


/**
 * Provides Disc State and movement.
 * 
 * Created by mc on 25/01/2016.
 */
public class Disc extends Entity {
  private int destX;
  private int destY;
  private int nextY;
  private int nextX;
  private DiscState state = DiscLandedState.getInstance();

  public Disc() {
    super();
  }

  public Disc(int x, int y) {
    super(x, y);
  }

  public Disc getClone() {
    Disc ret = new Disc(x,y);
    ret.setState(state);
    ret.nextX = nextX;
    ret.nextY = nextY;
    ret.destX = destX;
    ret.destY = destY;
    return ret;
  }

  public String toString() {
    String ret =  "D(" + x + "," + y + "," + state.toString();
    if (state == DiscFlyingState.getInstance() || state == DiscThrownState.getInstance()) {
      ret += "," + nextX + "," + nextY;
      ret += "," + destX + "," + destY;
    }
    ret += ")";
    return ret;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x,y,destX,destY,state);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    Disc comp = (Disc) obj;
    return comp.getX() == x &&
            comp.getY() == y &&
            comp.getDestX() == destX &&
            comp.getDestY() == destY &&
            comp.getState() == state;
  }

  public DiscState getState() {
    return state;
  }

  public int getDestX() {
    return destX;
  }

  public int getDestY() {
    return destY;
  }

  public int getNextY() {
    return nextY;
  }

  public void setNextY(int nextY) {
    this.nextY = nextY;
  }

  public int getNextX() {
    return nextX;
  }

  public void setNextX(int nextX) {
    this.nextX = nextX;
  }

  public void setState(DiscState state) {
    this.state = state;
  }

  public void throwTo(int destX, int destY) {
    this.destX = destX;
    this.destY = destY;
    this.setNext();
    this.setState(DiscThrownState.getInstance());
  }

  public void fly() {
    double distToDest = Math.sqrt(Math.pow( this.getDestX() - this.getX(), 2) + Math.pow(this.getDestY() - this.getY(), 2));
    if (distToDest <= Pitch.getDiscVelocity()) {
      this.land();
    }
    else {
      this.setLocation(this.getNextX(), this.getNextY());
      this.setNext();
      this.setState(DiscFlyingState.getInstance());
    }
  }

  public void land() {
    x = destX;
    y = destY;
    this.setState(DiscLandedState.getInstance());
  }

  public void caught(Player player) {
    x = player.getX();
    y = player.getY();
    this.setState(DiscHeldState.getInstance());
  }

  public void setNext() {
    Vector2d toDest = new Vector2d(this.getDestX() - this.getX(), this.getDestY() - this.getY());
    if (toDest.length() > Pitch.getDiscVelocity()) {
      Vector2d curPos = new Vector2d(this.getX(), this.getY());
      toDest.normalize();
      toDest.scale(Pitch.getDiscVelocity());
      curPos.add(toDest);
      this.setNextX((int) Math.round(curPos.getX()));
      this.setNextY((int) Math.round(curPos.getY()));
    }
    else {
      this.setNextX(this.getDestX());
      this.setNextY(this.getDestY());
    }
  }

  @Override
  public void act() {
    state.act(this);
  }
}
