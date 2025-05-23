package com.klungwa.chess;

public class Piece {
    private PieceType type;
    private boolean isWhite;
    private int row;
    private int col;   
    private boolean hasMoved = false;
    
    public Piece(PieceType type, boolean isWhite, int row, int col) {
      this.type = type;
      this.isWhite = isWhite;
      this.row = row;
      this.col = col;
    }
    
    public PieceType getType() {
      return this.type;
    }
    
    public boolean isWhite() {
      return this.isWhite;
    }
    
    public int getRow() {
      return this.row;
    }
    
    public int getCol() {
      return this.col;
    }
    
    public boolean hasMoved() {
      return this.hasMoved;
    }
    
    public void setPosition(int paramInt1, int paramInt2) {
      this.row = paramInt1;
      this.col = paramInt2;
      this.hasMoved = true;
    }
    
    public void setCol(int paramInt) {
      this.col = paramInt;
      this.hasMoved = true;
    }
    
    public void setHasMoved(boolean paramBoolean) {
      this.hasMoved = paramBoolean;
    }
    
    public String getSymbol() {
      switch (this.type) {
        case KING:
          return this.isWhite ? "K" : "k";
        case QUEEN:
          return this.isWhite ? "Q" : "q";
        case ROOK:
          return this.isWhite ? "R" : "r";
        case BISHOP:
          return this.isWhite ? "B" : "b";
        case KNIGHT:
          return this.isWhite ? "N" : "n";
        case PAWN:
          return this.isWhite ? "P" : "p";
      } 
      return "";
    }
  }
  