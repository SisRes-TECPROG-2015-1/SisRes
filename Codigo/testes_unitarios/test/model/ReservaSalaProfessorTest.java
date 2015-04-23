package test.model;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.Teacher;
import model.TeacherRoomReserve;
import model.Room;

import org.junit.Test;

import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ReservaSalaProfessorTest {

	
	@Test
	public void testInstance() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor);
		assertTrue("Teste de Instancia.", reserva instanceof TeacherRoomReserve);
	}

	
	
	
	@Test (expected= ReserveException.class)
	public void testProfessorNulo() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = null;
		new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Pesquisa", professor);
	}
	
	@Test (expected= ReserveException.class)
	public void testFinalidadeNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room, null, professor);
	}
	@Test (expected= ReserveException.class)
	public void testFinalidadeVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room, "     ", professor);
	}
	
	
	
	@Test (expected= ReserveException.class)
	public void testRoomNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = null;
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room, "Pesquisa", professor);
	}
	
	
	
	@Test
	public void testHora() throws PatrimonyException, ClientException, ReserveException {
		String hora = this.horaAtualAMais(100000000);
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(),
				hora, Room, "Reuniao", professor);
		assertTrue("", reserva.getHour() == hora);
	}
	@Test (expected= ReserveException.class)
	public void testHoraNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), null, Room, "Reuniao", professor);
	}
	@Test (expected= ReserveException.class)
	public void testHoraVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), "    ", Room, "Pesquisa", professor);
	}
	@Test (expected= ReserveException.class)
	public void testHoraDespadronizada() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(this.dataAtual(), "1000", Room, "Reuniao", professor);
	}
	
	@Test
	public void testData() throws PatrimonyException, ClientException, ReserveException {
		String data = "12/2/33";
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(data,
				this.horaAtual(), Room, "Aula de DS", professor);

		assertTrue("", reserva.getDate().equals("12/02/2033"));
	}
	@Test (expected= ReserveException.class)
	public void testDataNula() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve(null, this.horaAtual(), Room, "Aula de C1", professor);
	}
	@Test (expected= ReserveException.class)
	public void testDataVazia() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		new TeacherRoomReserve("    ", this.horaAtual(), Room, "Aula de fisica", professor);
	}
	
	@Test (expected= ReserveException.class)
	public void testDataComChar() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "501.341.852-69", "456678", "", "");
		new TeacherRoomReserve("12/q2/2030", this.horaAtual(), Room, "Grupo de Estudos", professor);
	}
	
	@Test
	public void testEqualsTrue() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reforco", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reforco", professor);
		assertTrue("Teste de Equals.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseRoom() throws PatrimonyException, ClientException, ReserveException {//mesma reserva mas em Rooms dif
		Room Room = new Room("123", "Room de Aula", "120");
		Room Room2 = new Room("1233", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room2,
				"Reuniao", professor);
		
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseProfessor() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		Teacher professor2 = new Teacher("testInstanceD", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor2);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseData() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtualAMais(100000000), this.horaAtual(), Room,
				"Grupo de Estudos", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Grupo de Estudos", professor);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseHora() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtualAMais(10000000), Room,
				"Reuniao", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor);
		assertFalse("Teste de Equals False.", reserva.equals(reserva2));
	}
	@Test
	public void testEqualsFalseFinalidade() throws PatrimonyException, ClientException, ReserveException {
		Room Room = new Room("123", "Room de Aula", "120");
		Teacher professor = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "Node@email");
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Reuniao", professor);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(), this.horaAtual(), Room,
				"Pesquisa", professor);
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
