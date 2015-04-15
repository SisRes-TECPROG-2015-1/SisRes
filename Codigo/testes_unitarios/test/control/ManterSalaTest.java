package test.control;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.FactoryConnection;
import control.MaintainRoom;
import model.Sala;
import exception.PatrimonyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;


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
		Sala sala_new = new Sala("codigo", "descricao", "2");
		MaintainRoom.getInstance().inserir("codigo", "descricao", "2");
		assertNotNull("Falha ao inserir", this.procurarNoVetor(sala_new));
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + sala_new.getCodigo() + "\" and " +
				"sala.descricao = \"" + sala_new.getDescricao() +  "\" and " +
				"sala.capacidade = " + sala_new.getCapacidade() + ";"
				);
	}

	@Test
	public void testAlterar() throws PatrimonyException, SQLException {
		Sala sala = new Sala("codigo_old", "descricao", "1");
		Sala sala_new = new Sala("codigo", "descricao", "2");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + sala.getCodigo() + "\", " +
				"\"" + sala.getDescricao() + "\", " +
				"" + sala.getCapacidade() + "); "
				);
		MaintainRoom.getInstance().alterar("codigo", "descricao", "2", sala);
		
		assertNotNull("Falha ao alterar", this.procurarNoVetor(sala_new));
		
		this.executaNoBanco("DELETE FROM sala WHERE " +
				"sala.codigo = \"" + sala_new.getCodigo() + "\" and " +
				"sala.descricao = \"" + sala_new.getDescricao() +  "\" and " +
				"sala.capacidade = " + sala_new.getCapacidade() + ";"
				);
	}

	@Test
	public void testExcluir() throws SQLException, PatrimonyException {
		Sala sala = new Sala("codigo_old", "descricao", "1");
		
		this.executaNoBanco("INSERT INTO " +
				"sala (codigo, descricao, capacidade) VALUES (" +
				"\"" + sala.getCodigo() + "\", " +
				"\"" + sala.getDescricao() + "\", " +
				"" + sala.getCapacidade() + "); "
				);
		
		MaintainRoom.getInstance().excludeRoom(sala);
		
		assertNull("Falha ao excluir", this.procurarNoVetor(sala));
	}

	public Sala procurarNoVetor(Sala teste) throws PatrimonyException, SQLException {
		Vector<Sala> todos = MaintainRoom.getInstance().getSalas_vet();
		Iterator<Sala> i = todos.iterator();
		while(i.hasNext()){
			Sala e = i.next();
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
