package com.klungwa.chess;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;

public class Tile extends JButton {
  private int row;
  
  private int col;
  
  public Tile(int row, int col) {
    this.row = row;
    this.col = col;
    setOpaque(true);
    setBorderPainted(false);
    setPreferredSize(new Dimension(80, 80));
    setFont(new Font("Arial", 1, 40));
  }
  
  public int getRow() {
    return this.row;
  }
  
  public int getCol() {
    return this.col;
  }
}
