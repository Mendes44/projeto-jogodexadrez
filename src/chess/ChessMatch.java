package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	//Coração do Jogo de Xadrez
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();//Deve colocar o initial no construtor.
	}
	
	/*
	 - Estou na camada de Xadrez. Por isso que tem que liberar uma matriz de ChessPiece
	 - Tabuleiro Board ele tem as peças so que as peças são do tipo Pieces 
	 - O programa so pode ver somente a camada de xadrez e não a de tabuleiro.
	*/
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		
		for (int i=0; i < board.getRows();i++) {
			for(int j=0; j < board.getColumns();j++) {
				mat[i][j] = (ChessPiece) board.piece(i,j); //Fazendo um downCasting
			}
		}
		return mat;
	}
	
	//Aqui estou chamando o board para criar minhas peças na tela.
	private void initialSetup() {
		board.placePiece(new Rook(board, Color.WHITE), new Position(2, 1));
		board.placePiece(new King (board, Color.BLACK), new Position(0, 4));
		board.placePiece(new King (board, Color.WHITE), new Position(7, 4));
	}
}
