package test.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.Equipment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exception.PatrimonyException;

/**
 * Testa Patrimonio, de modo indireto por causa da Heranca de Equipment
 * */

public class EquipamentoTest {

	Equipment eq;
	
	@Before
	public void setUp() throws PatrimonyException{
		eq = new Equipment("codigo", "descricao");
	}
	
	@After
	public void tearDown() throws PatrimonyException{
		eq = null;
	}
	
	@Test
	public void testInstance() throws PatrimonyException {
		assertTrue(eq instanceof Equipment);
	}
	
	@Test
	public void testNome() throws PatrimonyException {
		assertTrue("codigo diferente instanciado", "codigo" == eq.getCode());
	}
	
	@Test
	public void testDescricao() throws PatrimonyException {
		assertTrue("Descricao diferente instanciada", "descricao" == eq.getDescription());
	}
	
	@Test
	public void testEquals() throws PatrimonyException {
		Equipment eq2 = new Equipment("codigo", "descricao");
		assertTrue("Equipments deviam ser iguais", eq.equals(eq2));
	}
	
	@Test
	public void testEqualsCodigoDiferente() throws PatrimonyException {
		Equipment eq2 = new Equipment("codigo diferente", "descricao");
		assertFalse("Equipments deviam ser diferentes", eq.equals(eq2));
	}
	
	@Test
	public void testEqualsDescricaoDiferente() throws PatrimonyException {
		Equipment eq2 = new Equipment("codigo", "descricao diferente");
		assertFalse("Equipments deviam ser diferentes", eq.equals(eq2));
	}
	
	@Test(expected = PatrimonyException.class)
	public void testDescricaoVazia() throws PatrimonyException {
		new Equipment("abc", "");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testCodigoVazio() throws PatrimonyException {
		new Equipment("", "abc");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testCodigoNulo() throws PatrimonyException {
		new Equipment(null, "abc");
	}
	
	@Test(expected = PatrimonyException.class)
	public void testDescricaoNulo() throws PatrimonyException {
		new Equipment("abc", null);
	}

}
