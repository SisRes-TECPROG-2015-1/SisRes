package test.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import model.Room;
import model.Teacher;
import model.TeacherRoomReserve;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.ClassRoomDAO;
import persistence.FactoryConnection;
import persistence.TeacherDAO;
import persistence.TeacherRoomReserveDAO;


import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class TeacherRoomReserveDAOTest {

	
	private static Room room_a;
	private static Room room_b;
	private static Teacher teacher1;
	private static Teacher teacher2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		room_a = new Room("S2", "Room de aula", "130");
		room_b = new Room("I6", "Laboratorio", "40");
		teacher1 = new Teacher("teacherUm", "490.491.781-20", "58801", "3333-3333", "prof@email");
		teacher2 = new Teacher("teacherDois", "040.757.021-70", "36106", "3628-3079", "prof@email");
		
		ClassRoomDAO.getInstance().includeARoom(room_a);
		ClassRoomDAO.getInstance().includeARoom(room_b);
		TeacherDAO.getInstance().includeNewTeacher(teacher1);
		TeacherDAO.getInstance().includeNewTeacher(teacher2);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ClassRoomDAO.getInstance().excludeRoom(room_a);
		ClassRoomDAO.getInstance().excludeRoom(room_b);
		TeacherDAO.getInstance().excludeATeacher(teacher1);
		TeacherDAO.getInstance().excludeATeacher(teacher2);	
	}

	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia", TeacherRoomReserveDAO.getInstance() instanceof TeacherRoomReserveDAO);
	}
	
	@Test
	public void testIncluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Aula de reforco", teacher1);
		
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.executeQuery("DELETE FROM reservation_room_teacher WHERE date = \"20/12/34\";");
		
		assertTrue("Teste de Inclusao.", resultado);
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(null);
	}
	@Test (expected= ReserveException.class)
	public void testReservaPorteacherInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Reuniao", new Teacher("Inexistente", "501.341.852-69", "456678", "", ""));
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", new Room("222", "Laboratorio", "20"),
				"Grupo de Estudos", teacher1);
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirRoomReservadaProf() throws ReserveException, ClientException, 
											PatrimonyException, SQLException 
	{
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Aula de MDS",  teacher1);
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Aula de PDS",  teacher2);
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM reservation_room_teacher;");
		
		}
		
	}
	@Test
	public void testIncluirRoomReservadastudent() throws ReserveException, ClientException, 
											PatrimonyException, SQLException 
	{
		this.executeQuery("INSERT INTO student (name, cpf, registration) " +
		"VALUES (\"student\", \"257.312.954-33\", \"33108\");");
		this.executeQuery("INSERT INTO reservation_room_student (id_student,id_room,finality,hour,date, reserved_chairs) "+
		"VALUES ((SELECT id_student FROM student WHERE cpf = \"257.312.954-33\")," +
				"(SELECT id_room FROM room WHERE code = \"S2\")," +
				"\"Estudo de Fisica\", \"08:00\", \"20/12/2013\", 20);");
		
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/13", "8:00", room_a,
				"Aula de EA",  teacher1);
		
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
			
		boolean resultadoProf = this.inDB(reserva);
		boolean resultadostudent = this.inDBGeneric("SELECT * FROM reservation_room_student " +
				"INNER JOIN room ON room.id_room = reservation_room_student.id_room " +
				"INNER JOIN student ON student.id_student = reservation_room_student.id_student;");
		
				
		this.executeQuery("DELETE FROM student;");
		this.executeQuery("DELETE FROM reservation_room_student;");
		this.executeQuery("DELETE FROM reservation_room_teacher;");
		
		
		assertTrue("Room reservada por student", (resultadoProf && !resultadostudent));
		
		}
	public void testIncluirdatePassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/1990", "8:00", room_a,
				"Grupo de Estudos", teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_teacher(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirdatePassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/01/2013", "8:00", room_a,
				"Grupo de Estudos", teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_teacher(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirdatePassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dateAtualAMais(-100000000), this.hourAtual(), room_a,
				"Grupo de Estudos", teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_teacher(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirhourPassouhour() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dateAtual(),
				 this.hourAtualAMais(-10000000), room_a,
				"Grupo de Estudos",  teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_teacher(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirhourPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dateAtual(),
				this.hourAtualAMais(-100000), room_a,
				"Grupo de Estudos", teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_teacher(reserva);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testIncluirteacherOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/13", "8:00", room_a,
				"Aulao pre-prova", teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/13", "8:00", room_a,
				"Aulao pre-prova", teacher1);
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva2);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
		
		
	}
	@Test
	public void testAlterar() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva1 = new TeacherRoomReserve("20/12/13", "8:00", room_a,
				"Pesquisa", teacher1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("21/12/34", "19:00", room_a,
				"Pesquisa", teacher1);
		
		this.executeQuery("INSERT INTO " +
				"reservation_room_teacher (id_teacher, id_room, finality, hour, date) " +
				"VALUES ( " + values_reservation_room_teacher(reserva1) + " );");
		
		
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva1, reserva2);
		
		boolean resultado = this.inDB(reserva2);
		
		this.executeQuery("DELETE FROM reservation_room_teacher;");
		
		assertTrue("Teste de Alteracao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_AntigoNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(null, reserva);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_NovoNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
			"Grupo de pesquisa", teacher1);
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, null);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
		
	}
	public void testAlterardatePassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos",  teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/1990", "8:00", room_a,
				"Grupo de Estudos", teacher2);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_teacher(reserva);
		if(this.inDB(reserva2))
			this.delete_from_teacher(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardatePassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos",  teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/01/2013", "8:00", room_a,
				"Grupo de Estudos", teacher2);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_teacher(reserva);
		if(this.inDB(reserva2))
			this.delete_from_teacher(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardatePassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos",  teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dateAtualAMais(-100000000), this.hourAtual(), room_a,
				"Grupo de Estudos",  teacher1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_teacher(reserva);
		if(this.inDB(reserva2))
			this.delete_from_teacher(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarhourPassouhour() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos",  teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dateAtual(),
				 this.hourAtualAMais(-10000000), room_a,
				"Grupo de Estudos",  teacher1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_teacher(reserva);
		if(this.inDB(reserva2))
			this.delete_from_teacher(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarhourPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos",  teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dateAtual(),
				this.hourAtualAMais(-100000), room_a,
				"Grupo de Estudos",  teacher1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_teacher(reserva);
		if(this.inDB(reserva2))
			this.delete_from_teacher(reserva2);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testAlterarJaInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("27/12/34", "9:00", room_b,
				"Grupo d", teacher2);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva2, reserva);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
		
	}
	
	@Test (expected= ReserveException.class)
	public void testAlterarhourReservaFeita() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "9:00", room_a,
				"Grupo de pesquisa", teacher1);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		TeacherRoomReserve reserva3 = new TeacherRoomReserve("20/12/34", "8:00", room_b,
				"Grupo de Estudos", teacher1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva2, reserva3);
		} finally {
		
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardateDifRoomOcupada() throws ReserveException, ClientException, PatrimonyException, SQLException {
		this.executeQuery("INSERT INTO teacher (name, cpf, registration) " +
				"VALUES (\"teacher\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
				"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"257.312.954-33\")," +
						"(SELECT id_room FROM room WHERE code = \"S2\")," +
						"\"Aula de Calculo\", \"8:00\", \"20/12/34\");");
		
				
		
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", room_a,
				"Grupo de Pesquisa", teacher1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Estudos", teacher1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM teacher WHERE cpf = \"257.312.954-33\"");
		this.executeQuery("DELETE FROM reservation_room_teacher");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarteacherInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de pesquisa", new Teacher("Nao Existe", "501.341.852-69", "456678", "", ""));
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", room_a,
				"Grupo de pesquisa", teacher1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", new Room("S5", "Room de aula", "120"),
				"Grupo de Estudos", teacher1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reservation_room_teacher;");
		}
	}
	@Test
	public void testExcluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Grupo de Pesquisa", teacher1);
		
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
				"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"" + reserva.getTeacher().getCpf() + "\")," + 
						"(SELECT id_room FROM room WHERE code = \"" + room_a.getCode() + "\")," +
						"\"Grupo de Pesquisa\", \"08:00\", \"20/12/2034\");");
		
		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		this.executeQuery("DELETE FROM reservation_room_teacher;");
		
		assertFalse("Teste de Exclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(null);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Reuniao", teacher1);

		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(reserva);
		
		this.executeQuery("DELETE FROM reservation_room_teacher;");
	}
	
		
	@Test
	public void testBuscarPordate() throws SQLException, PatrimonyException, ClientException, ReserveException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", room_a,
				"Reuniao", teacher1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "19:00", room_a,
				"Reuniao", teacher1);
		
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
				"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"" + reserva.getTeacher().getCpf() + "\")," + 
						"(SELECT id_room FROM room WHERE code = \"" + room_a.getCode() + "\")," +
						"\"" + reserva.getFinality() + "\", \"" +
						reserva.getHour() + "\", \"" + reserva.getDate() +"\");");
		
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
				"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"" + reserva2.getTeacher().getCpf() + "\")," + 
						"(SELECT id_room FROM room WHERE code = \"" + room_a.getCode() + "\")," +
						"\"" + reserva2.getFinality() + "\", \"" +
						reserva2.getHour() + "\", \"" + reserva2.getDate() +"\");");
		
		Vector<TeacherRoomReserve> vet = TeacherRoomReserveDAO.getInstance().getTeacherReservedRoomsByDay("20/12/2034");
		
		
		boolean resultado = false;
		boolean resultado2 = false;
		
		Iterator<TeacherRoomReserve> it = vet.iterator();
		while(it.hasNext()){
			TeacherRoomReserve obj = it.next();
			if(obj.equals(reserva))
				resultado = true;
			else if(obj.equals(reserva2))
				resultado2 = true;
		}
		
		this.executeQuery("DELETE FROM reservation_room_teacher WHERE date = \"20/12/2034\"");
		
		assertTrue("Teste de busca por date", resultado && resultado2);
	}
		
	private String select_id_teacher(Teacher p){
		return "SELECT id_teacher FROM teacher WHERE " +
				"teacher.name = \"" + p.getName() + "\" and " +
				"teacher.cpf = \"" + p.getCpf() + "\" and " +
				"teacher.fone = \"" + p.getFone() + "\" and " +
				"teacher.email = \"" + p.getEmail() + "\" and " +
				"teacher.registration = \"" + p.getRegistration() + "\"";
	}
	private String select_id_room(Room room){
		return "SELECT id_room FROM room WHERE " +
				"room.code = \"" + room.getCode() + "\" and " +
				"room.description = \"" + room.getDescription() +  "\" and " +
				"room.capacity = " + room.getCapacity();
	}
	private String where_reservation_room_teacher(TeacherRoomReserve r){
		return " WHERE " +
		"id_teacher = ( " + select_id_teacher(r.getTeacher()) + " ) and " +
		"id_room = ( " + select_id_room(r.getRoom()) + " ) and " +
		"finality = \"" + r.getFinality() + "\" and " +
		"hour = \"" + r.getHour() + "\" and " +
		"date = \"" + r.getDate() + "\"";
	}
	private String values_reservation_room_teacher(TeacherRoomReserve r){
		return "( " + select_id_teacher(r.getTeacher()) + " ), " +
		"( " + select_id_room(r.getRoom()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHour() + "\", " +
		"\"" + r.getDate() + "\"";
	}
	/*private String atibutes_value_reservation_room_teacher(ReservaRoomteacher r){
		return "id_teacher = ( " + select_id_teacher(r.getteacher()) + " ), " +
		"id_room = ( " + select_id_room(r.getRoom()) + " ), " +
		"finality = \"" + r.getfinality() + "\", " +
		"hour = \"" + r.getHour() + "\", " +
		"date = \"" + r.getdate() + "\"";
	}*/

	private String insert_into(TeacherRoomReserve r){
		return "INSERT INTO " +
				"reservation_room_teacher (id_teacher, id_room, finality, hour, date) " +
				"VALUES ( " + values_reservation_room_teacher(r) + " );";
	}
	
	private String delete_from_teacher(TeacherRoomReserve r){
		return "DELETE FROM reservation_room_teacher " + this.where_reservation_room_teacher(r) + " ;";
	}
	/*
	private String delete_from_student(ReservaRoomteacher r){
		return "DELETE FROM reservation_room_student WHERE " +
				"hour = \"" + r.getHour() + "\" and " +
				"date = \"" + r.getdate() +  "\" ;";
	}
	
	private String update(ReservaRoomteacher r, ReservaRoomteacher r2){
		return "UPDATE reservation_room_teacher SET " + 
				this.atibutes_value_reservation_room_teacher(r2) +
				this.where_reservation_room_teacher(r) + " ;";
	}
*/
	
	
	private String dateAtual(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		return formatador.format(date);
	}
	
	private String hourAtual(){
		Date date = new Date(System.currentTimeMillis());
		return date.toString().substring(11, 16);
	}
	
	private String hourAtualAMais(int fator){
		Date date = new Date(System.currentTimeMillis()+fator);
		return date.toString().substring(11, 16);
	}
	
	private String dateAtualAMais(int fator){
		Date date = new Date(System.currentTimeMillis()+fator);
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		return formatador.format(date);
	}
			
	private boolean inDB(TeacherRoomReserve reserva) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reservation_room_teacher " + 
								this.where_reservation_room_teacher(reserva) + " ;");
	}
	private boolean inDBGeneric(String query) throws SQLException{
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
	private void executeQuery(String msg) throws SQLException{
		Connection con =  FactoryConnection.getInstance().getConnection();
		PreparedStatement pst = con.prepareStatement(msg);
		pst.executeUpdate();		
		pst.close();
		con.close();
	}


}
