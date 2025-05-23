package com.klungwa.chess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TileClickListener implements ActionListener {
  private KPanel kPanel;
  
  private int row;
  
  private int col;
  
  public TileClickListener(KPanel kPanel, int row, int col) {
    this.kPanel = kPanel;
    this.row = row;
    this.col = col;
  }
  
  public void actionPerformed(ActionEvent e) {
    this.kPanel.tileClicked(this.row, this.col);
  }
}
