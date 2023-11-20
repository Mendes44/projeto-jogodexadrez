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
				UI.printBoard(chessMatch.getPieces(), possibleMoves);
				System.out.println();
				System.out.print("Destino: ");
				ChessPosition target = UI.readChessPosition(sc);
				
				ChessPiece capturePiece = chessMatch.perfomChessMove(source, target);
				
				if (capturePiece != null) {
					captured.add(capturePiece);
				}
				
				if (chessMatch.getPromoted() != null) {
					System.out.print("Entre com peca para PROMOCAO (B/C/T/Q): ");
					String type = sc.nextLine().toUpperCase();//Coloca todas para maiusculas.
					while (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("Q")) {
						System.out.print("VALOR INVALIDO!!! >>> Entre com peca para PROMOCAO (B/C/T/Q): ");
					}
					chessMatch.replacePromotedPiece(type);	
				}
			}
			catch(ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
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
