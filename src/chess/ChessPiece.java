package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;

public abstract class ChessPiece extends Piece {

	private Color color;
	private int moveCount;

	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public int getMoveCount() {
		return moveCount;
	}
	
	public void increaseMoveCount() {
		moveCount++;
	}
	public void decreaseMoveCount() {
		moveCount--;
	}
	
	// Metodo para retornar a posição no formato do xadrez
	public ChessPosition getChessPosition() {
		return ChessPosition.fromPosition(position);
	}
	
	//Essa operação fica nessa classe pois vai ser reaproveitada em todas as outras peças.
	protected boolean isThereOpponentPiece(Position position) {
		ChessPiece p = (ChessPiece)getBoard().piece(position);
		return p != null && p.getColor() != color;
	}

	//Apaga o SET para não pode fazer alteração pois so pode ser acessado.
	
	
	
	
}
