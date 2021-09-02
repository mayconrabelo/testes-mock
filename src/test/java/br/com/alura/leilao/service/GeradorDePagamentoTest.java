package br.com.alura.leilao.service;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {
	
	private GeradorDePagamento gerador;
	
	@Mock
	private PagamentoDao pagamentoDao;
	
	@Captor
	private ArgumentCaptor<Pagamento> captor;
	
	@Mock
	private Clock clock;

	
	@BeforeEach
	public void beforEach() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamentoDao,clock);
	}

	@Test
	void deveriaCriarPagamentoParaVencedorDoLeilao() {
		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();
		
		LocalDate data = LocalDate.of(2021, 8 , 30);
		
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		gerador.gerarPagamento(vencedor);
		
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		
		Pagamento pagamento = captor.getValue();
		
		assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		assertEquals(vencedor.getValor(), pagamento.getValor());
		assertFalse(pagamento.getPago());
		assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
		assertEquals(leilao, pagamento.getLeilao());
	}
	
	@Test
	void deveriaCriarPagamentoParaVencedorDoLeilaoNaSegundaFeira() {
		Leilao leilao = leilao();
		Lance vencedor = leilao.getLanceVencedor();
		
		LocalDate data = LocalDate.of(2021, 9 , 3);
		
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		gerador.gerarPagamento(vencedor);
		
		Mockito.verify(pagamentoDao).salvar(captor.capture());
		
		Pagamento pagamento = captor.getValue();
		
		assertEquals(LocalDate.now().plusDays(7), pagamento.getVencimento());
		assertEquals(vencedor.getValor(), pagamento.getValor());
		assertFalse(pagamento.getPago());
		assertEquals(vencedor.getUsuario(), pagamento.getUsuario());
		assertEquals(leilao, pagamento.getLeilao());
	}
	
	private Leilao leilao(){
		Leilao leilao = new Leilao("celular",new BigDecimal("500"),new Usuario("fulano"));
		Lance lance = new Lance(new Usuario("cicrano"),new BigDecimal("900"));
		
		leilao.propoe(lance);
		leilao.setLanceVencedor(lance);
		return leilao;
		
	}

}
