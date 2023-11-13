package chess;

import boardgame.Board;
import boardgame.Piece;
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
	
	public ChessPiece perfomChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		//Primeiro vou converto soucerPosition e targetPosition para posições para matriz
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		//Agora antes de fazer a movimentação tenho que validar a posição
		validateSourcePosition(source);
		Piece capturePiece = makeMove(source, target);//Operação responsavel por realizar o movimento da peça.
		return (ChessPiece)capturePiece;//downCasting
	}
	
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException(">>> NÃO EXISTE PEÇA NA POSIÇÃO DE ORIGEM <<<");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("NAO EXISTE MOVIMENTOS POSSIVEIS PARA PECA ESCOLHIDA!!!");
		}
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);
		Piece capturePiece = board.removePiece(target);
		board.placePiece(p, target);
		return capturePiece;
	}
	
	/*Criar um metodo para receber as coordenadas do xadrez
	 * Onde uso o .toPosition para fazer a operação de colocar 
	 * a peça passando a posição nas coodernadas do xadrez.
	*/
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	//Aqui estou chamando o board para criar minhas peças na tela.
	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
