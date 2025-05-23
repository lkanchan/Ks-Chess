package com.klungwa.chess;

import javax.swing.SwingUtilities;

public class Chess {
  public static void main(String[] paramArrayOfString) {
    SwingUtilities.invokeLater(() -> {
          KFrame kFrame = new KFrame();
          kFrame.setVisible(true);
        });
  }
}
