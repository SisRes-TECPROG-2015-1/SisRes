package test.model;

import static org.junit.Assert.*;

import model.Student;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import exception.ClientException;

public class AlunoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	

	@Test
	public void testInstance() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Instanciamento do Student", a instanceof Student);
	}
	
	@Test
	public void testNome() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do Nome do Student", "Nome" == a.getName());
	}

	@Test
	public void testCpf() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do CPF do Student", "040.757.021-70" == a.getCpf());
	}
	
	@Test
	public void testMatricula() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste da Matricula do Student", "123456" == a.getRegistration());
	}
	
	@Test
	public void testTelefone() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste de Telefone do Student", "1234-5678" == a.getFone());
	}
	
	@Test
	public void testEmail() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "Nome@email");
		assertTrue("Teste do E-mail do Student", "Nome@email" == a.getEmail());
	}

	
	
	@Test (expected= ClientException.class)
	public void testNomeVazio() throws ClientException {
		new Student("", "040.757.02170", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClientException.class)
	public void testNomeNumero() throws ClientException {
		new Student("Nome12", "040.757.021-70", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testNomeCaractere() throws ClientException {
		new Student("Nome*", "040.757.021-70", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testNomeNulo() throws ClientException {
		new Student(null, "040.757.021-70", "123456", "1234-5678", "Nome@email");
	}

	
	
	@Test (expected= ClientException.class)
	public void testCpfVazio() throws ClientException {
		new Student("Nome", "", "123456", "1234-5678", "Nome@email");
	}

	@Test (expected= ClientException.class)
	public void testCpfLetras() throws ClientException {
		new Student("Nome", "O40.757.021-7O", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testCpfDespadronizado() throws ClientException {
		new Student("Nome", "04075702170", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected = ClientException.class)
	public void testCpfInvalido() throws ClientException {
		new Student("Nome", "123.456.789-90", "123456", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testCpfNulo() throws ClientException {
		new Student("Nome", null, "123456", "1234-5678", "Nome@email");
	}
	
	
	
	@Test (expected= ClientException.class)
	public void testMatriculaVazia() throws ClientException {
		new Student("Nome", "040.757.021-70", "", "1234-5678", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testMatriculaNula() throws ClientException {
		new Student("Nome", "040.757.021-70", null, "1234-5678", "Nome@email");
	}
	
	
	
	@Test
	public void testTelefoneVazio() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		assertTrue("Teste de Telefone Vazio do Student", "" == a.getFone());
	}
	
	@Test (expected= ClientException.class)
	public void testTelefoneDespadronizado() throws ClientException {
		new Student("Nome", "040.757.021-70", "123456", "6133333333", "Nome@email");
	}
	
	@Test (expected= ClientException.class)
	public void testTelefoneNulo() throws ClientException {
		new Student("Nome", "040.757.021-70", "123456", null, "Nome@email");
	}

	
	
	@Test
	public void testEmailVazio() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "1234-5678", "");
		assertTrue("Teste de Email Vazio do Student", "" == a.getEmail());
	}
	
	@Test (expected= ClientException.class)
	public void testEmailNulo() throws ClientException {
		new Student("Nome", "040.757.021-70", "123456", "9999-9999", null);
	}
	@Test 
	public void testToString() throws ClientException{
		Student student = new Student("Nome", "040.757.021-70", "123456", "9999-9999", "Nome@email");
		assertNotNull(student.toString());
	}
	
	
	@Test
	public void testEqualsTrue() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student ae = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		assertTrue("Teste do E-mail do Student", a.equals(ae));
	}
	
	@Test
	public void testEqualsFalseNome() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student oa = new Student("NomeDiferente", "040.757.021-70", "12356", "(90) 9999-9999", "Nom@email");
		assertFalse("Teste do E-mail do Student", a.equals(oa));
	}
	@Test
	public void testEqualsFalseCpf() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student oa = new Student("Nome", "490.491.781-20", "12356", "(90) 9999-9999", "Nom@email");
		assertFalse("Teste do E-mail do Student", a.equals(oa));
	}
	@Test
	public void testEqualsFalseMatricula() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student oa = new Student("Nome", "040.757.021-70", "12356", "(90) 9999-9999", "Nom@email");
		assertFalse("Teste do E-mail do Student", a.equals(oa));
	}
	@Test
	public void testEqualsFalseTelefone() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student oa = new Student("Nome", "040.757.021-70", "123456", "(90) 9999-9999", "Nom@email");
		assertFalse("Teste do E-mail do Student", a.equals(oa));
	}
	@Test
	public void testEqualsFalseEmail() throws ClientException {
		Student a = new Student("Nome", "040.757.021-70", "123456", "", "Nome@email");
		Student oa = new Student("Nome", "040.757.021-70", "123456", "", "Nom@el");
		assertFalse("Teste do E-mail do Studentr", a.equals(oa));
	}
}
