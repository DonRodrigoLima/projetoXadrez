package xadrez;

import tabuleiro.Tabuleiro;
import xadrez.pecadexadrez.Rei;
import xadrez.pecadexadrez.Torre;

public class PartidaDeXadrez {
	
	private Tabuleiro tabuleiro;
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		setupInicial();
	}
	
	public PecaXadrez[][] getPeca(){
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()] [tabuleiro.getColunas()];
		for(int i = 0; i < tabuleiro.getLinhas(); i++) {
			for(int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.pecas(i, j);
			}
		}
		return mat;
	}
	
	private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		
	}
	
	private void setupInicial() {
		colocarNovaPeca('b' , 6, new Torre(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('e' , 8, new Rei(tabuleiro, Cores.PRETO));
		colocarNovaPeca('e' , 1, new Rei(tabuleiro, Cores.BRANCO));
	}
}
