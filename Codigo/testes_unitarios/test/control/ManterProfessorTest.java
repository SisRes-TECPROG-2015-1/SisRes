package test.control;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Teacher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.FactoryConnection;


import control.MaintainTeacher;
import exception.ClienteException;

public class ManterProfessorTest {

	private static Vector<Teacher> vet;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vet = MaintainTeacher.getInstance().getProfessores_vet();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testInstance() {
		assertTrue("Teste de Intancia de ManterProfessor", MaintainTeacher.getInstance() instanceof MaintainTeacher);
	}
	
	@Test
	public void testSingleton() {
		MaintainTeacher p = MaintainTeacher.getInstance();
		MaintainTeacher q = MaintainTeacher.getInstance();
		assertSame("Teste Singleton de ManterProfessor", p, q);
	}
	
	
	
	@Test
	public void testInserirVet() throws ClienteException, SQLException {
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		MaintainTeacher.getInstance().inserir("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		
		boolean resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + prof.getNome() + "\" and " +
				"professor.cpf = \"" + prof.getCpf() + "\" and " +
				"professor.telefone = \"" + prof.getTelefone() + "\" and " +
				"professor.email = \"" + prof.getEmail() + "\" and " +
				"professor.matricula = \"" + prof.getMatricula() + "\";");
				
		if(resultado){
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getNome() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getTelefone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getMatricula() + "\";");
		}
		
		Teacher p = vet.lastElement();
		boolean resultado2 = prof.equals(p);
		vet.remove(vet.lastElement());
		assertTrue("Teste de Inclusao do Professor.", resultado == true && resultado2 == true);
	}
	
	@Test
	public void testAlterarVet() throws ClienteException, SQLException {
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher p = new Teacher("Nome para Alterar", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		
		this.executaNoBanco("INSERT INTO " +
				"professor (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + prof.getNome() + "\", " +
				"\"" + prof.getCpf()+ "\", " +
				"\"" + prof.getTelefone() + "\", " +
				"\"" + prof.getEmail() + "\", " +
				"\"" + prof.getMatricula() + "\"); ");
		
		MaintainTeacher.getInstance().alterar("Nome para Alterar", "868.563.327-34", "123456", 
				"1234-5678", "Nome@email", prof);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getNome() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getTelefone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getMatricula() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getNome() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getTelefone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getMatricula() + "\";");
		
		assertTrue("Teste de Alteracao do Professor.", resultado);
	}
	
	@Test
	public void testExcluirVet() throws ClienteException, SQLException {
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		
		this.executaNoBanco("INSERT INTO " +
				"professor (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + prof.getNome() + "\", " +
				"\"" + prof.getCpf()+ "\", " +
				"\"" + prof.getTelefone() + "\", " +
				"\"" + prof.getEmail() + "\", " +
				"\"" + prof.getMatricula() + "\");");
		
		MaintainTeacher.getInstance().excluir(prof);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + prof.getNome() + "\" and " +
				"professor.cpf = \"" + prof.getCpf() + "\" and " +
				"professor.telefone = \"" + prof.getTelefone() + "\" and " +
				"professor.email = \"" + prof.getEmail() + "\" and " +
				"professor.matricula = \"" + prof.getMatricula() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getNome() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getTelefone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getMatricula() + "\";");
		
		boolean resultado2 = true;
		if(vet.size() > 0)
			resultado2 = !vet.lastElement().equals(prof);
		
		assertTrue("Teste de Exclusao do Professor.", resultado == false && resultado2 == true);
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
