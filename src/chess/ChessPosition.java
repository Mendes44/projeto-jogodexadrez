package chess;

import boardgame.Position;

public class ChessPosition {

	private char column;
	private int row;
	
	public ChessPosition(char column, int row) {
		if(column < 'a' || column > 'h' || row < 1 || row > 8) {
			throw new ChessException(">>> ERRO DE INSTANCIAÇÃO CHESSPOSITION! -> "
					+ "VALIDAR VALORES A1 ATE H8.");
		}
		this.column = column;
		this.row = row;
	}
	
	
	public char getColumn() {
		return column;
	}


	public int getRow() {
		return row;
	}


	/*
	 * OPERAÇÃO PARA CONVERTER A POSIÇÃO DA MATRIZ TIPO A1 B2 C3 ... 
	 * EM VEZ DE FICAR POR 0,1 1,2 E ETC
	 */
	protected Position toPosition() {
		return new Position(8 - row , column - 'a');
	}
	
	protected static ChessPosition fromPosition(Position position) {
		return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	//TENHO QUE COLOCAR CHAR NA FRENTE POIS A CONVERSÃO DE CARACTER NÃO E AUTOMATICA
	}
	
	@Override
	public String toString() {
		return "" + column + row;
	}
}
