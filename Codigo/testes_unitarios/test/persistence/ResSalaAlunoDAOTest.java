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
		Student2 = new Student("Incluindo Matricula Igual", "490.491.781-20", "345543", "2222-2222", "Student2@email");
		
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
		this.executeQuery("INSERT INTO professor (nome, cpf, matricula) " +
		"VALUES (\"ProfessorDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reserva_Room_professor (id_professor,id_Room,finalidade,hora,data) "+
		"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"257.312.954-33\")," +
				"(SELECT id_Room FROM Room WHERE codigo = \"123\")," +
				"\"Aula de Calculo\", \"08:00\", \"20/12/2034\");");
		
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "60", Student1);
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		if(this.inDB(reserva))
			this.delete_from(reserva);
		
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reserva_Room_professor WHERE data = \"20/12/2034\";");
		
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
	public void testIncluirDataPassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
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
	public void testIncluirDataPassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
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
	public void testIncluirDataPassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouHora() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), Room1,
				"Grupo de Estudos", "60", Student1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), Room1,
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
	public void testAlterarHoraDifStudentOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
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
	public void testAlterarDataDifRoomOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		this.executeQuery("INSERT INTO professor (nome, cpf, matricula) " +
				"VALUES (\"ProfessorDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reserva_Room_professor (id_professor,id_Room,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"257.312.954-33\")," +
						"(SELECT id_Room FROM Room WHERE codigo = \"123\")," +
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
		
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reserva_Room_professor WHERE data = \"20/12/2034\";");
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
	public void testAlterarDataPassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
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
	public void testAlterarDataPassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
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
	public void testAlterarDataPassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), Room1,
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
	public void testAlterarHoraPassouHora() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), Room1,
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
	public void testAlterarHoraPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", Room1,
				"Grupo de Estudos", "30", Student1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), Room1,
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
	public void testBuscarPorHora() throws SQLException, PatrimonyException, ClientException, ReserveException {
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
	
	
	private String select_id_Student(Student a){
		return "SELECT id_Student FROM Student WHERE " +
				"Student.nome = \"" + a.getName() + "\" and " +
				"Student.cpf = \"" + a.getCpf() + "\" and " +
				"Student.telefone = \"" + a.getFone() + "\" and " +
				"Student.email = \"" + a.getEmail() + "\" and " +
				"Student.matricula = \"" + a.getRegistration() + "\"";
	}
	private String select_id_Room(Room room){
		return "SELECT id_Room FROM Room WHERE " +
				"Room.codigo = \"" + room.getCode() + "\" and " +
				"Room.descricao = \"" + room.getDescription() +  "\" and " +
				"Room.capacidade = " + room.getCapacity();
	}
	private String where_reserva_Room_Student(StudentRoomReserve r){
		return " WHERE " +
		"id_Student = ( " + select_id_Student(r.getStudent()) + " ) and " +
		"id_Room = ( " + select_id_Room(r.getRoom()) + " ) and " +
		"finalidade = \"" + r.getFinality() + "\" and " +
		"hora = \"" + r.getHour() + "\" and " +
		"data = \"" + r.getDate() + "\" and " +
		"cadeiras_reservadas = " + r.getReservedChairs();
	}
	private String values_reserva_Room_Student(StudentRoomReserve r){
		return "( " + select_id_Student(r.getStudent()) + " ), " +
		"( " + select_id_Room(r.getRoom()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHour() + "\", " +
		"\"" + r.getDate() + "\", " +
		r.getReservedChairs();
	}
	private void insert_into(StudentRoomReserve r){
		try {
			this.executeQuery("INSERT INTO " +
					"reserva_Room_Student (id_Student, id_Room, finalidade, hora, data, cadeiras_reservadas) " +
					"VALUES ( " + values_reserva_Room_Student(r) + " );");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void delete_from(StudentRoomReserve r){
		try {
			this.executeQuery("DELETE FROM reserva_Room_Student " + 
								this.where_reserva_Room_Student(r) + " ;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private boolean inDB(StudentRoomReserve r) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reserva_Room_Student " + 
								this.where_reserva_Room_Student(r) + " ;");
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
