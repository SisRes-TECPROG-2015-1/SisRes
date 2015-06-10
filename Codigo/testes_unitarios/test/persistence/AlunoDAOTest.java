package test.persistence;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Student;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import exception.ClientException;
import persistence.FactoryConnection;
import persistence.StudentDAO;

public class AlunoDAOTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testInstance() {
		assertTrue("Instanciando StudentDAO", StudentDAO.getInstance() instanceof StudentDAO);
	}
	
	@Test
	public void testSingleton() {
		StudentDAO p = StudentDAO.getInstance();
		StudentDAO q = StudentDAO.getInstance();
		assertSame("Testando o Padrao Singleton", p, q);
	}
	

	@Test
	public void testIncluir() throws ClientException, SQLException {
		boolean resultado = false;
		Student student = new Student("Incluindo", "040.757.021-70", "098765", "9999-9999", "student@email");
		StudentDAO.getInstance().includeNewStudent(student);
		
		resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
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
		assertTrue("Teste de Inclus�o.", resultado);
	}
	
	@Test (expected= ClientException.class)
	public void testIncluirNulo() throws ClientException, SQLException {
		StudentDAO.getInstance().includeNewStudent(null);
	}
	
	@Test (expected= ClientException.class)
	public void testIncluirComMesmoCpf() throws ClientException, SQLException {
		boolean resultado = true;
		Student student = new Student("Incluindo", "040.757.021-70", "098765", "1111-1111", "student@email");
		Student student2 = new Student("Incluindo CPF Igual", "040.747.021-70", "987654", "2222-2222", "student2@email");
		StudentDAO.getInstance().includeNewStudent(student);
		try{
			StudentDAO.getInstance().includeNewStudent(student2);
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student.getName() + "\" and " +
					"student.cpf = \"" + student.getCpf() + "\" and " +
					"student.fone = \"" + student.getFone() + "\" and " +
					"student.email = \"" + student.getEmail() + "\" and " +
					"student.registration = \"" + student.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirComMesmaregistration() throws ClientException, SQLException {
		boolean resultado = true;
		Student student = new Student("Incluindo", "040.757.021-70", "111111", "1111-1111", "student@email");
		Student student2 = new Student("Incluindo registration Igual", "490.491.781-20", "111111", "2222-2222", "student2@email");
		StudentDAO.getInstance().includeNewStudent(student);
		try{
			StudentDAO.getInstance().includeNewStudent(student2);
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student.getName() + "\" and " +
					"student.cpf = \"" + student.getCpf() + "\" and " +
					"student.fone = \"" + student.getFone() + "\" and " +
					"student.email = \"" + student.getEmail() + "\" and " +
					"student.registration = \"" + student.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Student student = new Student("Incluindo", "040.757.021-70", "58801", "3333-3333", "student@email");
		Student student2 = new Student("Incluindo", "040.757.021-70", "58801", "3333-3333", "student@email");
		StudentDAO.getInstance().includeNewStudent(student);
		try{
			StudentDAO.getInstance().includeNewStudent(student2);
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + student.getName() + "\" and " +
					"student.cpf = \"" + student.getCpf() + "\" and " +
					"student.fone = \"" + student.getFone() + "\" and " +
					"student.email = \"" + student.getEmail() + "\" and " +
					"student.registration = \"" + student.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
					"student.name = \"" + student2.getName() + "\" and " +
					"student.cpf = \"" + student2.getCpf() + "\" and " +
					"student.fone = \"" + student2.getFone() + "\" and " +
					"student.email = \"" + student2.getEmail() + "\" and " +
					"student.registration = \"" + student2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	
	
	
	@Test
	public void testAlterar() throws ClientException, SQLException {
		boolean resultado = false;
		Student a = new Student("Incluindo", "868.563.327-34", "123456", "1234-5678", "name@email");
		Student an = new Student("Alterando", "387.807.647-97", "098765", "(123)4567-8899", "email@name");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		StudentDAO.getInstance().modifyStudent(a, an);
		
		resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + an.getName() + "\" and " +
				"student.cpf = \"" + an.getCpf() + "\" and " +
				"student.fone = \"" + an.getFone() + "\" and " +
				"student.email = \"" + an.getEmail() + "\" and " +
				"student.registration = \"" + an.getRegistration() + "\";");
		boolean resultado2 =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + an.getName() + "\" and " +
					"student.cpf = \"" + an.getCpf() + "\" and " +
					"student.fone = \"" + an.getFone() + "\" and " +
					"student.email = \"" + an.getEmail() + "\" and " +
					"student.registration = \"" + an.getRegistration() + "\";");
		if(resultado2)
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == false);
	}
	
	@Test (expected= ClientException.class)
	public void testAlterarPrimeiroArgNulo() throws ClientException, SQLException {
		Student an = new Student("Alterando", "00.757.021-70", "123456", "(999)9999-9999", "student@email");
		StudentDAO.getInstance().modifyStudent(null, an);
	}
	
	@Test (expected= ClientException.class)
	public void testAlterarSegundoArgNulo() throws ClientException, SQLException {
		Student an = new Student("Alterando", "00.757.021-70", "123456", "(999)9999-9999", "student@email");
		StudentDAO.getInstance().modifyStudent(an, null);
	}
	@Test (expected= ClientException.class)
	public void testAlterarNaoExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "1111-1111", "student@email");
		Student an = new Student("Alterando", "490.491.781-20", "098765", "(999)9999-9999", "email@student");
		
		try{
			StudentDAO.getInstance().modifyStudent(a, an);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + an.getName() + "\" and " +
				"student.cpf = \"" + an.getCpf() + "\" and " +
				"student.fone = \"" + an.getFone() + "\" and " +
				"student.email = \"" + an.getEmail() + "\" and " +
				"student.registration = \"" + an.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + an.getName() + "\" and " +
					"student.cpf = \"" + an.getCpf() + "\" and " +
					"student.fone = \"" + an.getFone() + "\" and " +
					"student.email = \"" + an.getEmail() + "\" and " +
					"student.registration = \"" + an.getRegistration() + "\";");
		}
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "student@email");
		Student an = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		try{
			StudentDAO.getInstance().modifyStudent(a, an);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + an.getName() + "\" and " +
				"student.cpf = \"" + an.getCpf() + "\" and " +
				"student.fone = \"" + an.getFone() + "\" and " +
				"student.email = \"" + an.getEmail() + "\" and " +
				"student.registration = \"" + an.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + an.getName() + "\" and " +
					"student.cpf = \"" + an.getCpf() + "\" and " +
					"student.fone = \"" + an.getFone() + "\" and " +
					"student.email = \"" + an.getEmail() + "\" and " +
					"student.registration = \"" + an.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		}
		assertTrue("Teste de Altera��o.", resultado == false && resultado2 == true);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaCpfExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		Student an = new Student("Incluindo Segundo", "490.491.781-20", "1234", "4444-4444", "novostudent@email");
		Student ann = new Student("Incluindo Segundo", "040.757.021-70", "1234", "4444-4444", "novostudent@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + an.getName() + "\", " +
				"\"" + an.getCpf()+ "\", " +
				"\"" + an.getFone() + "\", " +
				"\"" + an.getEmail() + "\", " +
				"\"" + an.getRegistration() + "\"); ");
		
		try{
			StudentDAO.getInstance().modifyStudent(an, ann);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + an.getName() + "\" and " +
				"student.cpf = \"" + an.getCpf() + "\" and " +
				"student.fone = \"" + an.getFone() + "\" and " +
				"student.email = \"" + an.getEmail() + "\" and " +
				"student.registration = \"" + an.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM student WHERE " +
					"student.name = \"" + ann.getName() + "\" and " +
					"student.cpf = \"" + ann.getCpf() + "\" and " +
					"student.fone = \"" + ann.getFone() + "\" and " +
					"student.email = \"" + ann.getEmail() + "\" and " +
					"student.registration = \"" + ann.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + an.getName() + "\" and " +
					"student.cpf = \"" + an.getCpf() + "\" and " +
					"student.fone = \"" + an.getFone() + "\" and " +
					"student.email = \"" + an.getEmail() + "\" and " +
					"student.registration = \"" + an.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"student.name = \"" + ann.getName() + "\" and " +
					"student.cpf = \"" + ann.getCpf() + "\" and " +
					"student.fone = \"" + ann.getFone() + "\" and " +
					"student.email = \"" + ann.getEmail() + "\" and " +
					"student.registration = \"" + ann.getRegistration() + "\";");
		}
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Test (expected= ClientException.class)
	public void testAlterarPararegistrationExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-99999", "student@email");
		Student an = new Student("Incluindo Segundo", "490.491.781-20", "0987", "5555-5555", "studentNovo@email");
		Student ann = new Student("Incluindo Segundo", "490.491.781-20", "123456", "5555-5555", "studentNovo@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + an.getName() + "\", " +
				"\"" + an.getCpf()+ "\", " +
				"\"" + an.getFone() + "\", " +
				"\"" + an.getEmail() + "\", " +
				"\"" + an.getRegistration() + "\"); ");
		
		try{
			StudentDAO.getInstance().modifyStudent(an, ann);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + an.getName() + "\" and " +
				"student.cpf = \"" + an.getCpf() + "\" and " +
				"student.fone = \"" + an.getFone() + "\" and " +
				"student.email = \"" + an.getEmail() + "\" and " +
				"student.registration = \"" + an.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM student WHERE " +
					"student.name = \"" + ann.getName() + "\" and " +
					"student.cpf = \"" + ann.getCpf() + "\" and " +
					"student.fone = \"" + ann.getFone() + "\" and " +
					"student.email = \"" + ann.getEmail() + "\" and " +
					"student.registration = \"" + ann.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM student WHERE " +
						"student.name = \"" + an.getName() + "\" and " +
						"student.cpf = \"" + an.getCpf() + "\" and " +
						"student.fone = \"" + an.getFone() + "\" and " +
						"student.email = \"" + an.getEmail() + "\" and " +
						"student.registration = \"" + an.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + ann.getName() + "\" and " +
					"student.cpf = \"" + ann.getCpf() + "\" and " +
					"student.fone = \"" + ann.getFone() + "\" and " +
					"student.email = \"" + ann.getEmail() + "\" and " +
					"student.registration = \"" + ann.getRegistration() + "\";");
		}
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Ignore // (expected= ClientException.class)
	public void testAlterarEnvolvidoEmReserva() throws ClientException, SQLException {
		fail();
	}
	
	
	
	@Test
	public void testExcluir() throws ClientException, SQLException {
		boolean resultado = true;
		Student a = new Student("Incluindo", "040.757.021-70", "058801", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		StudentDAO.getInstance().deleteStudent(a);
		

		resultado =  this.estaNoBanco("SELECT * FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testExcluirNulo() throws ClientException, SQLException {
		StudentDAO.getInstance().deleteStudent(null);
	}
	@Ignore // (expected= ClientException.class)
	public void testExcluirEnvolvidoEmReserva() throws ClientException, SQLException {
		fail();
	}
	@Test (expected= ClientException.class)
	public void testExcluirNaoExistente() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		StudentDAO.getInstance().deleteStudent(a);
	}
	
	
	
	@Test
	public void testBuscarname() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		Vector<Student> vet = StudentDAO.getInstance().searchByName("Incluindo");

		this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarCpf() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		Vector<Student> vet = StudentDAO.getInstance().searchByCpf("040.757.021-70");

		this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarregistration() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
						"student (name, cpf, fone, email, registration) VALUES (" +
						"\"" + a.getName() + "\", " +
						"\"" + a.getCpf()+ "\", " +
						"\"" + a.getFone() + "\", " +
						"\"" + a.getEmail() + "\", " +
						"\"" + a.getRegistration() + "\"); ");
		
		Vector<Student> vet = StudentDAO.getInstance().searchByRegistration("123456");

		this.executaNoBanco("DELETE FROM student WHERE " +
					"student.name = \"" + a.getName() + "\" and " +
					"student.cpf = \"" + a.getCpf() + "\" and " +
					"student.fone = \"" + a.getFone() + "\" and " +
					"student.email = \"" + a.getEmail() + "\" and " +
					"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarfone() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + a.getName() + "\", " +
				"\"" + a.getCpf()+ "\", " +
				"\"" + a.getFone() + "\", " +
				"\"" + a.getEmail() + "\", " +
				"\"" + a.getRegistration() + "\"); ");
		
		Vector<Student> vet = StudentDAO.getInstance().searchByPhoneNumber("9999-9999");

		this.executaNoBanco("DELETE FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarEmail() throws ClientException, SQLException {
		Student a = new Student("Incluindo", "040.757.021-70", "123456", "9999-9999", "student@email");
		this.executaNoBanco("INSERT INTO " +
				"student (name, cpf, fone, email, registration) VALUES (" +
				"\"" + a.getName() + "\", " +
				"\"" + a.getCpf()+ "\", " +
				"\"" + a.getFone() + "\", " +
				"\"" + a.getEmail() + "\", " +
				"\"" + a.getRegistration() + "\"); ");
		
		Vector<Student> vet = StudentDAO.getInstance().searchByEmail("student@email");

		this.executaNoBanco("DELETE FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
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

	