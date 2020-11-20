package xadrez;

import tabuleiro.Posicao;
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
	
	private void setupInicial() {
		tabuleiro.colocaPeca(new Torre(tabuleiro, Cores.BRANCO), new Posicao(2, 1));
		tabuleiro.colocaPeca(new Rei(tabuleiro, Cores.PRETO), new Posicao(0, 4));
		tabuleiro.colocaPeca(new Rei(tabuleiro, Cores.BRANCO), new Posicao(7, 4));
	}
}
