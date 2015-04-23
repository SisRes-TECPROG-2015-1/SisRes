package test.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Equipment;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import control.MaintainEquipment;
import exception.PatrimonyException;

public class ManterEquipamentoTest {

	static MaintainEquipment instance;
	Vector<Equipment> todos;
	Equipment e;
 
	public ManterEquipamentoTest() {
	}

	@BeforeClass
	public static void setUpClass() throws PatrimonyException {
		instance = MaintainEquipment.getInstance();
	}

	@AfterClass
	public static void tearDownClass() {
		instance = null;
	}

	@Before
	public void setUp() throws Exception {
		e = new Equipment("codigo", "descricao");
		instance.insertNewEquipment("codigo","descricao");
		todos = instance.getEquipments();
	}

	@After
	public void tearDown() throws SQLException, PatrimonyException {
		todos = instance.getEquipments();
		Iterator<Equipment> i = todos.iterator();
		while(i.hasNext()){
			e = i.next();
			instance.excludeEquipment(e);
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
		MaintainEquipment me = MaintainEquipment.getInstance();
		assertSame("Instancias diferentes", me, instance);
		
	}

	@Test
	public void testIncluirVet() throws SQLException, PatrimonyException {
		assertNotNull("Teste de Inclusao no Equipamento Vet.", procurarNoVetor(e));
	}
	
	@Test
	public void testAlterarVet() throws SQLException, PatrimonyException {
		instance.changeEquipmentsAttributes("codigo alterado", "descricao alterarda", e);
		Equipment e2 = new Equipment("codigo alterado", "descricao alterarda");
		assertNotNull("Teste de Inclusao no Equipamento Vet.", procurarNoVetor(e2));
	}
	
	@Test(expected = PatrimonyException.class)
	public void testAlterarNaoExistente() throws SQLException, PatrimonyException {
		Equipment eq = new Equipment("codigo", "nao existe");
		instance.changeEquipmentsAttributes("codigo alterado", "descricao alterarda", eq);
	}
	
	@Test(expected = PatrimonyException.class)
	public void testAlterarNull() throws SQLException, PatrimonyException {
		instance.changeEquipmentsAttributes("codigo alterado", "descricao alterarda", null);
	}
	
	@Test (expected = PatrimonyException.class)
	public void testExcluirNull() throws SQLException, PatrimonyException {
		e = null;
		instance.excludeEquipment(e);
	}
	
	public Equipment procurarNoVetor(Equipment teste) throws PatrimonyException, SQLException {
		todos = instance.getEquipments();
		Iterator<Equipment> i = todos.iterator();
		while(i.hasNext()){
			Equipment e = i.next();
			if(e.equals(teste))
				return e;			
		}
		return null;
	}
	
}
