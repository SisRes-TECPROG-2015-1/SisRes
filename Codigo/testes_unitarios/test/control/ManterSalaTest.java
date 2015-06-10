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
		Room room_new = new Room("code", "description", "2");
		MaintainRoom.getInstance().insertRooms("code", "description", "2");
		assertNotNull("Falha ao inserir", this.procurarNoVetor(room_new));
		this.executaNoBanco("DELETE FROM room WHERE " +
				"room.code = \"" + room_new.getCode() + "\" and " +
				"room.description = \"" + room_new.getDescription() +  "\" and " +
				"room.capacity = " + room_new.getCapacity() + ";"
				);
	}

	@Test
	public void testAlterar() throws PatrimonyException, SQLException {
		Room room = new Room("code_old", "description", "1");
		Room room_new = new Room("code", "description", "2");
		
		this.executaNoBanco("INSERT INTO " +
				"room (code, description, capacity) VALUES (" +
				"\"" + room.getCode() + "\", " +
				"\"" + room.getDescription() + "\", " +
				"" + room.getCapacity() + "); "
				);
		MaintainRoom.getInstance().changeRoom("code", "description", "2", room);
		
		assertNotNull("Falha ao alterar", this.procurarNoVetor(room_new));
		
		this.executaNoBanco("DELETE FROM room WHERE " +
				"room.code = \"" + room_new.getCode() + "\" and " +
				"room.description = \"" + room_new.getDescription() +  "\" and " +
				"room.capacity = " + room_new.getCapacity() + ";"
				);
	}

	@Test
	public void testExcluir() throws SQLException, PatrimonyException {
		Room room = new Room("code_old", "description", "1");
		
		this.executaNoBanco("INSERT INTO " +
				"room (code, description, capacity) VALUES (" +
				"\"" + room.getCode() + "\", " +
				"\"" + room.getDescription() + "\", " +
				"" + room.getCapacity() + "); "
				);
		
		MaintainRoom.getInstance().excludeRoom(room);
		
		assertNull("Falha ao excluir", this.procurarNoVetor(room));
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
