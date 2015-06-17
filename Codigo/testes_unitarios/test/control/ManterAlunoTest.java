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

	private static Vector<Student> students;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		students = MaintainStudent.getInstance().getStudents();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testGetInstance() {
		assertTrue("Verifica metodo getInstance() de MaintainStudent.", MaintainStudent.getInstance() instanceof MaintainStudent);
	}

	@Test
	public void testSingleton() {
		MaintainStudent p = MaintainStudent.getInstance();
		MaintainStudent q = MaintainStudent.getInstance();
		assertSame("Testando o Padrao Singleton em MaintainStudent", p, q);
	}

	
	
	@Test
	public void testInserir() throws ClientException, SQLException {
		Student student = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		MaintainStudent.getInstance().insertStudents("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		
		boolean resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + student.getName() + "\" and " +
				"student.cpf = \"" + student.getCpf() + "\" and " +
				"student.fone = \"" + student.getFone() + "\" and " +
				"student.email = \"" + student.getEmail() + "\" and " +
				"student.registration = \"" + student.getRegistration() + "\";");
				
		if(resultado){
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student.getName() + "\" and " +
					"student.cpf = \"" + student.getCpf() + "\" and " +
					"student.fone = \"" + student.getFone() + "\" and " +
					"student.email = \"" + student.getEmail() + "\" and " +
					"student.registration = \"" + student.getRegistration() + "\";");
		}
		
		Student a = students.lastElement();
		boolean resultado2 = student.equals(a);
		students.remove(students.lastElement());
		assertTrue("Teste de Inclusao do Aluno.", resultado == true && resultado2 == true);
	}
	
	
	@Test
	public void testAlterar() throws ClientException, SQLException {
		Student student = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		Student a = new Student("Alterando", "040.757.021-70", "123456", "9999-9999", "Nome@email");
		
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + student.getName() + "\", " +
				"\"" + student.getCpf()+ "\", " +
				"\"" + student.getFone() + "\", " +
				"\"" + student.getEmail() + "\", " +
				"\"" + student.getRegistration() + "\"); ");
		
		MaintainStudent.getInstance().changeStudent("Alterando", "040.757.021-70", "123456", 
				"9999-9999", "Nome@email", student);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
		if(resultado) {
			
		
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Alteracao do Aluno.", resultado);
	
		}
		else
		{
			// Do nothing
		}
	}
	
	@Test
	public void testExcluir() throws ClientException, SQLException {
		Student student = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + student.getName() + "\", " +
				"\"" + student.getCpf()+ "\", " +
				"\"" + student.getFone() + "\", " +
				"\"" + student.getEmail() + "\", " +
				"\"" + student.getRegistration() + "\");");
		
		MaintainStudent.getInstance().excludeStudent(student);
		
		boolean resultado =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + student.getName() + "\" and " +
				"student.cpf = \"" + student.getCpf() + "\" and " +
				"student.fone = \"" + student.getFone() + "\" and " +
				"student.email = \"" + student.getEmail() + "\" and " +
				"student.registration = \"" + student.getRegistration() + "\";");
		if(resultado){
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student.getName() + "\" and " +
					"student.cpf = \"" + student.getCpf() + "\" and " +
					"student.fone = \"" + student.getFone() + "\" and " +
					"student.email = \"" + student.getEmail() + "\" and " +
					"student.registration = \"" + student.getRegistration() + "\";");
		
		boolean resultado2 = true;
		if(students.size() > 0)
			resultado2 = !students.lastElement().equals(student);
		
		assertTrue("Teste de Exclusao do Professor.", resultado == false && resultado2 == true);
		} else {
			//Do nothing
		}
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
