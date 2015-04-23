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
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		
		resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
		"professor.nome = \"" + prof.getName() + "\" and " +
		"professor.cpf = \"" + prof.getCpf() + "\" and " +
		"professor.telefone = \"" + prof.getFone() + "\" and " +
		"professor.email = \"" + prof.getEmail() + "\" and " +
		"professor.matricula = \"" + prof.getRegistration() + "\";");
		
		if(resultado){
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getName() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getFone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getRegistration() + "\";");
		}
		assertTrue("Teste de Inclusão.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirNulo() throws ClientException, SQLException {
		TeacherDAO.getInstance().includeNewTeacher(null);
	}
	@Test (expected= ClientException.class)
	public void testIncluirComMesmoCpf() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher prof2 = new Teacher("Nome para Incluir Segundo", "868.563.327-34", "0987", "5678-5555", "");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getName() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getFone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclusão.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirComMesmaMatricula() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher prof2 = new Teacher("Nome para Incluir Segundo", "387.807.647-97", "123456", "5678-5555", "");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getName() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getFone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclusão.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testIncluirJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher prof = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher prof2 = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		TeacherDAO.getInstance().includeNewTeacher(prof);
		try{
			TeacherDAO.getInstance().includeNewTeacher(prof2);
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
			
		} finally {
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + prof.getName() + "\" and " +
					"professor.cpf = \"" + prof.getCpf() + "\" and " +
					"professor.telefone = \"" + prof.getFone() + "\" and " +
					"professor.email = \"" + prof.getEmail() + "\" and " +
					"professor.matricula = \"" + prof.getRegistration() + "\";");
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
					"professor.nome = \"" + prof2.getName() + "\" and " +
					"professor.cpf = \"" + prof2.getCpf() + "\" and " +
					"professor.telefone = \"" + prof2.getFone() + "\" and " +
					"professor.email = \"" + prof2.getEmail() + "\" and " +
					"professor.matricula = \"" + prof2.getRegistration() + "\";");
		}
		
		assertFalse("Teste de Inclusão.", resultado);
	}
	
	
	
	@Test
	public void testAlterar() throws ClientException, SQLException {
		boolean resultado = false;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher pn = new Teacher("Nome para Alterar", "387.807.647-97", "098765", "(123)4567-8899", "email@Nome");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		TeacherDAO.getInstance().modifyATeacher(p, pn);
		
		resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + pn.getName() + "\" and " +
				"professor.cpf = \"" + pn.getCpf() + "\" and " +
				"professor.telefone = \"" + pn.getFone() + "\" and " +
				"professor.email = \"" + pn.getEmail() + "\" and " +
				"professor.matricula = \"" + pn.getRegistration() + "\";");
		boolean resultado2 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pn.getName() + "\" and " +
					"professor.cpf = \"" + pn.getCpf() + "\" and " +
					"professor.telefone = \"" + pn.getFone() + "\" and " +
					"professor.email = \"" + pn.getEmail() + "\" and " +
					"professor.matricula = \"" + pn.getRegistration() + "\";");
		if(resultado2)
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", resultado == true && resultado2 == false);
	}
	@Test (expected= ClientException.class)
	public void testAlterarPrimeiroArgNulo() throws ClientException, SQLException {
		Teacher pn = new Teacher("Nome para Alterar", "868.563.327-34", "098765", "(123)4567-8899", "email@Nome");
		TeacherDAO.getInstance().modifyATeacher(null, pn);
	}
	@Test (expected= ClientException.class)
	public void testAlterarSegundoArgNulo() throws ClientException, SQLException {
		Teacher pn = new Teacher("Nome para Alterar", "868.563.327-34", "098765", "(123)4567-8899", "email@Nome");
		TeacherDAO.getInstance().modifyATeacher(pn, null);
	}
	@Test (expected= ClientException.class)
	public void testAlterarNaoExistente() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher pn = new Teacher("Nome para Alterar", "387.807.647-97", "098765", "(123)4567-8899", "email@Nome");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(p, pn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + pn.getName() + "\" and " +
				"professor.cpf = \"" + pn.getCpf() + "\" and " +
				"professor.telefone = \"" + pn.getFone() + "\" and " +
				"professor.email = \"" + pn.getEmail() + "\" and " +
				"professor.matricula = \"" + pn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pn.getName() + "\" and " +
					"professor.cpf = \"" + pn.getCpf() + "\" and " +
					"professor.telefone = \"" + pn.getFone() + "\" and " +
					"professor.email = \"" + pn.getEmail() + "\" and " +
					"professor.matricula = \"" + pn.getRegistration() + "\";");
		}
		assertFalse("Teste de Alteração.", resultado);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaJaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher pn = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(p, pn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + pn.getName() + "\" and " +
				"professor.cpf = \"" + pn.getCpf() + "\" and " +
				"professor.telefone = \"" + pn.getFone() + "\" and " +
				"professor.email = \"" + pn.getEmail() + "\" and " +
				"professor.matricula = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pn.getName() + "\" and " +
					"professor.cpf = \"" + pn.getCpf() + "\" and " +
					"professor.telefone = \"" + pn.getFone() + "\" and " +
					"professor.email = \"" + pn.getEmail() + "\" and " +
					"professor.matricula = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		}
		assertTrue("Teste de Alteração.", resultado == false && resultado2 == true);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaCpfExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher pn = new Teacher("Nome para Incluir Segundo", "387.807.647-97", "0987", "5555-5678", "Ne@email");
		Teacher pnn = new Teacher("Nome para Incluir Segundo", "868.563.327-34", "0987", "5555-5678", "Ne@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"professor (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + pn.getName() + "\", " +
				"\"" + pn.getCpf()+ "\", " +
				"\"" + pn.getFone() + "\", " +
				"\"" + pn.getEmail() + "\", " +
				"\"" + pn.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(pn, pnn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + pn.getName() + "\" and " +
				"professor.cpf = \"" + pn.getCpf() + "\" and " +
				"professor.telefone = \"" + pn.getFone() + "\" and " +
				"professor.email = \"" + pn.getEmail() + "\" and " +
				"professor.matricula = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
					"professor.nome = \"" + pnn.getName() + "\" and " +
					"professor.cpf = \"" + pnn.getCpf() + "\" and " +
					"professor.telefone = \"" + pnn.getFone() + "\" and " +
					"professor.email = \"" + pnn.getEmail() + "\" and " +
					"professor.matricula = \"" + pnn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pn.getName() + "\" and " +
					"professor.cpf = \"" + pn.getCpf() + "\" and " +
					"professor.telefone = \"" + pn.getFone() + "\" and " +
					"professor.email = \"" + pn.getEmail() + "\" and " +
					"professor.matricula = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pnn.getName() + "\" and " +
					"professor.cpf = \"" + pnn.getCpf() + "\" and " +
					"professor.telefone = \"" + pnn.getFone() + "\" and " +
					"professor.email = \"" + pnn.getEmail() + "\" and " +
					"professor.matricula = \"" + pnn.getRegistration() + "\";");
		}
		assertTrue("Teste de Alteração.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Test (expected= ClientException.class)
	public void testAlterarParaMatriculaExistente() throws ClientException, SQLException {
		boolean resultado = true;
		boolean resultado2 = false;
		boolean resultado3 = false;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		Teacher pn = new Teacher("Nome para Incluir Segundo", "387.807.647-97", "0987", "5555-5678", "Ne@email");
		Teacher pnn = new Teacher("Nome para Incluir Segundo", "387.807.647-97", "123456", "5555-5678", "Ne@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		this.executaNoBanco("INSERT INTO " +
				"professor (nome, cpf, telefone, email, matricula) VALUES (" +
				"\"" + pn.getName() + "\", " +
				"\"" + pn.getCpf()+ "\", " +
				"\"" + pn.getFone() + "\", " +
				"\"" + pn.getEmail() + "\", " +
				"\"" + pn.getRegistration() + "\"); ");
		
		try{
			TeacherDAO.getInstance().modifyATeacher(pn, pnn);
		} finally {
			resultado = this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + pn.getName() + "\" and " +
				"professor.cpf = \"" + pn.getCpf() + "\" and " +
				"professor.telefone = \"" + pn.getFone() + "\" and " +
				"professor.email = \"" + pn.getEmail() + "\" and " +
				"professor.matricula = \"" + pn.getRegistration() + "\";");
			resultado2 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\";");
			resultado3 =  this.estaNoBanco("SELECT * FROM professor WHERE " +
					"professor.nome = \"" + pnn.getName() + "\" and " +
					"professor.cpf = \"" + pnn.getCpf() + "\" and " +
					"professor.telefone = \"" + pnn.getFone() + "\" and " +
					"professor.email = \"" + pnn.getEmail() + "\" and " +
					"professor.matricula = \"" + pnn.getRegistration() + "\";");
			if(resultado)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pn.getName() + "\" and " +
					"professor.cpf = \"" + pn.getCpf() + "\" and " +
					"professor.telefone = \"" + pn.getFone() + "\" and " +
					"professor.email = \"" + pn.getEmail() + "\" and " +
					"professor.matricula = \"" + pn.getRegistration() + "\";");
			if(resultado2)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
			if(resultado3)
				this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + pnn.getName() + "\" and " +
					"professor.cpf = \"" + pnn.getCpf() + "\" and " +
					"professor.telefone = \"" + pnn.getFone() + "\" and " +
					"professor.email = \"" + pnn.getEmail() + "\" and " +
					"professor.matricula = \"" + pnn.getRegistration() + "\";");
		}
		assertTrue("Teste de Alteração.", resultado == true && resultado2 == true && resultado3 == false);
	}
	@Ignore // (expected= ClientException.class)
	public void testAlterarEnvolvidoEmReserva() throws ClientException, SQLException {
		fail();
	}
	
	
	
	@Test
	public void testExcluir() throws ClientException, SQLException {
		boolean resultado = true;
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		TeacherDAO.getInstance().excludeATeacher(p);
		

		resultado =  this.estaNoBanco("SELECT * FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\";");
		if(resultado)
			this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertFalse("Teste de Alteração.", resultado);
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
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		TeacherDAO.getInstance().excludeATeacher(p);
	}
	
	
	
	@Test
	public void testBuscarNome() throws ClientException, SQLException {
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByName("Nome para Incluir");

		this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", vet.size() > 0);
	}
	@Test
	public void testBuscarCpf() throws ClientException, SQLException {
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByCpf("868.563.327-34");

		this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", vet.size() > 0);
	}
	@Test
	public void testBuscarMatricula() throws ClientException, SQLException {
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByRegistration("123456");

		this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", vet.size() > 0);
	}
	@Test
	public void testBuscarTelefone() throws ClientException, SQLException {
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByPhoneNumber("1234-5678");

		this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", vet.size() > 0);
	}
	@Test
	public void testBuscarEmail() throws ClientException, SQLException {
		Teacher p = new Teacher("Nome para Incluir", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		this.executaNoBanco("INSERT INTO " +
						"professor (nome, cpf, telefone, email, matricula) VALUES (" +
						"\"" + p.getName() + "\", " +
						"\"" + p.getCpf()+ "\", " +
						"\"" + p.getFone() + "\", " +
						"\"" + p.getEmail() + "\", " +
						"\"" + p.getRegistration() + "\"); ");
		
		Vector<Teacher> vet = TeacherDAO.getInstance().searchByEmail("Nome@email");

		this.executaNoBanco("DELETE FROM professor WHERE " +
					"professor.nome = \"" + p.getName() + "\" and " +
					"professor.cpf = \"" + p.getCpf() + "\" and " +
					"professor.telefone = \"" + p.getFone() + "\" and " +
					"professor.email = \"" + p.getEmail() + "\" and " +
					"professor.matricula = \"" + p.getRegistration() + "\";");
		
		assertTrue("Teste de Alteração.", vet.size() > 0);
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
