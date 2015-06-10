package test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import model.Student;
import model.StudentRoomReserve;
import model.Room;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.StudentDAO;
import persistence.FactoryConnection;
import persistence.StudentRoomReserveDAO;
import persistence.ClassRoomDAO;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ResSalaAlunoDAOTest {

	private static Room Room1;
	private static Room Room2;
	private static Student Student1;
	private static Student Student2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Room1 = new Room("123", "Room de Aula", "120");
		Room2 = new Room("543", "Laboratorio", "30");
		Student1 = new Student("testInstance", "501.341.852-69", "456678", "", "");
		Student2 = new Student("Incluindo registration Igual", "490.491.781-20", "345543", "2222-2222", "Student2@email");
		
		StudentDAO.getInstance().includeNewStudent(Student1);
		StudentDAO.getInstance().includeNewStudent(Student2);
		ClassRoomDAO.getInstance().includeARoom(Room1);
		ClassRoomDAO.getInstance().includeARoom(Room2);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StudentDAO.getInstance().deleteStudent(Student1);
		StudentDAO.getInstance().deleteStudent(Student2);
		ClassRoomDAO.getInstance().excludeRoom(Room1);
		ClassRoomDAO.getInstance().excludeRoom(Room2);
	}

	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia", StudentRoomReserveDAO.getInstance() instanceof StudentRoomReserveDAO);
	}
	@Test
	public void testSingleton() {
		assertSame("Teste de Singleton", StudentRoomReserveDAO.getInstance(), StudentRoomReserveDAO.getInstance());
	}
	
	
	@Test
	public void testIncluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
		assertTrue("Teste de Inclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testIncluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(null);
	}
	@Test (expected= ReserveException.class)
	public void testIncluirStudentInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", new Student("tepp", "501.341.852-69", "456678", "", ""));
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		boolean resultado = this.inDB(reserva);

		if(resultado)
			this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", new Room("22277883", "Laboratorio", "120"),
				"Grupo de Estudos", "120", Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		boolean resultado = this.inDB(reserva);

		if(resultado)
			this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirRoomReservadaProf() throws ReserveException, ClientException, 
											PatrimonyException, SQLException 
	{
		this.executeQuery("INSERT INTO teacher (name, cpf, registration) " +
		"VALUES (\"teacherDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
		"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"257.312.954-33\")," +
				"(SELECT id_room FROM Room WHERE code = \"123\")," +
				"\"Aula de Calculo\", \"08:00\", \"20/12/2034\");");
		
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "60", Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		if(this.inDB(reserva))
			this.delete_from(reserva);
		
		this.executeQuery("DELETE FROM teacher WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reservation_room_teacher WHERE date = \"20/12/2034\";");
		
		}
		
	}
	@Test (expected= ReserveException.class)
	public void testIncluirStudentOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room2,
				"Grupo de Estudos", ""+Room2.getCapacity(), Student1);
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva2);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
			if(this.inDB(reserva2))
				this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirRoomCheia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "60", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "70", Student2);
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva2);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
			if(this.inDB(reserva2))
				this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirdatePassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/1990", "8:00", Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirdatePassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/01/2013", "8:00", Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirdatePassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dateAtualAMais(-100000000), this.hourAtual(), Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirhourPassouhour() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dateAtual(),
				 this.hourAtualAMais(-10000000), Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirhourPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dateAtual(),
				this.hourAtualAMais(-100000), Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	
	
	@Test
	public void testAlterar() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", Room1,
				"Grupo de Estudos", "120", Student1);
		
		this.insert_into(reserva);
		
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		
		boolean resultado = this.inDB(reserva2);
		
		if(resultado)
			this.delete_from(reserva2);
		if(this.inDB(reserva))
			this.delete_from(reserva);
		assertTrue("Teste de Inclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarNulo1() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(null, reserva);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarNulo2() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
			"Grupo de Estudos", "120", Student1);
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, null);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
			if(this.inDB(reserva2))
				this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarJaInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("27/12/34", "9:00", Room2,
				"Grupo d", ""+Room2.getCapacity(), Student2);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva2, reserva);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarhourDifStudentOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "9:00", Room1,
				"Grupo de Estudos", "120", Student1);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		StudentRoomReserve reserva3 = new StudentRoomReserve("20/12/34", "8:00", Room2,
				"Grupo de Estudos", ""+Room2.getCapacity(), Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva2, reserva3);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		if(this.inDB(reserva3))
			this.delete_from(reserva3);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardateDifRoomOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		this.executeQuery("INSERT INTO teacher (name, cpf, registration) " +
				"VALUES (\"teacherDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reservation_room_teacher (id_teacher,id_room,finality,hour,date) "+
				"VALUES ((SELECT id_teacher FROM teacher WHERE cpf = \"257.312.954-33\")," +
						"(SELECT id_room FROM room WHERE code = \"123\")," +
						"\"Aula de Calculo\", \"08:00\", \"20/12/2034\");");
				
		
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		
		this.executeQuery("DELETE FROM teacher WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reservation_room_teacher WHERE date = \"20/12/2034\";");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarStudentInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", new Student("tepp", "501.341.852-69", "456678", "", ""));
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", new Room("22277883", "Laboratorio", "120"),
				"Grupo de Estudos", "120", Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarNaoHaCadeira() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "20", Student2);
		StudentRoomReserve reserva3 = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student2);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva2, reserva3);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		if(this.inDB(reserva3))
			this.delete_from(reserva3);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardatePassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/1990", "8:00", Room1,
				"Grupo de Estudos", "20", Student2);
		this.insert_into(reserva);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardatePassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/01/2013", "8:00", Room1,
				"Grupo de Estudos", "20", Student2);
		this.insert_into(reserva);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterardatePassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dateAtualAMais(-100000000), this.hourAtual(), Room1,
				"Grupo de Estudos", "60", Student1);
		this.insert_into(reserva);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarhourPassouhour() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dateAtual(),
				 this.hourAtualAMais(-10000000), Room1,
				"Grupo de Estudos", "60", Student1);
		this.insert_into(reserva);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarhourPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dateAtual(),
				this.hourAtualAMais(-100000), Room1,
				"Grupo de Estudos", "60", Student1);
		this.insert_into(reserva);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		}
	}
	
	
	
	@Test
	public void testExcluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);
		this.insert_into(reserva);
		
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
		assertFalse("Teste de Exclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(null);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "120", Student1);

		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
	}
	
	
	@Test
	public void testBuscarPorDia() throws SQLException, PatrimonyException, ClientException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", Room1,
				"Grupo de Estudos", "40", Student1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", Room1,
				"Grupo de Estudos", "50", Student1);
		
		this.insert_into(reserva);
		this.insert_into(reserva2);
		Vector<StudentRoomReserve> vet = StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByDay("21/12/34");
		this.delete_from(reserva);
		this.delete_from(reserva2);
		
		boolean resultado = false;
		boolean resultado2 = false;
		
		Iterator<StudentRoomReserve> it = vet.iterator();
		while(it.hasNext()){
			StudentRoomReserve obj = it.next();
			if(obj.equals(reserva))
				resultado = true;
			else if(obj.equals(reserva2))
				resultado2 = true;
		}
		
		assertTrue("Teste de busca", resultado && resultado2);
	}
	@Test
	public void testBuscarPorhour() throws SQLException, PatrimonyException, ClientException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "9:00", Room1,
				"Grupo de Estudos", "40", Student1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "09:00", Room1,
				"Grupo de Estudos", "50", Student1);
		
		this.insert_into(reserva);
		this.insert_into(reserva2);
		Vector<StudentRoomReserve> vet = StudentRoomReserveDAO.getInstance().getStudentReservedRoomsByHour("09:00");
		this.delete_from(reserva);
		this.delete_from(reserva2);
		
		boolean resultado = false;
		boolean resultado2 = false;
		
		Iterator<StudentRoomReserve> it = vet.iterator();
		while(it.hasNext()){
			StudentRoomReserve obj = it.next();
			if(obj.equals(reserva))
				resultado = true;
			else if(obj.equals(reserva2))
				resultado2 = true;
		}
		
		assertTrue("Teste de busca", resultado && resultado2);
	}
	
	
	@Test
	public void testCadeirasDisponiveis() throws SQLException, PatrimonyException, ClientException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "19:00", Room1,
				"Grupo de Estudos", "40", Student1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", Room1,
				"Grupo de Estudos", "50", Student1);
		
		this.insert_into(reserva);
		this.insert_into(reserva2);
		int c = StudentRoomReserveDAO.getInstance().getAvailableChairs(Room1, "21/12/34", "19:00");
		this.delete_from(reserva);
		this.delete_from(reserva2);
		assertEquals("Teste de disponibilidade de Cadeiras", c, 30);
	}

	
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
	
	
	private String select_id_student(Student a){
		return "SELECT id_student FROM student WHERE " +
				"student.name = \"" + a.getName() + "\" and " +
				"student.cpf = \"" + a.getCpf() + "\" and " +
				"student.fone = \"" + a.getFone() + "\" and " +
				"student.email = \"" + a.getEmail() + "\" and " +
				"student.registration = \"" + a.getRegistration() + "\"";
	}
	private String select_id_room(Room room){
		return "SELECT id_room FROM room WHERE " +
				"room.code = \"" + room.getCode() + "\" and " +
				"room.description = \"" + room.getDescription() +  "\" and " +
				"room.capacity = " + room.getCapacity();
	}
	private String where_reservation_room_student(StudentRoomReserve r){
		return " WHERE " +
		"id_student = ( " + select_id_student(r.getStudent()) + " ) and " +
		"id_room = ( " + select_id_room(r.getRoom()) + " ) and " +
		"finality = \"" + r.getFinality() + "\" and " +
		"hour = \"" + r.getHour() + "\" and " +
		"date = \"" + r.getDate() + "\" and " +
		"reserved_chairs = " + r.getReservedChairs();
	}
	private String values_reservation_room_student(StudentRoomReserve r){
		return "( " + select_id_student(r.getStudent()) + " ), " +
		"( " + select_id_room(r.getRoom()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHour() + "\", " +
		"\"" + r.getDate() + "\", " +
		r.getReservedChairs();
	}
	private void insert_into(StudentRoomReserve r){
		try {
			this.executeQuery("INSERT INTO " +
					"reservation_room_student (id_student, id_room, finality, hour, date, reserved_chairs) " +
					"VALUES ( " + values_reservation_room_student(r) + " );");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void delete_from(StudentRoomReserve r){
		try {
			this.executeQuery("DELETE FROM reservation_room_student " + 
								this.where_reservation_room_student(r) + " ;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private boolean inDB(StudentRoomReserve r) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reservation_room_student " + 
								this.where_reservation_room_student(r) + " ;");
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
