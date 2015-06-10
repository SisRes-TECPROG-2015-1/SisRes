package test.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.Teacher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import persistence.FactoryConnection;
import persistence.TeacherDAO;
import exception.ClientException;

public class ProfessorDAOTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testInstance() {
		assertTrue("Instanciando ProfessorDAO", TeacherDAO.getInstance() instanceof TeacherDAO);
	}
	
	@Test
	public void testSingleton() {
		TeacherDAO p = TeacherDAO.getInstance();
		TeacherDAO q = TeacherDAO.getInstance();
		assertSame("Testando o Padrao Singleton", p, q);
	}
	
	
	
	@Test
	public void testIncluir() throws ClientException, SQLException {
		boolean resultado = false;
		Teacher prof = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		
		resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
		"teacher.name = \"" + prof.getName() + "\" and " +
		"teacher.cpf = \"" + prof.getCpf() + "\" and " +
		"teacher.fone = \"" + prof.getFone() + "\" and " +
		"teacher.email = \"" + prof.getEmail() + "\" and " +
		"teacher.registration = \"" + prof.getRegistration() + "\";");
		
		if(resultado){
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof.getName() + "\" and " +
					"teacher.cpf = \"" + prof.getCpf() + "\" and " +
					"teacher.fone = \"" + prof.getFone() + "\" and " +
					"teacher.email = \"" + prof.getEmail() + "\" and " +
					"teacher.registration = \"" + prof.getRegistration() + "\";");
		}
		assertTrue("Teste de Inclus�o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirNulo() throws ClientException, SQLException {
		TeacherDAO.getInstance().includeNewTeacher(null);
	}
	@Test (expected= ClientException.class)
	public void testIncluirComMesmoCpf() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher prof2 = new Teacher("name para Incluir Segundo", "868.563.327-34", "0987", "5678-5555", "");
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof.getName() + "\" and " +
					"teacher.cpf = \"" + prof.getCpf() + "\" and " +
					"teacher.fone = \"" + prof.getFone() + "\" and " +
					"teacher.email = \"" + prof.getEmail() + "\" and " +
					"teacher.registration = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirComMesmaregistration() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher prof2 = new Teacher("name para Incluir Segundo", "387.807.647-97", "123456", "5678-5555", "");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof.getName() + "\" and " +
					"teacher.cpf = \"" + prof.getCpf() + "\" and " +
					"teacher.fone = \"" + prof.getFone() + "\" and " +
					"teacher.email = \"" + prof.getEmail() + "\" and " +
					"teacher.registration = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher prof2 = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + prof.getName() + "\" and " +
					"teacher.cpf = \"" + prof.getCpf() + "\" and " +
					"teacher.fone = \"" + prof.getFone() + "\" and " +
					"teacher.email = \"" + prof.getEmail() + "\" and " +
					"teacher.registration = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
					"teacher.name = \"" + prof2.getName() + "\" and " +
					"teacher.cpf = \"" + prof2.getCpf() + "\" and " +
					"teacher.fone = \"" + prof2.getFone() + "\" and " +
					"teacher.email = \"" + prof2.getEmail() + "\" and " +
					"teacher.registration = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclus�o.", resultado);
	}
	
	
	
	@Test
	public void testAlterar() throws ClientException, SQLException {
		boolean resultado = false;
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher pn = new Teacher("name para Alterar", "387.807.647-97", "098765", "(123)4567-8899", "email@name");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		TeacherDAO.getInstance().modifyATeacher(p, pn);
		
		resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + pn.getName() + "\" and " +
				"teacher.cpf = \"" + pn.getCpf() + "\" and " +
				"teacher.fone = \"" + pn.getFone() + "\" and " +
				"teacher.email = \"" + pn.getEmail() + "\" and " +
				"teacher.registration = \"" + pn.getRegistration() + "\";");
		boolean resultado2 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pn.getName() + "\" and " +
					"teacher.cpf = \"" + pn.getCpf() + "\" and " +
					"teacher.fone = \"" + pn.getFone() + "\" and " +
					"teacher.email = \"" + pn.getEmail() + "\" and " +
					"teacher.registration = \"" + pn.getRegistration() + "\";");
		if(resultado2)
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == false);
	}
	@Test (expected= ClientException.class)
	public void testAlterarPrimeiroArgNulo() throws ClientException, SQLException {
		Teacher pn = new Teacher("name para Alterar", "868.563.327-34", "098765", "(123)4567-8899", "email@name");
		TeacherDAO.getInstance().modifyATeacher(null, pn);
	}
	@Test (expected= ClientException.class)
	public void testAlterarSegundoArgNulo() throws ClientException, SQLException {
		Teacher pn = new Teacher("name para Alterar", "868.563.327-34", "098765", "(123)4567-8899", "email@name");
		TeacherDAO.getInstance().modifyATeacher(pn, null);
	}
	@Test (expected= ClientException.class)
	public void testAlterarNaoExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher pn = new Teacher("name para Alterar", "387.807.647-97", "098765", "(123)4567-8899", "email@name");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(p, pn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + pn.getName() + "\" and " +
				"teacher.cpf = \"" + pn.getCpf() + "\" and " +
				"teacher.fone = \"" + pn.getFone() + "\" and " +
				"teacher.email = \"" + pn.getEmail() + "\" and " +
				"teacher.registration = \"" + pn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pn.getName() + "\" and " +
					"teacher.cpf = \"" + pn.getCpf() + "\" and " +
					"teacher.fone = \"" + pn.getFone() + "\" and " +
					"teacher.email = \"" + pn.getEmail() + "\" and " +
					"teacher.registration = \"" + pn.getRegistration() + "\";");
		}
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher pn = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(p, pn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + pn.getName() + "\" and " +
				"teacher.cpf = \"" + pn.getCpf() + "\" and " +
				"teacher.fone = \"" + pn.getFone() + "\" and " +
				"teacher.email = \"" + pn.getEmail() + "\" and " +
				"teacher.registration = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pn.getName() + "\" and " +
					"teacher.cpf = \"" + pn.getCpf() + "\" and " +
					"teacher.fone = \"" + pn.getFone() + "\" and " +
					"teacher.email = \"" + pn.getEmail() + "\" and " +
					"teacher.registration = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		}
		assertTrue("Teste de Altera��o.", resultado == false && resultado2 == true);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaCpfExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher pn = new Teacher("name para Incluir Segundo", "387.807.647-97", "0987", "5555-5678", "Ne@email");
		Teacher pnn = new Teacher("name para Incluir Segundo", "868.563.327-34", "0987", "5555-5678", "Ne@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"teacher (name, cpf, fone, email, registration) VALUES (" +
				"\"" + pn.getName() + "\", " +
				"\"" + pn.getCpf()+ "\", " +
				"\"" + pn.getFone() + "\", " +
				"\"" + pn.getEmail() + "\", " +
				"\"" + pn.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(pn, pnn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + pn.getName() + "\" and " +
				"teacher.cpf = \"" + pn.getCpf() + "\" and " +
				"teacher.fone = \"" + pn.getFone() + "\" and " +
				"teacher.email = \"" + pn.getEmail() + "\" and " +
				"teacher.registration = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
					"teacher.name = \"" + pnn.getName() + "\" and " +
					"teacher.cpf = \"" + pnn.getCpf() + "\" and " +
					"teacher.fone = \"" + pnn.getFone() + "\" and " +
					"teacher.email = \"" + pnn.getEmail() + "\" and " +
					"teacher.registration = \"" + pnn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pn.getName() + "\" and " +
					"teacher.cpf = \"" + pn.getCpf() + "\" and " +
					"teacher.fone = \"" + pn.getFone() + "\" and " +
					"teacher.email = \"" + pn.getEmail() + "\" and " +
					"teacher.registration = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pnn.getName() + "\" and " +
					"teacher.cpf = \"" + pnn.getCpf() + "\" and " +
					"teacher.fone = \"" + pnn.getFone() + "\" and " +
					"teacher.email = \"" + pnn.getEmail() + "\" and " +
					"teacher.registration = \"" + pnn.getRegistration() + "\";");
		}
		assertTrue("Teste de Altera��o.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Test (expected= ClientException.class)
	public void testAlterarPararegistrationExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		Teacher pn = new Teacher("name para Incluir Segundo", "387.807.647-97", "0987", "5555-5678", "Ne@email");
		Teacher pnn = new Teacher("name para Incluir Segundo", "387.807.647-97", "123456", "5555-5678", "Ne@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"teacher (name, cpf, fone, email, registration) VALUES (" +
				"\"" + pn.getName() + "\", " +
				"\"" + pn.getCpf()+ "\", " +
				"\"" + pn.getFone() + "\", " +
				"\"" + pn.getEmail() + "\", " +
				"\"" + pn.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(pn, pnn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + pn.getName() + "\" and " +
				"teacher.cpf = \"" + pn.getCpf() + "\" and " +
				"teacher.fone = \"" + pn.getFone() + "\" and " +
				"teacher.email = \"" + pn.getEmail() + "\" and " +
				"teacher.registration = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
					"teacher.name = \"" + pnn.getName() + "\" and " +
					"teacher.cpf = \"" + pnn.getCpf() + "\" and " +
					"teacher.fone = \"" + pnn.getFone() + "\" and " +
					"teacher.email = \"" + pnn.getEmail() + "\" and " +
					"teacher.registration = \"" + pnn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pn.getName() + "\" and " +
					"teacher.cpf = \"" + pn.getCpf() + "\" and " +
					"teacher.fone = \"" + pn.getFone() + "\" and " +
					"teacher.email = \"" + pn.getEmail() + "\" and " +
					"teacher.registration = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + pnn.getName() + "\" and " +
					"teacher.cpf = \"" + pnn.getCpf() + "\" and " +
					"teacher.fone = \"" + pnn.getFone() + "\" and " +
					"teacher.email = \"" + pnn.getEmail() + "\" and " +
					"teacher.registration = \"" + pnn.getRegistration() + "\";");
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
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		TeacherDAO.getInstance().excludeATeacher(p);
		

		resultado =  this.estaNoBanco("SELECT * FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertFalse("Teste de Altera��o.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testExcluirNulo() throws ClientException, SQLException {
		TeacherDAO.getInstance().excludeATeacher(null);
	}
	@Ignore //(expected= ClientException.class)
	public void testExcluirEnvolvidoEmReserva() throws ClientException, SQLException {
		fail();
	}
	@Test (expected= ClientException.class)
	public void testExcluirNaoExistente() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		TeacherDAO.getInstance().excludeATeacher(p);
	}
	
	
	
	@Test
	public void testBuscarname() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByName("name para Incluir");

		this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarCpf() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByCpf("868.563.327-34");

		this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarregistration() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByRegistration("123456");

		this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarfone() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByPhoneNumber("1234-5678");

		this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Altera��o.", vet.size() > 0);
	}
	@Test
	public void testBuscarEmail() throws ClientException, SQLException {
		Teacher p = new Teacher("name para Incluir", "868.563.327-34", "123456", "1234-5678", "name@email");
		this.executaNoBanco("INSERT INTO " +
						"teacher (name, cpf, fone, email, registration) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByEmail("name@email");

		this.executaNoBanco("DELETE FROM teacher WHERE " +
					"teacher.name = \"" + p.getName() + "\" and " +
					"teacher.cpf = \"" + p.getCpf() + "\" and " +
					"teacher.fone = \"" + p.getFone() + "\" and " +
					"teacher.email = \"" + p.getEmail() + "\" and " +
					"teacher.registration = \"" + p.getRegistration() + "\";");
		
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
