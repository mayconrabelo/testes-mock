package br.com.alura.leilao.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;


class FinalizarLeilaoServiceTest {

	private FinalizarLeilaoService service;
	
	@Mock
	private LeilaoDao leilaoDao;
	
	@Mock
	private EnviadorDeEmails enviadorDeEmails;
	
	@BeforeEach
	public void beforEach() {
		MockitoAnnotations.initMocks(this);
		this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
	}
	
	
	@Test
	void deveriaFinalizarUmLeilao() {
		List<Leilao> leiloes = leiloes();
		
		Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
		
		service.finalizarLeiloesExpirados();
		
		Leilao leilao = leiloes.get(0);
		
		assertTrue(leilao.isFechado());
		assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());
		
		Mockito.verify(leilaoDao).salvar(leilao);
	}
	
	@Test
	void deveriaEnviarEmailParaVencedorDoLeilao() {
		List<Leilao> leiloes = leiloes();
		
		Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
		
		service.finalizarLeiloesExpirados();
		
		Leilao leilao = leiloes.get(0);
		
		Lance lanceVencedor = leilao.getLanceVencedor();
		
		Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor);
	}
	
	@Test
	void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarOLeilao() {
		List<Leilao> leiloes = leiloes();
		
		Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);
		
		Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);
		
		try {
			service.finalizarLeiloesExpirados();
			Mockito.verifyNoInteractions(enviadorDeEmails);
			
		} catch (Exception e) {}
		
		
		
	}
	
	private List<Leilao> leiloes(){
		List<Leilao> lista = new ArrayList<>();
		
		Leilao leilao = new Leilao("celular",new BigDecimal("500"),new Usuario("fulano"));
		Lance primeiro = new Lance(new Usuario("beltrano"), new BigDecimal("600"));
		Lance segundo = new Lance(new Usuario("cicrano"),new BigDecimal("900"));
		
		leilao.propoe(primeiro);
		leilao.propoe(segundo);
		
		lista.add(leilao);
		
		return lista;
		
	}
}
