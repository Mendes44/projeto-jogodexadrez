package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knigth;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	// # Coração do Jogo de Xadrez
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
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
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
		
		ChessPiece movePiece = (ChessPiece)board.piece(target);
		
		// # Movimento Especial - PROMOÇÃO
		promoted = null;
		if(movePiece instanceof Pawn) {
			if((movePiece.getColor() == Color.WHITE && target.getRow() == 0) || (movePiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}
		
		// #Movimento Especial - En Passant
		if (movePiece instanceof Pawn && (target.getRow() == source.getRow() - 2) || (target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movePiece;
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturePiece;//downCasting
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("NAO HA PECA PARA SER PROMOVIDA!!!");
		}
		if (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("Q")) {
			throw new InvalidParameterException("TIPO DE PROMOCAO INVALIDA!!!");
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece (String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("C")) return new Knigth(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
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
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturePiece = board.removePiece(target);
		board.placePiece(p, target);
		
		if (capturePiece != null) {
			piecesOnTheBoard.remove(capturePiece);
			capturedPieces.add(capturePiece);
		}
		
		// # Movimento Especial Roque Pequeno - Lado do Rei
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); //Operação para remover a peça
			board.placePiece(rook, targetT); // Colocando a pela na posição.
			rook.increaseMoveCount();
		}
		
		// # Movimento Especial Roque Grande - Lado da rainha.
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); //Operação para remover a peça
			board.placePiece(rook, targetT); // Colocando a pela na posição.
			rook.increaseMoveCount();
		}
		
		// # Movimento Especial EN PASSANT
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturePiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position (target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position (target.getRow() - 1, target.getColumn());
				}
				capturePiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturePiece);
				piecesOnTheBoard.remove(capturePiece);
			}
		}
		
		return capturePiece;
	}
	
	// Desfazendo o movimento
	private void undoMove(Position source, Position target, Piece capturePiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturePiece != null) {
			board.placePiece(capturePiece, target);
			capturedPieces.remove(capturePiece);
			piecesOnTheBoard.add(capturePiece);
		}
		
		// # Movimento Especial Roque Pequeno - Lado do Rei
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT); // Operação para remover a peça
			board.placePiece(rook, sourceT); // Colocando a pela na posição.
			rook.increaseMoveCount();
		}

		// # Movimento Especial Roque Grande - Lado da rainha.
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT); 
			rook.decreaseMoveCount();
		}
		
		// # Movimento Especial EN PASSANT - DESFAZER
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturePiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
				pawnPosition = new Position (3, target.getColumn());
				}
				else {
					pawnPosition = new Position (4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);		
			}
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
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knigth(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knigth(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knigth(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knigth(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
               
	}
}
