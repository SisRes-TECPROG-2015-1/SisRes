package test.model;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.Student;
import model.StudentRoomReserve;
import model.Room;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ResSalaAlunoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testInstance() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		assertTrue("Teste de Instancia.", reserva instanceof StudentRoomReserve);
	}
	
	
	
	@Test (expected= ReserveException.class)
	public void testStudentNulo() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = null;
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", "30", Student);
	}
	
	
	
	@Test (expected= ReserveException.class)
	public void testCadeirasNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", null, Student);
	}
	
	@Test (expected= ReserveException.class)
	public void testCadeirasVazias() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", "     ", Student);
	}
	
	@Test(expected= ReserveException.class)
	public void testCadeirasDespadronizadas() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", "3A-", Student);
	}
	
	@Test (expected= ReserveException.class)
	public void testCadeirasAcimaCapacidade() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", "121", Student);
	}
	
	
	
	@Test (expected= ReserveException.class)
	public void testFinalidadeNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, null, "11", Student);
	}
	@Test (expected= ReserveException.class)
	public void testFinalidadeVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "     ", "11", Student);
	}
	
	
	
	@Test (expected= ReserveException.class)
	public void testRoomNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = null;
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Grupo de Estudos", "30", Student);
	}
	
	
	
	@Test
	public void testHora() throws PatrimonyException, ClientException, ReserveException {
		String hora = this.horaAtualAMais(100000000);
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(),
				hora, Room,
				"Grupo de Estudos", "120", Student);
		assertTrue("", reserva.getHour() == hora);
	}
	@Test (expected= ReserveException.class)
	public void testHoraNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), null, Room, "Grupo de Estudos", "120", Student);
	}
	@Test (expected= ReserveException.class)
	public void testHoraVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), "    ", Room, "Grupo de Estudos", "120", Student);
	}
	@Test (expected= ReserveException.class)
	public void testHoraDespadronizada() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(this.dataAtual(), "1000", Room, "Grupo de Estudos", "120", Student);
	}
	
	
	
	@Test
	public void testData() throws PatrimonyException, ClientException, ReserveException {
		String data = "12/2/33";
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(data,
				"8:00", Room, "Grupo de Estudos", "120", Student);

		assertTrue("", reserva.getDate().equals("12/02/2033"));
	}
	@Test (expected= ReserveException.class)
	public void testDataNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve(null, this.horaAtual(), Room, "Grupo de Estudos", "120", Student);
	}
	@Test (expected= ReserveException.class)
	public void testDataVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve("    ", this.horaAtual(), Room, "Grupo de Estudos", "120", Student);
	}
	@Test (expected= ReserveException.class)
	public void testDataDespadronizada() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		new StudentRoomReserve("12/q2/2030", this.horaAtual(), Room, "Grupo de Estudos", "120", Student);
	}
	
	
	@Test
	public void testEqualsTrue() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		assertTrue("Teste de Equals.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseRoom() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Room Room2 = new Room("1233", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room2,
				"Grupo de Estudos", "120", Student);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseStudent() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		Student Student2 = new Student("testInstanceD", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student2);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseData() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtualAMais(100000000), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseHora() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtualAMais(10000000), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseFinalidade() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos So q n", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseCadierasReservadas() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Student Student = new Student("testInstance", "501.341.852-69", "456678", "", "");
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "120", Student);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", "1", Student);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	
	
	private String dataAtual(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		return formatador.format(date);
	}
	
	private String horaAtual(){
		Date date = new Date(System.currentTimeMillis());
		return date.toString().substring(11, 16);
	}
	
	private String horaAtualAMais(int fator){
		Date date = new Date(System.currentTimeMillis()+fator);
		return date.toString().substring(11, 16);
	}
	
	private String dataAtualAMais(int fator){
		Date date = new Date(System.currentTimeMillis()+fator);
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		return formatador.format(date);
	}
}
