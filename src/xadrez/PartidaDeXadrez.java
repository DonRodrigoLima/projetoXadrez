package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecadexadrez.Bispo;
import xadrez.pecadexadrez.Cavalo;
import xadrez.pecadexadrez.Peao;
import xadrez.pecadexadrez.Rainha;
import xadrez.pecadexadrez.Rei;
import xadrez.pecadexadrez.Torre;

public class PartidaDeXadrez {
	
	private int turno;
	private Cores jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaXadrez enPassantVulnerable;
	
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
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public PecaXadrez getEnPassantVulnerable() {
		return enPassantVulnerable;
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
		
		if(testCheck(jogadorAtual)) {
			desfacaMovimento(origem, destino, pecaCapturada);
			throw new XadrezException("Voce nao pode se colocar em check");
		}
		
		PecaXadrez pecaMexida = (PecaXadrez)tabuleiro.peca(destino);
		
		check = (testCheck(oponente(jogadorAtual))) ? true : false;
		
		if(testCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			proximoTurno();
		}
		
		//en peasent
		if(pecaMexida instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2 )) {
			enPassantVulnerable = pecaMexida;
		}
		else
			enPassantVulnerable = null;
		
		return (PecaXadrez)pecaCapturada;
		
	}
	
	private Peca facaMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removePeca(origem);
		p.incrementarContadorMovimento();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocaPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		//rook pequeno
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.incrementarContadorMovimento();
		}
		
		//rook grande
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.incrementarContadorMovimento();
		}
		
		//enpassant
		if(p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if(p.getCor() == Cores.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		
		return pecaCapturada;
	}
	
	private void desfacaMovimento(Posicao origem, Posicao alvo, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removePeca(alvo);
		p.decrementarContadorMovimento();
		tabuleiro.colocaPeca(p, origem);
		
		if(pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, alvo);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		
		//rook pequeno
		if(p instanceof Rei && alvo.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(alvoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.decrementarContadorMovimento();
		}
		
		//rook grande
		if(p instanceof Rei && alvo.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao alvoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(alvoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.decrementarContadorMovimento();
		}
		
		//enpassant
		if(p instanceof Peao) {
			if(origem.getColuna() != alvo.getColuna() && pecaCapturada == enPassantVulnerable) {
				PecaXadrez peao = (PecaXadrez)tabuleiro.removePeca(alvo);
				Posicao posicaoPeao;
				if(p.getCor() == Cores.BRANCO) {
					posicaoPeao = new Posicao(3, alvo.getColuna());
				}
				else {
					posicaoPeao = new Posicao(4, alvo.getColuna());
				}
				tabuleiro.colocaPeca(peao, posicaoPeao);
			}
		}
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
	
	private Cores oponente(Cores cor) {
		return (cor == Cores.BRANCO) ? Cores.PRETO : Cores.BRANCO;
	}
	
	private PecaXadrez rei(Cores cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : list) {
			if(p instanceof Rei) {
				return (PecaXadrez)p;
			}
		}
		throw new IllegalStateException("Nao existe rei " + cor + " no tabuleiro");
	}
	
	private boolean testCheck(Cores cor) {
		Posicao posicaoDoRei = rei(cor).getPosicaoXadrez().toPosicao();
		List<Peca> pecasDoOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
		for(Peca p : pecasDoOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if(mat[posicaoDoRei.getLinha()][posicaoDoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean testCheckMate(Cores cor) {
		if(!testCheck(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for(int i = 0; i < tabuleiro.getLinhas(); i++) {
				for(int j = 0; j < tabuleiro.getColunas(); j++) {
					if(mat[i][j]) {
						Posicao origem = ((PecaXadrez)p).getPosicaoXadrez().toPosicao();
						Posicao destino = new Posicao(i,j);
						Peca pecaCapturada = facaMovimento(origem, destino);
						boolean testCheck = testCheck(cor);
						desfacaMovimento(origem, destino, pecaCapturada);
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
		
	}
	
	private void setupInicial() {
		colocarNovaPeca('a', 1, new Torre(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('b', 1, new Cavalo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('c', 1, new Bispo(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('d', 1, new Rainha(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('e', 1, new Rei(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('f', 1, new Bispo(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('g', 1, new Cavalo(tabuleiro, Cores.BRANCO));
        colocarNovaPeca('h', 1, new Torre(tabuleiro, Cores.BRANCO));
		colocarNovaPeca('a', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('b', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('c', 2, new Peao(tabuleiro, Cores.BRANCO,this));
		colocarNovaPeca('d', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('e', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('f', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('g', 2, new Peao(tabuleiro, Cores.BRANCO,this));
        colocarNovaPeca('h', 2, new Peao(tabuleiro, Cores.BRANCO,this));
    
		colocarNovaPeca('a', 8, new Torre(tabuleiro, Cores.PRETO));
		colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cores.PRETO));
		colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cores.PRETO));
        colocarNovaPeca('e', 8, new Rei(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cores.PRETO));
    	colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cores.PRETO));
        colocarNovaPeca('h', 8, new Torre(tabuleiro, Cores.PRETO));
		colocarNovaPeca('a', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('b', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('c', 7, new Peao(tabuleiro, Cores.PRETO,this));
		colocarNovaPeca('d', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('e', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('f', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('g', 7, new Peao(tabuleiro, Cores.PRETO,this));
        colocarNovaPeca('h', 7, new Peao(tabuleiro, Cores.PRETO,this));
	}


}
