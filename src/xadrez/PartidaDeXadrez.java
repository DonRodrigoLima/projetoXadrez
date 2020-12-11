package xadrez;

import java.util.ArrayList;
import java.util.List;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecadexadrez.Rei;
import xadrez.pecadexadrez.Torre;

public class PartidaDeXadrez {
	
	private int turno;
	private Cores jogadorAtual;
	private Tabuleiro tabuleiro;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cores.BRANCO;
		setupInicial();
	}
	
	public int getTurno() {
		return turno;
	}


	public Cores getJogadorAtual() {
		return jogadorAtual;
	}

	
	public PecaXadrez[][] getPeca(){
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()] [tabuleiro.getColunas()];
		for(int i = 0; i < tabuleiro.getLinhas(); i++) {
			for(int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] movimentosPossiveis(PosicaoXadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	} 
	
	public PecaXadrez performarMovimentoPeca(PosicaoXadrez posicaoOrigem, PosicaoXadrez posicaoDestino) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao destino = posicaoDestino.toPosicao();
		validarPosicaoOrigem(origem);
		validarPosicaoAlvo(origem,destino);
		Peca pecaCapturada = facaMovimento(origem, destino);
		proximoTurno();
		return (PecaXadrez)pecaCapturada;
		
	}
	
	private Peca facaMovimento(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removePeca(origem);
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocaPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		return pecaCapturada;
	}
	
	private void validarPosicaoOrigem(Posicao posicao) {
		if(!tabuleiro.haUmaPeca(posicao)) {
			throw new XadrezException("Nao existe peça na posicao de origem");
		}
		if(jogadorAtual != ((PecaXadrez)tabuleiro.peca(posicao)).getCor()){
			throw new XadrezException("A peca escolhida nao eh a sua");
		}
		
		if(!tabuleiro.peca(posicao).haAlgumMovimentoPossivel()) {
			throw new XadrezException("Nao existe movimentos possiveis para a peca escolhida");
		}
	}
	
	private void validarPosicaoAlvo(Posicao origem, Posicao destino) {
		if(!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezException("A peca escolhida nao pode se mover para a posicao destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cores.BRANCO) ? Cores.PRETO : Cores.BRANCO;
	}
	
	private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
		
	}
	
	private void setupInicial() {
		colocarNovaPeca('c', 1, new Torre(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('c', 2, new Torre(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('d', 2, new Torre(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('e', 2, new Torre(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('e', 1, new Torre(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('d', 1, new Rei(tabuleiro, Cores.BRANCO));

        colocarNovaPeca('c', 7, new Torre(tabuleiro, Cores.PRETO));
        colocarNovaPeca('c', 8, new Torre(tabuleiro, Cores.PRETO));
        colocarNovaPeca('d', 7, new Torre(tabuleiro, Cores.PRETO));
        colocarNovaPeca('e', 7, new Torre(tabuleiro, Cores.PRETO));
        colocarNovaPeca('e', 8, new Torre(tabuleiro, Cores.PRETO));
        colocarNovaPeca('d', 8, new Rei(tabuleiro, Cores.PRETO));
	}


}
