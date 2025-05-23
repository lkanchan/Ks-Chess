package com.klungwa.chess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class KFrame extends JFrame {
  private KPanel kPanel;
  private JLabel statusLabel;
  private JButton showHistoryButton;
  private JTextArea moveHistoryArea;
  private JScrollPane historyScrollPane;
  private JPanel rightPanel;
  private boolean historyVisible = false;
  
  public KFrame() {
    setTitle("K's CHESS");
    setDefaultCloseOperation(3);
    setLayout(new BorderLayout());

    Object[] arrayOfObject = { "Human vs Computer", "You vs You" };
      int i = JOptionPane.showOptionDialog(
        this, 
        "Choose game mode", 
        "Game Mode", 
        JOptionPane.DEFAULT_OPTION, 
        JOptionPane.QUESTION_MESSAGE, 
        null, 
        arrayOfObject, 
        arrayOfObject[0]
    );
    
    // Exit if X is clicked
    if (i == -1) {
        System.exit(0);
        return; 
    }
    
    this.kPanel = new KPanel(this);
    this.kPanel.setVsComputer((i == 0));
    this.statusLabel = new JLabel("White's turn", 0);
    this.statusLabel.setFont(new Font("Arial", 1, 16));
    this.moveHistoryArea = new JTextArea();
    this.moveHistoryArea.setEditable(false);
    this.moveHistoryArea.setFont(new Font("Monospaced", 0, 14));
    this.historyScrollPane = new JScrollPane(this.moveHistoryArea);
    this.historyScrollPane.setPreferredSize(new Dimension(200, 600));
    this.showHistoryButton = new JButton("Show Moves");
    this.showHistoryButton.addActionListener(paramActionEvent -> toggleMoveHistory());
    this.rightPanel = new JPanel(new BorderLayout());
    this.rightPanel.add(this.historyScrollPane, "Center");
    JButton jButton = new JButton("Hide Moves");
    jButton.addActionListener(paramActionEvent -> toggleMoveHistory());
    this.rightPanel.add(jButton, "South");
    this.rightPanel.setVisible(false);
    add(this.showHistoryButton, "North");
    add(this.kPanel, "Center");
    add(this.statusLabel, "South");
    add(this.rightPanel, "East");
    pack();
    setLocationRelativeTo((Component)null);
  }
  
  public void updateStatus(String status) {
    this.statusLabel.setText(status);
  }
  
  private void toggleMoveHistory() {
    this.historyVisible = !this.historyVisible;
    this.rightPanel.setVisible(this.historyVisible);
    this.showHistoryButton.setText(this.historyVisible ? "Hide Moves" : "Show Moves");
    if (this.historyVisible)
      updateMoveHistoryDisplay(); 
    pack();
  }
  
  public void updateMoveHistoryDisplay() {
    List<String> list1 = this.kPanel.getWhiteMoves();
    List<String> list2 = this.kPanel.getBlackMoves();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Move History\n\n");
    for (byte b = 0; b < Math.max(list1.size(), list2.size()); b++) {
      String str1 = (b < list1.size()) ? list1.get(b) : "";
      String str2 = (b < list2.size()) ? list2.get(b) : "";
      if (b < list1.size() || b < list2.size())
        stringBuilder.append(String.format("%2d. %-7s %-7s%n", new Object[] { Integer.valueOf(b + 1), str1, str2 })); 
    } 
    this.moveHistoryArea.setText(stringBuilder.toString());
  }
  
  public boolean isHistoryVisible() {
    return this.historyVisible;
  }
}
