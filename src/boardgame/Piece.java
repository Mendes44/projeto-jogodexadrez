package boardgame;

public class Piece {

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


	
	
	
}
