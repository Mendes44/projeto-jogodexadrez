package boardgame;

public abstract class Piece {

	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null; //Não e necessario colocar pq o java ja joga, estou colocando para lembrar.
	}

	//Não vou permitir que o tabuleiro seja alterado, ai tira o set.
	//Protected = pois somente classes dentro do mesmo pacote e subclasses vão pode acessar.
	protected Board getBoard() {
		return board;
	}

	public abstract boolean [][] possibleMoves();//Classe abstrata
	
	//gancho do metodo contrato com a abstrata
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}

	public boolean isThereAnyPossibleMove() {
		boolean[][] mat = possibleMoves();
		for (int i=0; i<mat.length; i++) {
			for(int j=0; j<mat.length;j++) {
				if(mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}
	
	
}
