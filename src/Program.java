import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		
		while(true) {
			try {
				UI.clearScreen();
				UI.printMatch(partidaDeXadrez);
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.lerPosicaoXadrez(sc);
				
				boolean[][] movimentosPossiveis = partidaDeXadrez.movimentosPossiveis(origem);
				UI.clearScreen();
				UI.printTabuleiro(partidaDeXadrez.getPeca(), movimentosPossiveis);
				
				System.out.println();
				System.out.print("Destino: ");
				PosicaoXadrez destino = UI.lerPosicaoXadrez(sc);
				
				PecaXadrez pecaCapturada = partidaDeXadrez.performarMovimentoPeca(origem, destino);
			}
			catch(XadrezException e){
				System.out.println(e.getMessage());
				sc.hasNextLine();
			}
			catch(InputMismatchException e){
				System.out.println(e.getMessage());
				sc.hasNextLine();
			}
		}
	}

}
