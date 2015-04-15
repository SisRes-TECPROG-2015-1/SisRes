package test.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Sala;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import persistence.FactoryConnection;
import persistence.ClassRoom;
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
		assertTrue("Instanciando SalaDAO", ClassRoom.getInstance() instanceof ClassRoom);
	}
	@Test
	public void testSingleton() {
		ClassRoom inst1 = ClassRoom.getInstance();
		ClassRoom inst2 = ClassRoom.getInstance();
		assertSame("Testando o Padrao Singleton", inst2, inst1);
	}
	

	@Test
	public void testIncluir() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		boolean rs = false;
		
		ClassRoom.getInstance().includeARoom(s);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() + "\" and " +
				"sala.capacidade = " + s.getCapacidade() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		assertTrue("Testando Inclusao no Banco", rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testIncluirNulo() throws PatrimonyException, SQLException {
		ClassRoom.getInstance().includeARoom(null);
	}
	@Test (expected= PatrimonyException.class)
	public void testIncluirCodigoExistente() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Sala s2 = new Sala("CodigoInc", "Descricao Dois", "200");
		boolean rs = false;
		
		ClassRoom.getInstance().includeARoom(s2);
		try{
			ClassRoom.getInstance().includeARoom(s);
		} finally {
			rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
					"sala.codigo = \"" + s.getCodigo() + "\" and " +
					"sala.descricao = \"" + s.getDescricao() + "\" and " +
					"sala.capacidade = " + s.getCapacidade() +
					";");
			if(rs)
				this.executaNoBanco("DELETE FROM sala WHERE " +
						"sala.codigo = \"" + s.getCodigo() + "\" and " +
						"sala.descricao = \"" + s.getDescricao() +  "\" and " +
						"sala.capacidade = " + s.getCapacidade() + ";");
			this.executaNoBanco("DELETE FROM sala WHERE " +
					"sala.codigo = \"" + s2.getCodigo() + "\" and " +
					"sala.descricao = \"" + s2.getDescricao() +  "\" and " +
					"sala.capacidade = " + s2.getCapacidade() + ";");
		}
		assertFalse("Teste de Inclusão.", rs);
	}
	
	@Test
	public void testAlerar() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Sala s2 = new Sala("CodigoAlt", "Descricao Dois", "200");
		boolean rs = true, rs2 = false;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		ClassRoom.getInstance().alterar(s, s2);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() + "\" and " +
				"sala.capacidade = " + s.getCapacidade() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() + "\" and " +
				"sala.capacidade = " + s2.getCapacidade() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() +  "\" and " +
				"sala.capacidade = " + s2.getCapacidade() + ";");
		
		assertTrue("Testando Inclusao no Banco", rs2 && !rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarPrimeiroNulo() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoom.getInstance().alterar(null, s);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarSegundoNulo() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoom.getInstance().alterar(s, null);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarNaoExistente() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Sala s2 = new Sala("CodigoAlt", "Descricao Dois", "200");
		boolean rs2 = true;
		
		try{
			ClassRoom.getInstance().alterar(s, s2);
		} finally {		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() + "\" and " +
				"sala.capacidade = " + s2.getCapacidade() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() +  "\" and " +
				"sala.capacidade = " + s2.getCapacidade() + ";");
		}
		assertTrue("Testando Inclusao no Banco", !rs2);
	}
	@Ignore // (expected= PatrimonioException.class)
	public void testAletarEvolvidoEmReserva() throws PatrimonyException, SQLException {
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarComMesmoCodigo() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		Sala s2 = new Sala("CodigoAlt", "Descricao Dois", "200");
		Sala s3 = new Sala("CodigoInc", "Descricao Dois", "200");
		boolean rs = false, rs2 = false, rs3 = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s2.getCodigo() + "\", " +
				"\"" + s2.getDescricao() + "\", " +
				s2.getCapacidade() + ");");
		
		try{
			ClassRoom.getInstance().alterar(s, s2);
		} finally {
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() + "\" and " +
				"sala.capacidade = " + s.getCapacidade() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() + "\" and " +
				"sala.capacidade = " + s2.getCapacidade() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() +  "\" and " +
				"sala.capacidade = " + s2.getCapacidade() + ";");
		
		rs3 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s3.getCodigo() + "\" and " +
				"sala.descricao = \"" + s3.getDescricao() + "\" and " +
				"sala.capacidade = " + s3.getCapacidade() +
				";");
		if(rs3)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s3.getCodigo() + "\" and " +
				"sala.descricao = \"" + s3.getDescricao() +  "\" and " +
				"sala.capacidade = " + s3.getCapacidade() + ";");
		}
		assertTrue("Testando Inclusao no Banco", rs && rs2 && !rs3);
	}
	@Test (expected= PatrimonyException.class)
	public void testAletarParaExistente() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoAlt", "Descricao Dois", "200");
		Sala s2 = new Sala("CodigoAlt", "Descricao Dois", "200");
		boolean rs = false, rs2 = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		try{
			ClassRoom.getInstance().alterar(s, s2);
		} finally {
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() + "\" and " +
				"sala.capacidade = " + s.getCapacidade() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		rs2 = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() + "\" and " +
				"sala.capacidade = " + s2.getCapacidade() +
				";");
		if(rs2)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s2.getCodigo() + "\" and " +
				"sala.descricao = \"" + s2.getDescricao() +  "\" and " +
				"sala.capacidade = " + s2.getCapacidade() + ";");
		}
		assertTrue("Testando Inclusao no Banco", rs && !rs2);
	}
	
	
	@Test
	public void testExcluir() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		boolean rs = true;
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		ClassRoom.getInstance().excludeRoom(s);
		
		rs = this.estaNoBanco("SELECT * FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() + "\" and " +
				"sala.capacidade = " + s.getCapacidade() +
				";");
		
		if(rs)
			this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		assertTrue("Testando Inclusao no Banco", !rs);
	}
	@Test (expected= PatrimonyException.class)
	public void testExcluirNulo() throws PatrimonyException, SQLException {
		ClassRoom.getInstance().excluir(null);
	}
	@Ignore // (expected= PatrimonioException.class)
	public void testExcluirEnvolvidoEmReserva() throws PatrimonyException, SQLException {
		
	}
	@Test (expected= PatrimonyException.class)
	public void testExcluirNaoExistente() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		ClassRoom.getInstance().excludeRoom(s);
	}
	
	
	@Test
	public void testBuscarCodigo() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		Vector<Sala> vet = ClassRoom.getInstance().searchByCode("CodigoInc");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		assertTrue("Testando Buscar o Vetor de ", vet.size() > 0);
	}
	@Test
	public void testDescricao() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		Vector<Sala> vet = ClassRoom.getInstance().searchByDescription("Descricao Da Sala Inclusao");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
		assertTrue("Testando Buscar o Vetor de ", vet.size() > 0);
	}
	@Test
	public void testCapacidade() throws PatrimonyException, SQLException {
		Sala s = new Sala("CodigoInc", "Descricao Da Sala Inclusao", "123");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + s.getCodigo() + "\", " +
				"\"" + s.getDescricao() + "\", " +
				s.getCapacidade() + ");");
		
		Vector<Sala> vet = ClassRoom.getInstance().searchByCapacity("123");
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + s.getCodigo() + "\" and " +
				"sala.descricao = \"" + s.getDescricao() +  "\" and " +
				"sala.capacidade = " + s.getCapacidade() + ";");
		
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
