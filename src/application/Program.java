package application;

import java.util.Locale;
import java.util.Scanner;

import chess.ChessMatch;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		
		ChessMatch chessMatch = new ChessMatch();
		UI.printBoard(chessMatch.getPieces());//chamada para imprimir o tabuleiro

		sc.close();
	}

}
