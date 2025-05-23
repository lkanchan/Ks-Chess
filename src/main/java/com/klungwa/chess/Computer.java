package com.klungwa.chess;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Computer {
    private KPanel chessBoard;
    private boolean isWhite;
    private Random random = new Random();

    public Computer(KPanel chessBoard, boolean isWhite) {
        this.chessBoard = chessBoard;
        this.isWhite = isWhite;
    }

    public void makeMove() {
        if (this.chessBoard.isWhiteTurn() == this.isWhite) {
            List<Piece> pieces = this.chessBoard.getPieces(this.isWhite);
            Collections.shuffle(pieces);
            Move bestMove = this.findBestMove(pieces);
            if (bestMove != null) {
                this.chessBoard.makeComputerMove(bestMove.piece, bestMove.destination.x, bestMove.destination.y);
            }
        }
    }

    private Move findBestMove(List<Piece> pieces) {
        ArrayList<Move> allMoves = new ArrayList<>();
        ArrayList<Move> captureMoves = new ArrayList<>();
        ArrayList<Move> safeMoves = new ArrayList<>();

        for (Piece piece : pieces) {
            List<Point> validMoves = this.chessBoard.getValidMovesForComputer(piece);
            for (Point destination : validMoves) {
                Move move = new Move(piece, destination);
                allMoves.add(move);
                if (this.chessBoard.isCaptureMove(piece, destination.x, destination.y)) {
                    captureMoves.add(move);
                }
                if (!this.chessBoard.wouldLeaveInCheck(piece, destination.x, destination.y)) {
                    safeMoves.add(move);
                }
            }
        }

        if (!captureMoves.isEmpty()) {
            return this.selectBestCapture(captureMoves);
        } else if (!safeMoves.isEmpty()) {
            return safeMoves.get(this.random.nextInt(safeMoves.size()));
        } else if (!allMoves.isEmpty()) {
            return allMoves.get(this.random.nextInt(allMoves.size()));
        } else {
            return null;
        }
    }

    private Move selectBestCapture(List<Move> captureMoves) {
        captureMoves.sort((move1, move2) -> {
            int value1 = this.getPieceValue(move2.piece.getType());
            int value2 = this.getPieceValue(move1.piece.getType());
            return Integer.compare(value1, value2);
        });
        return captureMoves.get(0);
    }

    private int getPieceValue(PieceType type) {
        switch(type) {
            case PAWN: return 1;
            case KNIGHT: return 3;
            case BISHOP: return 3;
            case ROOK: return 5;
            case QUEEN: return 9;
            case KING: return 100;
            default: return 0;
        }
    }

    private class Move {
        Piece piece;
        Point destination;

        public Move(Piece piece, Point destination) {
            this.piece = piece;
            this.destination = destination;
        }
    }
}