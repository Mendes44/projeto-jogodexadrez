package boardgame;

public class Board {

	private int rows;
	private int columns;
	private Piece[][]pieces;
	
	public Board(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	//Criar um metodo Board.Piece (row, column) e Board.Pieces(position) = sobrecarga
	
	public Piece piece(int row, int column) {
		return pieces [row][column];
	}
	
	//Sobrecarga - Retorna a peça pela posição
	public Piece piece(Position position) {
		return pieces[position.getRow()][position.getColumn()];
	}
	
	
}
