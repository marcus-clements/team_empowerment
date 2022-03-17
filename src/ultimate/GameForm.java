package ultimate;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

/**
 * Provides GUI.
 *
 * Created by mc on 25/01/2016.
 */
public class GameForm {
  private JPanel gamePanel;
  private JButton goButton;
  private JLabel runningLabel;
  private JLabel stepLabel;
  private JComboBox empowermentLookaheadComboBox;
  private JComboBox throwRangeComboBox;
  private JComboBox scenarioComboBox;
  private JCheckBox includeTeamMovesCheckBox;
  private JComboBox discVelocityComboBox;
  private Graphics graphics;
  private int topMargin = 50;
  private int leftMargin = 50;
  private int cellWidth = 20;
  private int cellHeight = 20;
  private boolean simRunning = false;
  private ArrayList<Scenario> scenarios;

  public GameForm(ArrayList<Scenario> scenarios) {
    this.scenarios = scenarios;
    goButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        simRunning = !simRunning;
        runningLabel.setText(getRunningLabelText());
        goButton.setText(getGoButtonText());
      }
    });

    empowermentLookaheadComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Pitch.setEmpowermentLookahead(Integer.parseInt(empowermentLookaheadComboBox.getSelectedItem().toString()));
      }
    });

    throwRangeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Pitch.setThrowRange(Integer.parseInt(throwRangeComboBox.getSelectedItem().toString()));
      }
    });

    scenarioComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        simRunning = false;
        runningLabel.setText(getRunningLabelText());
        goButton.setText(getGoButtonText());
        Pitch.getInstance().initialise((Scenario)scenarioComboBox.getSelectedItem());
      }
    });

    includeTeamMovesCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Pitch.setIncludeTeamMoves(!Pitch.getIncludeTeamMoves());
      }
    });

    discVelocityComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Pitch.setDiscVelocity(Integer.parseInt(discVelocityComboBox.getSelectedItem().toString()));
      }
    });

  }

  public boolean isSimRunning() {
    return simRunning;
  }

  public void setSimRunning(boolean simRunning) {
    this.simRunning = simRunning;
    this.goButton.setText(getGoButtonText());
  }

  private String getRunningLabelText() {
    return simRunning ? "Running..." : "Not running...";
  }

  private String getGoButtonText() {
    return simRunning ? "Stop" : "Go";
  }

  /**
   * Sets up the GUI, draws the grid. Called at the beginning of an experiment.
   */
  public void initialise() {
    JFrame frame = new JFrame("GameForm");
    frame.setContentPane(gamePanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    graphics = frame.getGraphics();
    eraseAll();
    drawGrid();
    runningLabel.setText(getRunningLabelText());
    stepLabel.setText("");
    throwRangeComboBox.setSelectedItem(Integer.toString(Pitch.getThrowRange()));
    empowermentLookaheadComboBox.setSelectedItem(Integer.toString(Pitch.getEmpowermentLookahead()));
    discVelocityComboBox.setSelectedItem(Integer.toString(Pitch.getDiscVelocity()));
    Scenario scenario = Pitch.getInstance().getCurrentScenario();
    scenarioComboBox.setSelectedItem(scenario);
    includeTeamMovesCheckBox.setSelected(Pitch.getIncludeTeamMoves());
  }

  public void displayStep(Integer step) {
    stepLabel.setText(step.toString());
  }

  public void drawDisc(Disc disc) {
    Color bgcolor = graphics.getColor();
    graphics.setColor(Color.RED);
    if (disc.getState() == DiscThrownState.getInstance()) {
      graphics.drawLine(
        xLoc(disc.getX()) + cellWidth / 2,
        yLoc(disc.getY()) + cellHeight / 2,
        xLoc(disc.getDestX()) + cellWidth / 2,
        yLoc(disc.getDestY()) + cellHeight / 2);
    }

    int discX = disc.getX();
    int discY = disc.getY();
    if (disc.getState() == DiscFlyingState.getInstance()) {
      discX = disc.getNextX();
      discY = disc.getNextY();
    }
    graphics.fillOval(
      xLoc(discX) + 5,
      yLoc(discY) + 5,
      cellWidth - 10,
      cellHeight - 10);
    graphics.setColor(bgcolor);

    graphics.setColor(bgcolor);
  }

  public void drawPlayer(Player player) {
    Color bgcolor = graphics.getColor();
    if (player.getTeam() == 0) {
      graphics.setColor(Color.BLUE);
    }
    else {
      graphics.setColor(Color.GREEN);
    }
    graphics.fillRect(
            xLoc(player.getX()) + 4,
            yLoc(player.getY()) + 4,
            cellWidth - 7,
            cellHeight - 7);
    graphics.setColor(bgcolor);
  }

  /**
   * Clear a grid square to background colour.
   * @param x horizontal index of square to erase
   * @param y vertical index of square to erase
   */
  public void erase(int x, int y) {
    Color prevColor = graphics.getColor();
    graphics.setColor(new Color(0,0,0));
    graphics.fillRect(xLoc(x) + 1, yLoc(y) + 1, cellWidth - 2, cellHeight - 2);
    graphics.setColor(prevColor);
  }

  public void eraseAll() {
    Color prevColor = graphics.getColor();
    graphics.setColor(new Color(0,0,0));
    graphics.fillRect(
            leftMargin,
            topMargin,
            Pitch.getWidth() * cellWidth,
            Pitch.getHeight() * cellHeight);
    graphics.setColor(prevColor);
  }

  public void drawHeatMap(HashMap<Entity, Integer> heatMap, Player oldPlayer, Disc oldDisc) {
    double max = 1;
    double min = 0;
    for (Integer numLeafNodes: heatMap.values()) {
      max = Node.log2(numLeafNodes) > max ? Node.log2(numLeafNodes) : max;
    }
    Color bgcolor = graphics.getColor();
    for (Entity e: heatMap.keySet()) {
        double brightness = ((Node.log2(heatMap.get(e)) - min) * 180) / (max - min);
        graphics.setColor(new Color((int) (brightness), (int) (brightness), (int) (brightness)));
        graphics.fillRect(xLoc(e.getX()) + 1, yLoc(e.getY()) + 1, cellWidth - 1, cellHeight - 1);
    }
    graphics.setColor(bgcolor);
    drawPlayer(oldPlayer);
    drawDisc(oldDisc);
  }

  public int xLoc(int x) {
    return x * cellWidth + leftMargin;
  }

  public int yLoc(int y) {
    return y * cellHeight + topMargin;
  }

  public void drawGrid() {
    Color prevColor = graphics.getColor();
    graphics.setColor(new Color(230,230,230));
    // Draw horizontal grid lines
    for (int n = 0; n <= Pitch.getHeight(); n++) {
      // horizontal
      graphics.drawLine(
        leftMargin,
        n * cellHeight + topMargin,
        Pitch.getWidth() * cellWidth + leftMargin,
        n * cellHeight + topMargin
      );
    }

    // Draw vertical grid lines
    for (int n = 0; n <= Pitch.getWidth(); n++) {
      graphics.drawLine(
        n * cellWidth + leftMargin,
        topMargin,
        n * cellWidth + leftMargin,
        Pitch.getHeight() * cellHeight + topMargin
      );
    }

    graphics.setColor(prevColor);
  }

  private void createUIComponents() {
    String [] options = {"1", "2", "3", "4", "5", "7", "10"};
    empowermentLookaheadComboBox = new JComboBox(options);
    options = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "10"};
    throwRangeComboBox = new JComboBox(options);
    options = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "15", "20", "100"};
    discVelocityComboBox = new JComboBox(options);
    scenarioComboBox = new JComboBox(scenarios.toArray());
    scenarioComboBox.setSelectedItem(scenarios.get(0));
  }
}
