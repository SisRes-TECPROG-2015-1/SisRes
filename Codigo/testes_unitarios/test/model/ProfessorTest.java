package test.model;

import static org.junit.Assert.*;

import model.Teacher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import exception.ClientException;

public class ProfessorTest {
	
	/**
	 *	Os teste da classe Cliente foram feitos aqui, por se tratar de uma classe
	 * abstrata, ela nao eh instaciavel, entao todas as suas funcionalidade foram
	 * testadas a partir das instancias da classe Professor.
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	
	@Test
	public void testInstance() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Instanciamento do Professor", p instanceof Teacher);
	}
	
	@Test
	public void testNome() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do Nome do Professor", "Nome" == p.getName());
	}

	@Test
	public void testCpf() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do CPF do Professor", "868.563.327-34" == p.getCpf());
	}
	
	@Test
	public void testMatricula() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste da Matricula do Professor", "123456" == p.getRegistration());
	}
	
	@Test
	public void testTelefone() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Telefone do Professor", "1234-5678" == p.getFone());
	}
	
	@Test
	public void testEmail() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do E-mail do Professor", "Nome@email" == p.getEmail());
	}

	
	
	@Test (expected= ClientException.class)
	public void testNomeVazio() throws ClientException {
		new Teacher("", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClientException.class)
	public void testNomeNumero() throws ClientException {
		new Teacher("Nome1", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testNomeCaractere() throws ClientException {
		new Teacher("Nome+", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testNomeNulo() throws ClientException {
		new Teacher(null, "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}

	
	
	@Test (expected= ClientException.class)
	public void testCpfVazio() throws ClientException {
		new Teacher("Nome", "", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClientException.class)
	public void testCpfLetras() throws ClientException {
		new Teacher("Nome", "868.563.327-3d", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testCpfDespadronizado() throws ClientException {
		new Teacher("Nome", "86856332734", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected = ClientException.class)
	public void testCpfInvalido() throws ClientException {
		new Teacher("Nome", "868.563.327-21", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testCpfNulo() throws ClientException {
		new Teacher("Nome", null, "123456", "1234-5678", "Nome@email");
	}
	
	
	
	@Test (expected= ClientException.class)
	public void testMatriculaVazia() throws ClientException {
		new Teacher("Nome", "868.563.327-34", "", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testMatriculaNula() throws ClientException {
		new Teacher("Nome", "868.563.327-34", null, "1234-5678", "Nome@email");
	}
	
	
	
	@Test
	public void testTelefoneVazio() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		assertTrue("Teste de Telefone Vazio do Professor", "" == p.getFone());
	}
	
	@Test (expected= ClientException.class)
	public void testTelefoneDespadronizado() throws ClientException {
		new Teacher("Nome", "868.563.327-34", "123456", "(901234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testTelefoneNulo() throws ClientException {
		new Teacher("Nome", "868.563.327-34", "123456", null, "Nome@email");
	}

	
	
	@Test
	public void testEmailVazio() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "");
		assertTrue("Teste de Email Vazio do Professor", "" == p.getEmail());
	}
	
	@Test (expected= ClientException.class)
	public void testEmailNulo() throws ClientException {
		new Teacher("Nome", "868.563.327-34", "123456", "123456", null);
	}

	
	
	@Test
	public void testEqualsTrue() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		assertTrue("Teste do E-mail do Professor", p.equals(q));
	}
	
	@Test
	public void testEqualsFalseNome() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("NomeDiferente", "868.563.327-34", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseCpf() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "338.688.964-65", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseMatricula() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseTelefone() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseEmail() throws ClientException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "", "Nom@el");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
}
