package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);

		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while(!chessMatch.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
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
				
				// Sempre que resultar em uma peça capturada add na lista de peças capturadas.
				if (capturePiece != null) {
					captured.add(capturePiece);
				}
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
		
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);
	}
}
