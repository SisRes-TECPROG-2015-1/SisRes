package test.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Room;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import persistence.FactoryConnection;
import persistence.ClassRoomDAO;
import exception.PatrimonyException;


public class SalaDAOTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	
	
	@Test
	public void testInstance() {
		assertTrue("Instanciando SalaDAO", ClassRoomDAO.getInstance() instanceof ClassRoomDAO);
	}
	@Test
	public void testSingleton() {
		ClassRoomDAO inst1 = ClassRoomDAO.getInstance();
		ClassRoomDAO inst2 = ClassRoomDAO.getInstance();
		assertSame("Testando o Padrao Singleton", inst2, inst1);
	}
	

	@Test
	public void testIncluir() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		boolean rs = false;
		
		ClassRoomDAO.getInstance().includeARoom(s);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() + "\" and " +
				"sala.capacidade = " + s.getCapacity() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		assertTrue("Testando Inclusao no Banco", rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testIncluirNulo() throws PatrimonyException, SQLException {
		ClassRoomDAO.getInstance().includeARoom(null);
	}
	@Test (expected= PatrimonyException.class)
	public void testIncluirCodigoExistente() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Room s2 = new Room("CodigoInc", "Descricao Dois", "200");
		boolean rs = false;
		
		ClassRoomDAO.getInstance().includeARoom(s2);
		try{
			ClassRoomDAO.getInstance().includeARoom(s);
		} finally {
			rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
					"sala.codigo = \"" + s.getCode() + "\" and " +
					"sala.descricao = \"" + s.getDescription() + "\" and " +
					"sala.capacidade = " + s.getCapacity() +
					";");
			if(rs)
				this.executaNoBanco("DELETE FROM sala WHERE " +
						"sala.codigo = \"" + s.getCode() + "\" and " +
						"sala.descricao = \"" + s.getDescription() +  "\" and " +
						"sala.capacidade = " + s.getCapacity() + ";");
			this.executaNoBanco("DELETE FROM sala WHERE " +
					"sala.codigo = \"" + s2.getCode() + "\" and " +
					"sala.descricao = \"" + s2.getDescription() +  "\" and " +
					"sala.capacidade = " + s2.getCapacity() + ";");
		}
		assertFalse("Teste de Inclus�o.", rs);
	}
	
	@Test
	public void testAlerar() throws PatrimonyException, SQLException {
		Room oldRoom = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Room newRoom = new Room("CodigoAlt", "Descricao Dois", "200");
		boolean rs = true, rs2 = false;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + oldRoom.getCode() + "\", " +
				"\"" + oldRoom.getDescription() + "\", " +
				oldRoom.getCapacity() + ");");
		
		ClassRoomDAO.getInstance().modifyRoom( oldRoom , newRoom );
		//changeRoomReserve(s, s2);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + oldRoom.getCode() + "\" and " +
				"sala.descricao = \"" + oldRoom.getDescription() + "\" and " +
				"sala.capacidade = " + oldRoom.getCapacity() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + oldRoom.getCode() + "\" and " +
				"sala.descricao = \"" + oldRoom.getDescription() +  "\" and " +
				"sala.capacidade = " + oldRoom.getCapacity() + ";");
		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + newRoom.getCode() + "\" and " +
				"sala.descricao = \"" + newRoom.getDescription() + "\" and " +
				"sala.capacidade = " + newRoom.getCapacity() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + newRoom.getCode() + "\" and " +
				"sala.descricao = \"" + newRoom.getDescription() +  "\" and " +
				"sala.capacidade = " + newRoom.getCapacity() + ";");
		
		assertTrue("Testando Inclusao no Banco", rs2 && !rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarPrimeiroNulo() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoomDAO.getInstance().modifyRoom(null, s);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarSegundoNulo() throws PatrimonyException, SQLException {
		Room room = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoomDAO.getInstance().modifyRoom(room, null);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarNaoExistente() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Room s2 = new Room("CodigoAlt", "Descricao Dois", "200");
		boolean rs2 = true;
		
		try{
			ClassRoomDAO.getInstance().modifyRoom(s, s2);
		} finally {		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() + "\" and " +
				"sala.capacidade = " + s2.getCapacity() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() +  "\" and " +
				"sala.capacidade = " + s2.getCapacity() + ";");
		}
		assertTrue("Testando Inclusao no Banco", !rs2);
	}
	@Ignore // (expected= PatrimonioException.class)
	public void testAletarEvolvidoEmReserva() throws PatrimonyException, SQLException {
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarComMesmoCodigo() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Room s2 = new Room("CodigoAlt", "Descricao Dois", "200");
		Room s3 = new Room("CodigoInc", "Descricao Dois", "200");
		boolean rs = false, rs2 = false, rs3 = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s2.getCode() + "\", " +
				"\"" + s2.getDescription() + "\", " +
				s2.getCapacity() + ");");
		
		try{
			ClassRoomDAO.getInstance().modifyRoom(s, s2);
		} finally {
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() + "\" and " +
				"sala.capacidade = " + s.getCapacity() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() + "\" and " +
				"sala.capacidade = " + s2.getCapacity() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() +  "\" and " +
				"sala.capacidade = " + s2.getCapacity() + ";");
		
		rs3 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s3.getCode() + "\" and " +
				"sala.descricao = \"" + s3.getDescription() + "\" and " +
				"sala.capacidade = " + s3.getCapacity() +
				";");
		if(rs3)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s3.getCode() + "\" and " +
				"sala.descricao = \"" + s3.getDescription() +  "\" and " +
				"sala.capacidade = " + s3.getCapacity() + ";");
		}
		assertTrue("Testando Inclusao no Banco", rs && rs2 && !rs3);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarParaExistente() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoAlt", "Descricao Dois", "200");
		Room s2 = new Room("CodigoAlt", "Descricao Dois", "200");
		boolean rs = false, rs2 = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		try{
			ClassRoomDAO.getInstance().modifyRoom(s, s2);
		} finally {
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() + "\" and " +
				"sala.capacidade = " + s.getCapacity() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() + "\" and " +
				"sala.capacidade = " + s2.getCapacity() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCode() + "\" and " +
				"sala.descricao = \"" + s2.getDescription() +  "\" and " +
				"sala.capacidade = " + s2.getCapacity() + ";");
		}
		assertTrue("Testando Inclusao no Banco", rs && !rs2);
	}
	
	
	@Test
	public void testExcluir() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		boolean rs = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		ClassRoomDAO.getInstance().excludeRoom(s);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() + "\" and " +
				"sala.capacidade = " + s.getCapacity() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		assertTrue("Testando Inclusao no Banco", !rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testExcluirNulo() throws PatrimonyException, SQLException {
		ClassRoomDAO.getInstance().excludeRoom(null);
	}
	@Ignore // (expected= PatrimonioException.class)
	public void testExcluirEnvolvidoEmReserva() throws PatrimonyException, SQLException {
		
	}
	@Test (expected= PatrimonyException.class)
	public void testExcluirNaoExistente() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoomDAO.getInstance().excludeRoom(s);
	}
	
	
	@Test
	public void testBuscarCodigo() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		Vector<Room> vet = ClassRoomDAO.getInstance().searchByCode("CodigoInc");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		assertTrue("Testando Buscar o Vetor de ", vet.size() > 0);
	}
	@Test
	public void testDescricao() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		Vector<Room> vet = ClassRoomDAO.getInstance().searchByDescription("Descricao Da Sala Inclusao");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		assertTrue("Testando Buscar o Vetor de ", vet.size() > 0);
	}
	@Test
	public void testCapacidade() throws PatrimonyException, SQLException {
		Room s = new Room("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCode() + "\", " +
				"\"" + s.getDescription() + "\", " +
				s.getCapacity() + ");");
		
		Vector<Room> vet = ClassRoomDAO.getInstance().searchByCapacity("123");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCode() + "\" and " +
				"sala.descricao = \"" + s.getDescription() +  "\" and " +
				"sala.capacidade = " + s.getCapacity() + ";");
		
		assertTrue("Testando Buscar o Vetor de ", vet.size() > 0);
	}

	
	private void executaNoBanco(String msg) throws SQLException{
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(msg);
		pst.executeUpdate();
		pst.close();
		con.close();
	}
	private boolean estaNoBanco(String query) throws SQLException{
		Connection con = FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(query);
		ResultSet rs = pst.executeQuery();
		
		if(!rs.next())
		{
			rs.close();
			pst.close();
			con.close();
			return false;
		}
		else {
			rs.close();
			pst.close();
			con.close();
			return true;
		}
	}
	
}
