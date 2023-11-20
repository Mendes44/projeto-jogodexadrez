package boardgame;

public class Board {

	private int rows;
	private int columns;
	private Piece[][]pieces;
	
	public Board(int rows, int columns) {
		if (rows < 1 || columns < 1 ) {
			throw new BoardException("!!! ERRO CRIANDO TABULEIRO!!! -> E NECESSARIO QUE HAJA PELO MENOS 1 LINHA E 1 COLUNA. ");
		}
		
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
	
	//Criar um metodo Board.Piece (row, column) e Board.Pieces(position)
	//PROGRAMAÇÃO DEFENSIVA - TESTAR PARA VER SE NÃO EXISTE PEÇA NA MESMA POSIÇÃO.
	public Piece piece(int row, int column) {
		if (!positionExists(row,column)) {
			throw new BoardException(">>> POSIÇAO NAO EXISTE NO TABULEIRO <<<");
		}
		return pieces [row][column];
	}
	
	//Sobrecarga - Retorna a peça pela posição - Para pegar as peças na posição na matriz.
	public Piece piece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException(">>> POSIÇÃO NÃO EXISTE NO TABULEIRO <<<");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	public void placePiece (Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardException(">>> JA EXISTE UMA PEÇA NA POSIÇÃO -> " 
		+ "LINHA:" + position.getRow()+ "--COLUNA:" + position.getColumn());
		}
		pieces [position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	public Piece removePiece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException(">>> POSIÇÃO NÃO EXISTE NO TABULEIRO <<<");
		}
		if (piece(position) == null) {
			return null;
		}
		Piece aux = piece(position);//Caso não esteja nulo, criei uma variavel aux para indicar que vai estar nulo.
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;//Aqui afirmo que não tem mais peça nessa posição.
		return aux;
	}
	
	//PARA SABER SE JA EXISTE UMA PEÇA NO TABULEIRO.
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(),position.getColumn());//Estou buscando a posição do meu metodo auxiliar.
	}
	
	public boolean thereIsAPiece (Position position) {
		if (!positionExists(position)) {
			throw new BoardException(">>> POSIÇÃO NÃO EXISTE NO TABULEIRO <<<");
		}
		return piece(position) != null;
	}
}
