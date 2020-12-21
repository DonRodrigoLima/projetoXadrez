import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partidaDeXadrez = new PartidaDeXadrez();
		List<PecaXadrez> capturadas = new ArrayList<>();
		
		while(!partidaDeXadrez.getCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(partidaDeXadrez, capturadas);
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
				
				if(pecaCapturada != null) {
					capturadas.add(pecaCapturada);
				}
				
				if(partidaDeXadrez.getPromocao() != null) {
					System.out.println("Informe peca para promocao (B/C/T/Q)");
					String tipo = sc.nextLine().toUpperCase();
					while(!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
						System.out.println("Valor invaliddo! Informe novamente peca para promocao (B/C/T/Q)");
						tipo = sc.nextLine().toUpperCase();
					}
					
					partidaDeXadrez.substituirPecaPromovida(tipo);
				}
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
		UI.clearScreen();
		UI.printMatch(partidaDeXadrez, capturadas);
	}

}
