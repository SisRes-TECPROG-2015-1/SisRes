package test.model;

import static org.junit.Assert.*;

import model.Teacher;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import exception.ClienteException;

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
	public void testInstance() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Instanciamento do Professor", p instanceof Teacher);
	}
	
	@Test
	public void testNome() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do Nome do Professor", "Nome" == p.getNome());
	}

	@Test
	public void testCpf() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do CPF do Professor", "868.563.327-34" == p.getCpf());
	}
	
	@Test
	public void testMatricula() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste da Matricula do Professor", "123456" == p.getMatricula());
	}
	
	@Test
	public void testTelefone() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Telefone do Professor", "1234-5678" == p.getTelefone());
	}
	
	@Test
	public void testEmail() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do E-mail do Professor", "Nome@email" == p.getEmail());
	}

	
	
	@Test (expected= ClienteException.class)
	public void testNomeVazio() throws ClienteException {
		new Teacher("", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClienteException.class)
	public void testNomeNumero() throws ClienteException {
		new Teacher("Nome1", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testNomeCaractere() throws ClienteException {
		new Teacher("Nome+", "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testNomeNulo() throws ClienteException {
		new Teacher(null, "868.563.327-34", "123456", "1234-5678", "Nome@email");
	}

	
	
	@Test (expected= ClienteException.class)
	public void testCpfVazio() throws ClienteException {
		new Teacher("Nome", "", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClienteException.class)
	public void testCpfLetras() throws ClienteException {
		new Teacher("Nome", "868.563.327-3d", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testCpfDespadronizado() throws ClienteException {
		new Teacher("Nome", "86856332734", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected = ClienteException.class)
	public void testCpfInvalido() throws ClienteException {
		new Teacher("Nome", "868.563.327-21", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testCpfNulo() throws ClienteException {
		new Teacher("Nome", null, "123456", "1234-5678", "Nome@email");
	}
	
	
	
	@Test (expected= ClienteException.class)
	public void testMatriculaVazia() throws ClienteException {
		new Teacher("Nome", "868.563.327-34", "", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testMatriculaNula() throws ClienteException {
		new Teacher("Nome", "868.563.327-34", null, "1234-5678", "Nome@email");
	}
	
	
	
	@Test
	public void testTelefoneVazio() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		assertTrue("Teste de Telefone Vazio do Professor", "" == p.getTelefone());
	}
	
	@Test (expected= ClienteException.class)
	public void testTelefoneDespadronizado() throws ClienteException {
		new Teacher("Nome", "868.563.327-34", "123456", "(901234-5678", "Nome@email");
	}
	
	@Test (expected= ClienteException.class)
	public void testTelefoneNulo() throws ClienteException {
		new Teacher("Nome", "868.563.327-34", "123456", null, "Nome@email");
	}

	
	
	@Test
	public void testEmailVazio() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "1234-5678", "");
		assertTrue("Teste de Email Vazio do Professor", "" == p.getEmail());
	}
	
	@Test (expected= ClienteException.class)
	public void testEmailNulo() throws ClienteException {
		new Teacher("Nome", "868.563.327-34", "123456", "123456", null);
	}

	
	
	@Test
	public void testEqualsTrue() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		assertTrue("Teste do E-mail do Professor", p.equals(q));
	}
	
	@Test
	public void testEqualsFalseNome() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("NomeDiferente", "868.563.327-34", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseCpf() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "338.688.964-65", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseMatricula() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "12356", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseTelefone() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "(90) 1234-3344", "Nom@email");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
	@Test
	public void testEqualsFalseEmail() throws ClienteException {
		Teacher p = new Teacher("Nome", "868.563.327-34", "123456", "", "Nome@email");
		Teacher q = new Teacher("Nome", "868.563.327-34", "123456", "", "Nom@el");
		assertFalse("Teste do E-mail do Professor", p.equals(q));
	}
}
