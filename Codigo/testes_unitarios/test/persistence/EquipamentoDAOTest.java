package test.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Equipamento;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.EquipmentDAO;
import exception.PatrimonyException;


public class EquipamentoDAOTest {
	static EquipmentDAO instance;
	Equipamento antigo, novo;
	Vector <Equipamento> todos;
	
	@BeforeClass
	public static void setUpClass() throws PatrimonyException, SQLException {
		instance = EquipmentDAO.getInstance();
	}
	
	@AfterClass
	public static void tearDownClass() throws SQLException, PatrimonyException {
		instance = null;
	}
	
	@Before
	public void setUp() throws PatrimonyException, SQLException {
		 antigo = new Equipamento("codigo", "descricao - antigo");
		 novo = new Equipamento("codigo", "descricao - alterada");
		 instance.includeReserve(antigo);
		 todos = instance.searchForAll();
	}
	
	@After
	public void tearDown() throws SQLException, PatrimonyException {
		todos = instance.searchForAll();
		Iterator<Equipamento> i = todos.iterator();
		while(i.hasNext()){
			Equipamento e = i.next();
			instance.excludeRoom(e);
		}
		antigo = null;
		novo = null;
	}
	
	@Test
	public void testInstance() {
		assertTrue("Instanciando EquipamentoDAO", instance instanceof EquipmentDAO);
	}
	
	@Test
	public void testSingleton() {
		EquipmentDAO inst1 = EquipmentDAO.getInstance();
		EquipmentDAO inst2 = EquipmentDAO.getInstance();
		assertSame("Testando o Padrao Singleton", inst2, inst1);
	}
	
	@Test
	public void testIncluir() throws PatrimonyException, SQLException {
		assertNotNull("Equipamento nao foi incluido", procurarNoVetor(antigo));
	}
	@Test
	public void testBuscarTodos() throws SQLException, PatrimonyException {
		assertNotNull("Testando a busca de elementos no BD.", todos);
	}
	
	@Test
	public void testBuscarPorCodigo() throws SQLException, PatrimonyException {
		assertNotNull("Testando a busca por codigo de elementos no BD.", instance.searchByCode(antigo.getCodigo()));
	}
	
	@Test
	public void testBuscarPorDescricao() throws SQLException, PatrimonyException {
		assertNotNull("Testando a busca por descricao de elementos no BD.", instance.searchByDescription(antigo.getDescricao()));
	}
	
	@Test
	public void testBuscarPorCodigoNull() throws SQLException, PatrimonyException {
		assertTrue("Testando a busca por codigo nulo de elementos no BD.", instance.searchByCode(null).isEmpty());
	}
	
	@Test
	public void testBuscarPorDescricaoNull() throws SQLException, PatrimonyException {
		assertTrue("Testando a busca por descricao nula de elementos no BD.", instance.searchByDescription(null).isEmpty());
	}
	
	@Test
	public void testAlterar() throws PatrimonyException, SQLException {
		instance.changeRoomReserve(antigo, novo);
		Equipamento e = procurarNoVetor(antigo);
		assertNull("Equipamento nao foi alterado", e);
		assertNotNull("Equipamento nao foi alterado", procurarNoVetor(novo));
	}
	
	@Test (expected= PatrimonyException.class)
	public void testIncluirComCodigoExistente() throws PatrimonyException, SQLException {
		instance.includeReserve(antigo);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testIncluirNulo() throws PatrimonyException, SQLException {
		instance.incluir(null);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testAlterarNull() throws PatrimonyException, SQLException {
		instance.modifyEquipment(null, null);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testAlterarSegundoNull() throws PatrimonyException, SQLException {
		instance.changeRoomReserve(antigo, null);
	}
	
	
	@Test (expected= PatrimonyException.class)
	public void testAlterarNaoExistente() throws PatrimonyException, SQLException {
		Equipamento equip = new Equipamento("codigo", "eqpt nao existente");
		Equipamento equipAlter = new Equipamento("codigo", "eqpt nao existente alteraddo");
		instance.changeRoomReserve(equip, equipAlter);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testAlterarIgual() throws PatrimonyException, SQLException {
		instance.changeRoomReserve(novo, novo);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testAlterarParaOutroEquipamento() throws PatrimonyException, SQLException {
		Equipamento e = new Equipamento("novo", "teste Alterar para outro");
		instance.includeReserve(e);
		instance.changeRoomReserve(e, novo);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testExcluirNull() throws PatrimonyException, SQLException {
		instance.exludeEquipment(null);
	}
	
	@Test (expected= PatrimonyException.class)
	public void testExcluirNaoExistente() throws PatrimonyException, SQLException {
		Equipamento eq = new Equipamento("codigo"," nao existe descricao");
		instance.excludeRoom(eq);
	}
	
	@Test
	public void testExcluirExistente() throws PatrimonyException, SQLException {
		Equipamento novoExclusao = new Equipamento("cdg", "teste exclusao");
		instance.includeReserve(novoExclusao);
		instance.excludeRoom(novoExclusao);
		assertNull("Equipamento nao foi alterado", procurarNoVetor(novoExclusao));
	}
	
	public Equipamento procurarNoVetor(Equipamento teste) throws PatrimonyException, SQLException {
		todos = instance.searchForAll();
		Iterator<Equipamento> i = todos.iterator();
		while(i.hasNext()){
			Equipamento e = i.next();
			if(e.equals(teste))
				return e;			
		}
		return null;
	}
	
}
