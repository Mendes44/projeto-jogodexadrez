package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	//Coração do Jogo de Xadrez
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();//Deve colocar o initial no construtor.
	}
	
	public int getTurn() {
		return turn;
	}
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
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
	
	// Movimentos Possiveis que vai ser validado 
	public boolean[][] possibleMoves(ChessPosition sourcePostion){
		Position position = sourcePostion.toPosition();// converter posição de xadrez para posição de matriz normal
		validateSourcePosition(position);// validação da posição
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece perfomChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		//Primeiro vou converto soucerPosition e targetPosition para posições para matriz
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		//Agora antes de fazer a movimentação tenho que validar a posição
		validateSourcePosition(source);
		validateTargetPosition (source, target);
		Piece capturePiece = makeMove(source, target);//Operação responsavel por realizar o movimento da peça.
		
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturePiece);
			throw new ChessException("VOCE NAO PODE SE COLOCAR EM CHECK!");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}
		return (ChessPiece)capturePiece;//downCasting
	}
	
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException(">>> NÃO EXISTE PEÇA NA POSIÇÃO DE ORIGEM <<<");
		}
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("A PECA ESCOLHIDA NAO E SUA!!!");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("NAO EXISTE MOVIMENTOS POSSIVEIS PARA PECA ESCOLHIDA!!!");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("A PECA ESCOLHIDA NÃO PODE SE MOVER PARA A POSICAO DE DESTINO");
		}
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source);
		Piece capturePiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if (capturePiece != null) {
			piecesOnTheBoard.remove(capturePiece);
			capturedPieces.add(capturePiece);
		}
		
		return capturePiece;
	}
	
	// Desfazendo o movimento
	private void undoMove(Position source, Position target, Piece capturePiece) {
		Piece p = board.removePiece(target);
		board.placePiece(p, source);
		
		if (capturePiece != null) {
			board.placePiece(capturePiece, target);
			capturedPieces.remove(capturePiece);
			piecesOnTheBoard.add(capturePiece);
		}
	}
	
	
	// Incremento de Turno e Troca de Jogador.
	private void nextTurn() {
		turn++;
		// Operação condicial ternaria (IF)    --- ?=então : caso contrario.
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK: Color.WHITE;
	}
	
	public Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("NAO EXISTE O REI DA COR " + color + " NO TABULEIRO!!!");// NÃO PODE ACONTECER ESSE ERRO. NO CASO E PRA ESTOURAR.
	}
	
	// Verificando se o rei esta em Check pela cor.
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i=0; i<board.getRows(); i++) {
				for (int j=0; j<board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position (i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	/*Criar um metodo para receber as coordenadas do xadrez
	 * Onde uso o .toPosition para fazer a operação de colocar 
	 * a peça passando a posição nas coodernadas do xadrez.
	*/
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	//Aqui estou chamando o board para criar minhas peças na tela.
	private void initialSetup() {
		placeNewPiece('h', 7, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
       
        placeNewPiece('b', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 8, new King(board, Color.BLACK));
               
	}
}
