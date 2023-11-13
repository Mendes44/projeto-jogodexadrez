package application;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

import boardgame.BoardException;
import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		
				
		ChessMatch chessMatch = new ChessMatch();
		
		while(true) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch);
				System.out.println();
				System.out.print("Origem: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				
				UI.clearScreen();
				// Sobrecarga - onde passo os movimentos possiveis - responsavel por imprimir o tabuleiro e pintar o tabuleiro
				UI.printBoard(chessMatch.getPieces(), possibleMoves);
				System.out.println();
				System.out.print("Destino: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturePiece = chessMatch.perfomChessMove(source, target);
			}
			catch(ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();//Para aguardar apertar enter.
			}
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
	}
		
		
		
	}

}
