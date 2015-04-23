package test.control;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Student;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.FactoryConnection;
import control.MaintainStudent;
import exception.ClientException;

public class ManterAlunoTest {

	private static Vector<Student> alunos;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		alunos = MaintainStudent.getInstance().getStudents();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testGetInstance() {
		assertTrue("Verifica método getInstance() de MaintainStudent.", MaintainStudent.getInstance() instanceof MaintainStudent);
	}

	@Test
	public void testSingleton() {
		MaintainStudent p = MaintainStudent.getInstance();
		MaintainStudent q = MaintainStudent.getInstance();
		assertSame("Testando o Padrao Singleton em MaintainStudent", p, q);
	}

	
	
	@Test
	public void testInserir() throws ClientException, SQLException {
		Student aluno = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		MaintainStudent.getInstance().insertStudents("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		boolean resultado = this.estaNoBanco("SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + aluno.getName() + "\" and " +
				"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno.getFone() + "\" and " +
				"aluno.email = \"" + aluno.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno.getRegistration() + "\";");
				
		if(resultado){
			this.executaNoBanco("DELETE FROM aluno WHERE " +
					"aluno.nome = \"" + aluno.getName() + "\" and " +
					"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
					"aluno.telefone = \"" + aluno.getFone() + "\" and " +
					"aluno.email = \"" + aluno.getEmail() + "\" and " +
					"aluno.matricula = \"" + aluno.getRegistration() + "\";");
		}
		
		Student a = alunos.lastElement();
		boolean resultado2 = aluno.equals(a);
		alunos.remove(alunos.lastElement());
		assertTrue("Teste de Inclusao do Aluno.", resultado == true && resultado2 == true);
	}
	
	
	@Test
	public void testAlterar() throws ClientException, SQLException {
		Student aluno = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		Student a = new Student("Alterando", "040.757.021-70", "123456", "9999-9999", "Nome@email");
		
		this.executaNoBanco("INSERT INTO " +
				"aluno (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + aluno.getName() + "\", " +
				"\"" + aluno.getCpf()+ "\", " +
				"\"" + aluno.getFone() + "\", " +
				"\"" + aluno.getEmail() + "\", " +
				"\"" + aluno.getRegistration() + "\"); ");
		
		MaintainStudent.getInstance().changeStudent("Alterando", "040.757.021-70", "123456", 
				"9999-9999", "Nome@email", aluno);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + a.getName() + "\" and " +
				"aluno.cpf = \"" + a.getCpf() + "\" and " +
				"aluno.telefone = \"" + a.getFone() + "\" and " +
				"aluno.email = \"" + a.getEmail() + "\" and " +
				"aluno.matricula = \"" + a.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM aluno WHERE " +
					"aluno.nome = \"" + a.getName() + "\" and " +
					"aluno.cpf = \"" + a.getCpf() + "\" and " +
					"aluno.telefone = \"" + a.getFone() + "\" and " +
					"aluno.email = \"" + a.getEmail() + "\" and " +
					"aluno.matricula = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Alteracao do Aluno.", resultado);
	}
	
	@Test
	public void testExcluir() throws ClientException, SQLException {
		Student aluno = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "aluno@email");
		
		this.executaNoBanco("INSERT INTO " +
				"aluno (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + aluno.getName() + "\", " +
				"\"" + aluno.getCpf()+ "\", " +
				"\"" + aluno.getFone() + "\", " +
				"\"" + aluno.getEmail() + "\", " +
				"\"" + aluno.getRegistration() + "\");");
		
		MaintainStudent.getInstance().excludeStudent(aluno);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM aluno WHERE " +
				"aluno.nome = \"" + aluno.getName() + "\" and " +
				"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
				"aluno.telefone = \"" + aluno.getFone() + "\" and " +
				"aluno.email = \"" + aluno.getEmail() + "\" and " +
				"aluno.matricula = \"" + aluno.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM aluno WHERE " +
					"aluno.nome = \"" + aluno.getName() + "\" and " +
					"aluno.cpf = \"" + aluno.getCpf() + "\" and " +
					"aluno.telefone = \"" + aluno.getFone() + "\" and " +
					"aluno.email = \"" + aluno.getEmail() + "\" and " +
					"aluno.matricula = \"" + aluno.getRegistration() + "\";");
		
		boolean resultado2 = true;
		if(alunos.size() > 0)
			resultado2 = !alunos.lastElement().equals(aluno);
		
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
