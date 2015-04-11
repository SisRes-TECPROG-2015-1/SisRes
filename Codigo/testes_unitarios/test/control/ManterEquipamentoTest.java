package test.control;

import control.ManterEquipamento;
import exception.PatrimonyException;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import model.Equipamento;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ManterEquipamentoTest {

	static ManterEquipamento instance;
	Vector<Equipamento> todos;
	Equipamento e;
 
	public ManterEquipamentoTest() {
	}

	@BeforeClass
	public static void setUpClass() throws PatrimonyException {
		instance = ManterEquipamento.getInstance();
	}

	@AfterClass
	public static void tearDownClass() {
		instance = null;
	}

	@Before
	public void setUp() throws Exception {
		e = new Equipamento("codigo", "descricao");
		instance.inserir("codigo","descricao");
		todos = instance.getEquipamento_vet();
	}

	@After
	public void tearDown() throws SQLException, PatrimonyException {
		todos = instance.getEquipamento_vet();
		Iterator<Equipamento> i = todos.iterator();
		while(i.hasNext()){
			e = i.next();
			instance.excluir(e);
		}
		e = null;
	}
	
	@Test
	public void testGetEquipamento_vet() throws Exception {
		assertNotNull(todos);
	}
	
	@Test
	public void testGetInstance() {
		assertNotNull("Get Instance falhou",instance);
	}
	
	@Test
	public void testSingleton(){
		ManterEquipamento me = ManterEquipamento.getInstance();
		assertSame("Instancias diferentes", me, instance);
		
	}

	@Test
	public void testIncluirVet() throws SQLException, PatrimonyException {
		assertNotNull("Teste de Inclusao no Equipamento Vet.", procurarNoVetor(e));
	}
	
	@Test
	public void testAlterarVet() throws SQLException, PatrimonyException {
		instance.alterar("codigo alterado", "descricao alterarda", e);
		Equipamento e2 = new Equipamento("codigo alterado", "descricao alterarda");
		assertNotNull("Teste de Inclusao no Equipamento Vet.", procurarNoVetor(e2));
	}
	
	@Test(expected = PatrimonyException.class)
	public void testAlterarNaoExistente() throws SQLException, PatrimonyException {
		Equipamento eq = new Equipamento("codigo", "nao existe");
		instance.alterar("codigo alterado", "descricao alterarda", eq);
	}
	
	@Test(expected = PatrimonyException.class)
	public void testAlterarNull() throws SQLException, PatrimonyException {
		instance.alterar("codigo alterado", "descricao alterarda", null);
	}
	
	@Test (expected = PatrimonyException.class)
	public void testExcluirNull() throws SQLException, PatrimonyException {
		e = null;
		instance.excluir(e);
	}
	
	public Equipamento procurarNoVetor(Equipamento teste) throws PatrimonyException, SQLException {
		todos = instance.getEquipamento_vet();
		Iterator<Equipamento> i = todos.iterator();
		while(i.hasNext()){
			Equipamento e = i.next();
			if(e.equals(teste))
				return e;			
		}
		return null;
	}
	
}
