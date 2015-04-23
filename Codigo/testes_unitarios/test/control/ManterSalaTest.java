package test.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Room;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.FactoryConnection;
import control.MaintainRoom;
import exception.PatrimonyException;


public class ManterSalaTest {
	
	@BeforeClass
	public static void setUpClass(){
		
	}

	@AfterClass
	public static void tearDownClass(){
	}
	
	@Test
	public void testGetInstance() {
		assertTrue("Verifica metodo getInstance().", MaintainRoom.getInstance() instanceof MaintainRoom);
	}
	
	@Test
	public void testSingleton() {
		MaintainRoom p = MaintainRoom.getInstance();
		MaintainRoom q = MaintainRoom.getInstance();
		assertSame("Testando o Padrao Singleton", p, q);
	}


	@Test
	public void testInserir() throws PatrimonyException, SQLException {
		Room sala_new = new Room("codigo", "descricao", "2");
		MaintainRoom.getInstance().insertRooms("codigo", "descricao", "2");
		assertNotNull("Falha ao inserir", this.procurarNoVetor(sala_new));
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + sala_new.getCode() + "\" and " +
				"sala.descricao = \"" + sala_new.getDescription() +  "\" and " +
				"sala.capacidade = " + sala_new.getCapacity() + ";"
				);
	}

	@Test
	public void testAlterar() throws PatrimonyException, SQLException {
		Room sala = new Room("codigo_old", "descricao", "1");
		Room sala_new = new Room("codigo", "descricao", "2");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + sala.getCode() + "\", " +
				"\"" + sala.getDescription() + "\", " +
				"" + sala.getCapacity() + "); "
				);
		MaintainRoom.getInstance().changeRoom("codigo", "descricao", "2", sala);
		
		assertNotNull("Falha ao alterar", this.procurarNoVetor(sala_new));
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + sala_new.getCode() + "\" and " +
				"sala.descricao = \"" + sala_new.getDescription() +  "\" and " +
				"sala.capacidade = " + sala_new.getCapacity() + ";"
				);
	}

	@Test
	public void testExcluir() throws SQLException, PatrimonyException {
		Room sala = new Room("codigo_old", "descricao", "1");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + sala.getCode() + "\", " +
				"\"" + sala.getDescription() + "\", " +
				"" + sala.getCapacity() + "); "
				);
		
		MaintainRoom.getInstance().excludeRoom(sala);
		
		assertNull("Falha ao excluir", this.procurarNoVetor(sala));
	}

	public Room procurarNoVetor(Room teste) throws PatrimonyException, SQLException {
		Vector<Room> todos = MaintainRoom.getInstance().getRooms();
		Iterator<Room> i = todos.iterator();
		while(i.hasNext()){
			Room e = i.next();
			if(e.equals(teste))
				return e;			
		}
		return null;
	}
	
	private void executaNoBanco(String msg) throws SQLException{
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(msg);
		pst.executeUpdate();
		pst.close();
		con.close();
	}
	
}
