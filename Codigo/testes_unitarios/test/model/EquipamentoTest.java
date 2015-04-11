package test.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Equipamento;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.PatrimonyException;

/**
 * Testa Patrimonio, de modo indireto por causa da Heranca de Equipamento
 * */

public class EquipamentoTest {

	Equipamento eq;
	
	@Before
	public void setUp() throws PatrimonyException{
		eq = new Equipamento("codigo", "descricao");
	}
	
	@After
	public void tearDown() throws PatrimonyException{
		eq = null;
	}
	
	@Test
	public void testInstance() throws PatrimonyException {
		assertTrue(eq instanceof Equipamento);
	}
	
	@Test
	public void testNome() throws PatrimonyException {
		assertTrue("codigo diferente instanciado", "codigo" == eq.getCodigo());
	}
	
	@Test
	public void testDescricao() throws PatrimonyException {
		assertTrue("Descricao diferente instanciada", "descricao" == eq.getDescricao());
	}
	
	@Test
	public void testEquals() throws PatrimonyException {
		Equipamento eq2 = new Equipamento("codigo", "descricao");
		assertTrue("Equipamentos deviam ser iguais", eq.equals(eq2));
	}
	
	@Test
	public void testEqualsCodigoDiferente() throws PatrimonyException {
		Equipamento eq2 = new Equipamento("codigo diferente", "descricao");
		assertFalse("Equipamentos deviam ser diferentes", eq.equals(eq2));
	}
	
	@Test
	public void testEqualsDescricaoDiferente() throws PatrimonyException {
		Equipamento eq2 = new Equipamento("codigo", "descricao diferente");
		assertFalse("Equipamentos deviam ser diferentes", eq.equals(eq2));
	}
	
	@Test(expected = PatrimonyException.class)
	public void testDescricaoVazia() throws PatrimonyException {
		new Equipamento("abc", "");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testCodigoVazio() throws PatrimonyException {
		new Equipamento("", "abc");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testCodigoNulo() throws PatrimonyException {
		new Equipamento(null, "abc");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testDescricaoNulo() throws PatrimonyException {
		new Equipamento("abc", null);
	}

}
