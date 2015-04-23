package test.model;

import static org.junit.Assert.*;
import model.Room;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import exception.PatrimonyException;

public class SalaTest {
	
	Room Room;
	
	@Before
	public void setUp() throws PatrimonyException{
		Room = new Room("codigo", "descricao", "1");
	}
	
	@After
	public void tearDown() throws PatrimonyException{
		Room = null;
	}
	
	@Test
    public void testInstance() throws PatrimonyException {
		assertTrue(new Room("codigo", "descricao","1") instanceof Room);
	}
	
	@Test
	public void testEquals() throws PatrimonyException {
		setUp();
		Room Room_new = new Room("codigo", "descricao", "1");
		assertTrue("Falha no Equals.", Room_new.equals(Room));
		Room_new = null;
		tearDown();
	}
	
	@Test
	public void testNotEqualsCapacidade() throws PatrimonyException {
		Room s = new Room("codigo", "descricao", "1");
		Room s2 = new Room("codigo", "descricao", "2");
		assertFalse("Falha no Equals.", s.equals(s2));

	}
	
	@Test
	public void testNotEqualsDescricao() throws PatrimonyException {
		setUp();
		Room Room_new = new Room("codigo", "d", "1");
		assertFalse("Falha no Equals.", Room.equals(Room_new));
		Room_new = null;
		tearDown();
	}
	
	@Test
	public void testNotEqualsCodigo() throws PatrimonyException {
		setUp();
		Room Room_new = new Room("c", "descricao", "1");
		assertFalse("Falha no Equals.", Room.equals(Room_new));
		Room_new = null;
		tearDown();
	}
	
	@Test
	public void testCodigo() throws PatrimonyException {
		setUp();
		assertEquals("codigo diferente instanciado", "codigo", Room.getCode());
		tearDown();
	}
	
	@Test
	public void testDescricao() throws PatrimonyException {
		setUp();
		assertEquals("Descricao diferente instanciada", "descricao", Room.getDescription());
		tearDown();
	}	
	
	@Test
	public void testCapacidade() throws PatrimonyException {
		setUp();
		assertEquals("Capacidade diferente instanciada", "1", Room.getCapacity());
		tearDown();
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCapacidadeNegativo() throws PatrimonyException {
		setUp();
		Room.setCapacity("-1");
		assertEquals("Capacidade diferente instanciada", "1", Room.getCapacity());
		tearDown();
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCapacidadeLetra() throws PatrimonyException {
		setUp();
		Room.setCapacity("a");
		assertEquals("Capacidade diferente instanciada", "1", Room.getCapacity());
		tearDown();
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testDescricaoVazia() throws PatrimonyException {
		new Room("codigo", "", "1");
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCapacidadeVazia() throws PatrimonyException {
		new Room("codigo", "descricao", "");
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCodigoVazio() throws PatrimonyException {
		new Room("", "descricao","1");
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCodigoNulo() throws PatrimonyException {
		new Room(null, "descricao", "1");
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testDescricaoNulo() throws PatrimonyException {
		new Room("codigo", null,"1");
	}
	
	@Test(expected = exception.PatrimonyException.class)
	public void testCapacidadeNulo() throws PatrimonyException {
		new Room("codigo", "descricao", null);
	}
}
