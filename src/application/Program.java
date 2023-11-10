package application;

import java.util.Locale;
import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		
				
		ChessMatch chessMatch = new ChessMatch();
		
		while(true) {
		UI.printBoard(chessMatch.getPieces());//chamada para imprimir o tabuleiro
		System.out.println();
		System.out.print("Origem: ");
		ChessPosition source = UI.readChessPosition(sc);
		
		System.out.println();
		System.out.print("Destino: ");
		ChessPosition target = UI.readChessPosition(sc);
		
		ChessPiece capturePiece = chessMatch.perfomChessMove(source, target);
	}
		
		
		
	}

}
