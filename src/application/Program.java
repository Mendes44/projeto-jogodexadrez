package application;

import java.util.Locale;
import java.util.Scanner;

import boardgame.Board;
import boardgame.Position;

public class Program {

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);
		
		Board board = new Board(8, 8);
		System.out.println("teste");
		
		sc.close();
	}

}
