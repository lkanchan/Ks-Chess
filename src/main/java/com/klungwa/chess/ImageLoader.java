package com.klungwa.chess;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageLoader {
  private BufferedImage whitePawn;
  private BufferedImage whiteRook;
  private BufferedImage whiteKnight;
  private BufferedImage whiteBishop;
  private BufferedImage whiteQueen;
  private BufferedImage whiteKing;
  private BufferedImage blackPawn;
  private BufferedImage blackRook;
  private BufferedImage blackKnight;
  private BufferedImage blackBishop;
  private BufferedImage blackQueen;
  
  private BufferedImage blackKing;
  
  public ImageLoader() {
    loadImages();
  }
  
  private void loadImages() {
    this.whitePawn = loadImage("/images/white_pawn.png");
    this.whiteRook = loadImage("/images/white_rook.png");
    this.whiteKnight = loadImage("/images/white_knight.png");
    this.whiteBishop = loadImage("/images/white_bishop.png");
    this.whiteQueen = loadImage("/images/white_queen.png");
    this.whiteKing = loadImage("/images/white_king.png");
    this.blackPawn = loadImage("/images/black_pawn.png");
    this.blackRook = loadImage("/images/black_rook.png");
    this.blackKnight = loadImage("/images/black_knight.png");
    this.blackBishop = loadImage("/images/black_bishop.png");
    this.blackQueen = loadImage("/images/black_queen.png");
    this.blackKing = loadImage("/images/black_king.png");
  }
  
   private BufferedImage loadImage(String imagePath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(imagePath);
            if (inputStream == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }
  
  public ImageIcon getImageForPiece(Piece piece) {
    BufferedImage bufferedImage = getRawImageForPiece(piece);
    if (bufferedImage != null)
      return new ImageIcon(bufferedImage.getScaledInstance(70, 70, 4)); 
    return null;
  }
  
  private BufferedImage getRawImageForPiece(Piece piece) {
    if (piece.isWhite()) {
      switch (piece.getType()) {
        case PAWN:
          return this.whitePawn;
        case ROOK:
          return this.whiteRook;
        case KNIGHT:
          return this.whiteKnight;
        case BISHOP:
          return this.whiteBishop;
        case QUEEN:
          return this.whiteQueen;
        case KING:
          return this.whiteKing;
      } 
    } else {
      switch (piece.getType()) {
        case PAWN:
          return this.blackPawn;
        case ROOK:
          return this.blackRook;
        case KNIGHT:
          return this.blackKnight;
        case BISHOP:
          return this.blackBishop;
        case QUEEN:
          return this.blackQueen;
        case KING:
          return this.blackKing;
      } 
    } 
    return null;
  }
}
