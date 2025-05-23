package com.klungwa.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class KPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;
    private Tile[][] tiles = new Tile[8][8];
    private Piece[][] board = new Piece[8][8];
    private Piece selectedPiece = null;
    private boolean isWhiteTurn = true;
    private Point selectedPiecePosition = null;
    private List<Point> possibleMoves = new ArrayList<>();
    private KFrame parentFrame;
    private boolean gameOver = false;
    private ImageLoader imageLoader;
    private Computer computer;
    private boolean vsComputer = false;
    private List<String> whiteMoves = new ArrayList<>();
    private List<String> blackMoves = new ArrayList<>();
    private int currentMoveNumber = 1;
    private Point enPassantTarget = null;

    public List<String> getWhiteMoves() {
        return this.whiteMoves;
    }

    public List<String> getBlackMoves() {
        return this.blackMoves;
    }

    public KPanel(KFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.imageLoader = new ImageLoader();
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(640, 640));
        this.initializeBoard();
        this.setupPieces();
        this.createTiles();
    }

    public void setVsComputer(boolean vsComputer) {
        this.vsComputer = vsComputer;
        if (vsComputer) {
            this.computer = new Computer(this, false);
        }
    }

    private void initializeBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.board[row][col] = null;
            }
        }
    }

    private void setupPieces() {
        for (int col = 0; col < 8; col++) {
            this.board[1][col] = new Piece(PieceType.PAWN, false, 1, col);
            this.board[6][col] = new Piece(PieceType.PAWN, true, 6, col);
        }

        this.board[0][0] = new Piece(PieceType.ROOK, false, 0, 0);
        this.board[0][7] = new Piece(PieceType.ROOK, false, 0, 7);
        this.board[7][0] = new Piece(PieceType.ROOK, true, 7, 0);
        this.board[7][7] = new Piece(PieceType.ROOK, true, 7, 7);
        this.board[0][1] = new Piece(PieceType.KNIGHT, false, 0, 1);
        this.board[0][6] = new Piece(PieceType.KNIGHT, false, 0, 6);
        this.board[7][1] = new Piece(PieceType.KNIGHT, true, 7, 1);
        this.board[7][6] = new Piece(PieceType.KNIGHT, true, 7, 6);
        this.board[0][2] = new Piece(PieceType.BISHOP, false, 0, 2);
        this.board[0][5] = new Piece(PieceType.BISHOP, false, 0, 5);
        this.board[7][2] = new Piece(PieceType.BISHOP, true, 7, 2);
        this.board[7][5] = new Piece(PieceType.BISHOP, true, 7, 5);
        this.board[0][3] = new Piece(PieceType.QUEEN, false, 0, 3);
        this.board[7][3] = new Piece(PieceType.QUEEN, true, 7, 3);
        this.board[0][4] = new Piece(PieceType.KING, false, 0, 4);
        this.board[7][4] = new Piece(PieceType.KING, true, 7, 4);
    }

    private void createTiles() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = new Tile(row, col);
                this.tiles[row][col] = tile;
                if ((row + col) % 2 == 0) {
                    tile.setBackground(new Color(240, 217, 181));
                } else {
                    tile.setBackground(new Color(181, 136, 99));
                }

                Piece piece = this.board[row][col];
                if (piece != null) {
                    ImageIcon icon = this.imageLoader.getImageForPiece(piece);
                    if (icon != null) {
                        tile.setIcon(icon);
                    } else {
                        tile.setText(piece.getSymbol());
                    }
                }

                tile.addActionListener(new TileClickListener(this, row, col));
                this.add(tile);
            }
        }
    }

    public void tileClicked(int row, int col) {
        if (!this.gameOver) {
            if (!this.vsComputer || this.isWhiteTurn) {
                Piece clickedPiece;
                if (this.selectedPiece == null) {
                    clickedPiece = this.board[row][col];
                    if (clickedPiece != null && clickedPiece.isWhite() == this.isWhiteTurn) {
                        this.selectedPiece = clickedPiece;
                        this.selectedPiecePosition = new Point(row, col);
                        this.possibleMoves = this.getValidMoves(this.selectedPiece);
                        this.updateBoard();
                    }
                } else {
                    if (this.isValidMove(this.selectedPiece, row, col)) {
                        this.movePiece(this.selectedPiece, row, col);
                        this.selectedPiece = null;
                        this.selectedPiecePosition = null;
                        this.possibleMoves.clear();
                        this.isWhiteTurn = !this.isWhiteTurn;
                        this.checkGameState();
                        if (this.vsComputer && !this.gameOver && !this.isWhiteTurn) {
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    Thread.sleep(500L);
                                    this.computer.makeMove();
                                    this.checkGameState();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } else {
                        clickedPiece = this.board[row][col];
                        if (clickedPiece != null && clickedPiece.isWhite() == this.isWhiteTurn) {
                            this.selectedPiece = clickedPiece;
                            this.selectedPiecePosition = new Point(row, col);
                            this.possibleMoves = this.getValidMoves(this.selectedPiece);
                        }
                    }
                    this.updateBoard();
                }
            }
        }
    }

    private void checkGameState() {
        if (this.isCheckmate(!this.isWhiteTurn)) {
            this.gameOver = true;
            this.displayGameResult(this.isWhiteTurn);
            this.highlightKingInCheck(!this.isWhiteTurn);
        } else if (this.isStalemate(!this.isWhiteTurn)) {
            this.gameOver = true;
            JOptionPane.showMessageDialog(this, "Game ended in stalemate!", "Game Over", 1);
            this.parentFrame.updateStatus("Game ended in stalemate!");
        } else if (this.isInCheck(!this.isWhiteTurn)) {
            this.parentFrame.updateStatus((this.isWhiteTurn ? "Black" : "White") + " is in check!");
        } else {
            this.parentFrame.updateStatus(this.isWhiteTurn ? "White's turn" : "Black's turn");
        }
    }

    public void makeComputerMove(Piece piece, int row, int col) {
        this.movePiece(piece, row, col);
        this.isWhiteTurn = !this.isWhiteTurn;
        this.updateBoard();
    }

    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = this.tiles[row][col];
                if ((row + col) % 2 == 0) {
                    tile.setBackground(new Color(240, 217, 181));
                } else {
                    tile.setBackground(new Color(181, 136, 99));
                }

                if (this.selectedPiecePosition != null && this.selectedPiecePosition.x == row && this.selectedPiecePosition.y == col) {
                    tile.setBackground(Color.YELLOW);
                } else if (this.containsPoint(this.possibleMoves, row, col)) {
                    tile.setBackground(Color.GREEN);
                }

                Piece piece = this.board[row][col];
                tile.setText("");
                tile.setIcon((Icon) null);
                if (piece != null) {
                    ImageIcon icon = this.imageLoader.getImageForPiece(piece);
                    if (icon != null) {
                        tile.setIcon(icon);
                    } else {
                        tile.setText(piece.getSymbol());
                    }
                }
            }
        }
    }

    private void movePiece(Piece piece, int row, int col) {
        String moveNotation = this.getMoveNotation(piece, row, col);
        Piece capturedPiece;
        if (piece.getType() == PieceType.PAWN && (row == 0 || row == 7)) {
            if (this.vsComputer && !piece.isWhite()) {
                capturedPiece = new Piece(PieceType.QUEEN, piece.isWhite(), row, col);
                this.board[piece.getRow()][piece.getCol()] = null;
                this.board[row][col] = capturedPiece;
                moveNotation = moveNotation + "=Q";
            } else {
                capturedPiece = this.showPromotionDialog(piece.isWhite(), row, col);
                this.board[piece.getRow()][piece.getCol()] = null;
                this.board[row][col] = capturedPiece;
                moveNotation = moveNotation + "=" + capturedPiece.getType().toString().charAt(0);
            }
        } else if (piece.getType() == PieceType.KING && Math.abs(col - piece.getCol()) == 2) {
            if (col > piece.getCol()) {
                capturedPiece = this.board[piece.getRow()][7];
                this.board[piece.getRow()][5] = capturedPiece;
                this.board[piece.getRow()][7] = null;
                capturedPiece.setCol(5);
            } else {
                capturedPiece = this.board[piece.getRow()][0];
                this.board[piece.getRow()][3] = capturedPiece;
                this.board[piece.getRow()][0] = null;
                capturedPiece.setCol(3);
            }

            this.board[piece.getRow()][piece.getCol()] = null;
            piece.setPosition(row, col);
            this.board[row][col] = piece;
        } else if (piece.getType() == PieceType.PAWN && col != piece.getCol() && this.board[row][col] == null) {
            this.board[piece.getRow()][col] = null;
            this.board[piece.getRow()][piece.getCol()] = null;
            piece.setPosition(row, col);
            this.board[row][col] = piece;
        } else {
            this.board[piece.getRow()][piece.getCol()] = null;
            piece.setPosition(row, col);
            this.board[row][col] = piece;
        }

        if (piece.getType() == PieceType.PAWN && Math.abs(row - piece.getRow()) == 2) {
            this.enPassantTarget = new Point(row, col);
        } else {
            this.enPassantTarget = null;
        }

        piece.setHasMoved(true);
        if (piece.isWhite()) {
            this.whiteMoves.add(moveNotation);
        } else {
            this.blackMoves.add(moveNotation);
            ++this.currentMoveNumber;
        }

        this.updateBoard();
        if (this.parentFrame.isHistoryVisible()) {
            this.parentFrame.updateMoveHistoryDisplay();
        }
    }

    private Piece showPromotionDialog(boolean isWhite, int row, int col) {
        Object[] options = new Object[]{"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(this, "Promote pawn to:", "Pawn Promotion", -1, -1, (Icon) null, options, options[0]);
        PieceType type;
        switch (choice) {
            case 0:
                type = PieceType.QUEEN;
                break;
            case 1:
                type = PieceType.ROOK;
                break;
            case 2:
                type = PieceType.BISHOP;
                break;
            case 3:
                type = PieceType.KNIGHT;
                break;
            default:
                type = PieceType.QUEEN;
        }

        return new Piece(type, isWhite, row, col);
    }

    private String getMoveNotation(Piece piece, int row, int col) {
        String[] columns = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        String pieceSymbol = piece.getType() == PieceType.PAWN ? "" : (piece.getType() == PieceType.KNIGHT ? "N" : String.valueOf(piece.getType().toString().charAt(0)));
        if (piece.getType() == PieceType.PAWN && this.board[row][col] != null) {
            pieceSymbol = columns[piece.getCol()];
        }

        String captureSymbol = this.board[row][col] == null && (piece.getType() != PieceType.PAWN || col == piece.getCol()) ? "" : "x";
        String destination = columns[col] + (8 - row);
        if (piece.getType() == PieceType.KING && Math.abs(col - piece.getCol()) == 2) {
            return col > piece.getCol() ? "O-O" : "O-O-O";
        } else {
            String moveText = pieceSymbol + captureSymbol + destination;
            if (this.isCheckmate(!piece.isWhite())) {
                moveText = moveText + "#";
            } else if (this.isInCheck(!piece.isWhite())) {
                moveText = moveText + "+";
            }

            return moveText;
        }
    }

    private List<Point> getValidMoves(Piece piece) {
        List<Point> pseudoLegalMoves = this.getPseudoLegalMoves(piece);
        ArrayList<Point> validMoves = new ArrayList<>();
        Iterator<Point> iterator = pseudoLegalMoves.iterator();

        while (iterator.hasNext()) {
            Point move = iterator.next();
            if (!this.wouldLeaveKingInCheck(piece, move.x, move.y)) {
                validMoves.add(move);
            }
        }

        if (piece.getType() == PieceType.KING && !piece.hasMoved()) {
            if (this.isValidCastle(piece, piece.getCol() + 2)) {
                validMoves.add(new Point(piece.getRow(), piece.getCol() + 2));
            }

            if (this.isValidCastle(piece, piece.getCol() - 2)) {
                validMoves.add(new Point(piece.getRow(), piece.getCol() - 2));
            }
        }

        return validMoves;
    }

    private boolean isValidCastle(Piece king, int targetCol) {
        if (!king.hasMoved() && !this.isInCheck(king.isWhite())) {
            int rookCol = targetCol > king.getCol() ? 7 : 0;
            Piece rook = this.board[king.getRow()][rookCol];
            if (rook != null && rook.getType() == PieceType.ROOK && !rook.hasMoved()) {
                int startCol = Math.min(king.getCol(), rookCol) + 1;
                int endCol = Math.max(king.getCol(), rookCol);

                for (int col = startCol; col < endCol; col++) {
                    if (this.board[king.getRow()][col] != null) {
                        return false;
                    }

                    if (this.isSquareUnderAttack(king.getRow(), col, !king.isWhite())) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isSquareUnderAttack(int row, int col, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = this.board[r][c];
                if (piece != null && piece.isWhite() == byWhite) {
                    List<Point> moves = this.getPseudoLegalMoves(piece);
                    Iterator<Point> iterator = moves.iterator();

                    while (iterator.hasNext()) {
                        Point move = iterator.next();
                        if (move.x == row && move.y == col) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void addStraightMoves(List<Point> moves, int row, int col, boolean isWhite) {
        int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int[][] var6 = directions;
        int var7 = directions.length;

        for (int var8 = 0; var8 < var7; var8++) {
            int[] direction = var6[var8];

            for (int distance = 1; distance < 8; distance++) {
                int newRow = row + direction[0] * distance;
                int newCol = col + direction[1] * distance;
                if (!this.isInBounds(newRow, newCol)) {
                    break;
                }

                if (this.board[newRow][newCol] != null) {
                    if (this.board[newRow][newCol].isWhite() != isWhite) {
                        moves.add(new Point(newRow, newCol));
                    }
                    break;
                }

                moves.add(new Point(newRow, newCol));
            }
        }
    }

    private void addDiagonalMoves(List<Point> moves, int row, int col, boolean isWhite) {
        int[][] directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] var6 = directions;
        int var7 = directions.length;

        for (int var8 = 0; var8 < var7; var8++) {
            int[] direction = var6[var8];

            for (int distance = 1; distance < 8; distance++) {
                int newRow = row + direction[0] * distance;
                int newCol = col + direction[1] * distance;
                if (!this.isInBounds(newRow, newCol)) {
                    break;
                }

                if (this.board[newRow][newCol] != null) {
                    if (this.board[newRow][newCol].isWhite() != isWhite) {
                        moves.add(new Point(newRow, newCol));
                    }
                    break;
                }

                moves.add(new Point(newRow, newCol));
            }
        }
    }

    private List<Point> getPseudoLegalMoves(Piece piece) {
        ArrayList<Point> moves = new ArrayList<>();
        int row = piece.getRow();
        int col = piece.getCol();
        boolean isWhite = piece.isWhite();
        int[][] directions;
        int newRow;
        int newCol;
        int[] pawnCaptures;
        int i;
        switch (piece.getType()) {
            case PAWN:
                int direction = isWhite ? -1 : 1;
                int startRow = isWhite ? 6 : 1;
                if (this.isInBounds(row + direction, col) && this.board[row + direction][col] == null) {
                    moves.add(new Point(row + direction, col));
                    if (row == startRow && this.isInBounds(row + 2 * direction, col) && this.board[row + 2 * direction][col] == null) {
                        moves.add(new Point(row + 2 * direction, col));
                    }
                }

                pawnCaptures = new int[]{col - 1, col + 1};
                int var13 = pawnCaptures.length;

                for (i = 0; i < var13; i++) {
                    newCol = pawnCaptures[i];
                    if (this.isInBounds(row + direction, newCol)) {
                        if (this.board[row + direction][newCol] != null && this.board[row + direction][newCol].isWhite() != isWhite) {
                            moves.add(new Point(row + direction, newCol));
                        } else if (this.enPassantTarget != null && this.enPassantTarget.x == row + direction && this.enPassantTarget.y == newCol) {
                            moves.add(new Point(row + direction, newCol));
                        }
                    }
                }

                return moves;
            case ROOK:
                this.addStraightMoves(moves, row, col, isWhite);
                break;
            case KNIGHT:
                directions = new int[][]{{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
                int[][] var9 = directions;
                int var10 = directions.length;

                for (i = 0; i < var10; i++) {
                    int[] knightMove = var9[i];
                    newRow = row + knightMove[0];
                    newCol = col + knightMove[1];
                    if (this.isInBounds(newRow, newCol) && (this.board[newRow][newCol] == null || this.board[newRow][newCol].isWhite() != isWhite)) {
                        moves.add(new Point(newRow, newCol));
                    }
                }

                return moves;
            case BISHOP:
                this.addDiagonalMoves(moves, row, col, isWhite);
                break;
            case QUEEN:
                this.addStraightMoves(moves, row, col, isWhite);
                this.addDiagonalMoves(moves, row, col, isWhite);
                break;
            case KING:
                directions = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
                int[][] var11 = directions;
                int var12 = directions.length;

                for (int var14 = 0; var14 < var12; var14++) {
                    int[] kingMove = var11[var14];
                    newRow = row + kingMove[0];
                    newCol = col + kingMove[1];
                    if (this.isInBounds(newRow, newCol) && (this.board[newRow][newCol] == null || this.board[newRow][newCol].isWhite() != isWhite)) {
                        moves.add(new Point(newRow, newCol));
                    }
                }
        }

        return moves;
    }

    private boolean isValidMove(Piece piece, int row, int col) {
        List<Point> validMoves = this.getValidMoves(piece);
        Iterator<Point> iterator = validMoves.iterator();

        Point move;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            move = iterator.next();
        } while (move.x != row || move.y != col);

        return true;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean wouldLeaveKingInCheck(Piece piece, int row, int col) {
        Piece capturedPiece = this.board[row][col];
        this.board[piece.getRow()][piece.getCol()] = null;
        this.board[row][col] = piece;
        int originalRow = piece.getRow();
        int originalCol = piece.getCol();
        piece.setPosition(row, col);
        boolean inCheck = this.isInCheck(piece.isWhite());
        piece.setPosition(originalRow, originalCol);
        this.board[originalRow][originalCol] = piece;
        this.board[row][col] = capturedPiece;
        return inCheck;
    }

    private boolean isInCheck(boolean isWhite) {
        Point kingPosition = this.findKing(isWhite);
        return kingPosition == null ? false : this.isSquareUnderAttack(kingPosition.x, kingPosition.y, !isWhite);
    }

    private Point findKing(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = this.board[row][col];
                if (piece != null && piece.getType() == PieceType.KING && piece.isWhite() == isWhite) {
                    return new Point(row, col);
                }
            }
        }

        return null;
    }

    private boolean isCheckmate(boolean isWhite) {
        if (!this.isInCheck(isWhite)) {
            return false;
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = this.board[row][col];
                    if (piece != null && piece.isWhite() == isWhite) {
                        List<Point> moves = this.getValidMoves(piece);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean isStalemate(boolean isWhite) {
        if (this.isInCheck(isWhite)) {
            return false;
        } else {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Piece piece = this.board[row][col];
                    if (piece != null && piece.isWhite() == isWhite) {
                        List<Point> moves = this.getValidMoves(piece);
                        if (!moves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean containsPoint(List<Point> points, int row, int col) {
        Iterator<Point> iterator = points.iterator();

        Point point;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            point = iterator.next();
        } while (point.x != row || point.y != col);

        return true;
    }

    public List<Piece> getPieces(boolean isWhite) {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = this.board[row][col];
                if (piece != null && piece.isWhite() == isWhite) {
                    pieces.add(piece);
                }
            }
        }

        return pieces;
    }

    public List<Point> getValidMovesForComputer(Piece piece) {
        return this.getValidMoves(piece);
    }

    public boolean isWhiteTurn() {
        return this.isWhiteTurn;
    }

    public boolean isCaptureMove(Piece piece, int row, int col) {
        return this.board[row][col] != null && this.board[row][col].isWhite() != piece.isWhite();
    }

    public boolean wouldLeaveInCheck(Piece piece, int row, int col) {
        Piece capturedPiece = this.board[row][col];
        this.board[piece.getRow()][piece.getCol()] = null;
        this.board[row][col] = piece;
        boolean inCheck = this.isInCheck(piece.isWhite());
        this.board[piece.getRow()][piece.getCol()] = piece;
        this.board[row][col] = capturedPiece;
        return inCheck;
    }

    private void highlightKingInCheck(boolean isWhite) {
        Point kingPosition = this.findKing(isWhite);
        if (kingPosition != null) {
            this.tiles[kingPosition.x][kingPosition.y].setBackground(Color.RED);
        }
    }

    private void displayGameResult(boolean isWhiteWinner) {
        String result = isWhiteWinner ? "White won!!" : "Black won!!";
        JOptionPane.showMessageDialog(this, result, "Game Over", 1);
        this.parentFrame.updateStatus(result);
    }
}