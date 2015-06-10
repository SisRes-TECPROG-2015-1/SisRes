package test.control;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import model.Room;
import model.Student;
import model.StudentRoomReserve;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.ClassRoomDAO;
import persistence.FactoryConnection;
import persistence.StudentDAO;
import control.MaintainClassroomReservationByStudent;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ManterResSalaAlunoTest {
	private static Room room1;
	private static Student student1;
	private static Vector<StudentRoomReserve> vet;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vet = MaintainClassroomReservationByStudent.getInstance().getRoomReserves();
		room1 = new Room("123", "Sala de Aula", "120");
		student1 = new Student("testInstance", "501.341.852-69", "456678", "", "");
		
		StudentDAO.getInstance().includeNewStudent(student1);
		ClassRoomDAO.getInstance().includeARoom(room1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StudentDAO.getInstance().deleteStudent(student1);
		ClassRoomDAO.getInstance().excludeRoom(room1);
	}

	
	
	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia.", MaintainClassroomReservationByStudent.getInstance() instanceof MaintainClassroomReservationByStudent);
	}
	@Test
	public void testSingleton() {
		assertSame("Teste de Instancia.", MaintainClassroomReservationByStudent.getInstance(), MaintainClassroomReservationByStudent.getInstance());
	}
	
	
	@Test
	public void testInserir() throws SQLException, ReserveException, ClientException, PatrimonyException {
		String reserved_chairs = "120";
		String finality = "Sala de Estudos";
		String date = "20/12/33";
		String hour = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(date, hour, room1, finality, reserved_chairs, student1);
		MaintainClassroomReservationByStudent.getInstance().insertReserve(room1, student1, date, hour, finality, reserved_chairs);
		boolean resultado = this.inDB(r);
		boolean resultado2 = r.equals(vet.lastElement());
		if(resultado)
			this.delete_from(r);
		assertTrue("Teste de Insercao.", resultado && resultado2);
	}
	@Test
	public void testAlterar() throws ReserveException, SQLException, ClientException, PatrimonyException {
		String reserved_chairs = "120";
		String finality = "Sala de Estudos";
		String date = "20/12/33";
		String hour = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(date, hour, room1, finality, reserved_chairs, student1);
		this.insert_into(r);
		vet.add(r);
		StudentRoomReserve r2 = new StudentRoomReserve(date, hour, room1, finality, "100", student1);
		MaintainClassroomReservationByStudent.getInstance().changeReserve(finality, "100", vet.lastElement());
		boolean resultado = this.inDB(r2);
		boolean resultado2 = r2.equals(vet.lastElement());
		if(resultado)
			this.delete_from(r2);
		if(this.inDB(r))
			this.delete_from(r);
		assertTrue("Teste de Alteracao.", resultado && resultado2);
	}
	@Test
	public void testExcluir() throws ReserveException, SQLException {
		String reserved_chairs = "120";
		String finality = "Sala de Estudos";
		String date = "20/12/33";
		String hour = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(date, hour, room1, finality, reserved_chairs, student1);
		this.insert_into(r);
		vet.add(r);
		MaintainClassroomReservationByStudent.getInstance().excludeRoom(r);
		boolean resultado = this.inDB(r);
		boolean resultado2 = true;
		if(vet.size() > 0)
			resultado2 = !r.equals(vet.lastElement());
		if(resultado)
			this.delete_from(r);
		assertTrue("Teste de Alteracao.", !resultado && resultado2);
	}
	
	@Test
	public void testVetDia() throws SQLException, ReserveException, ClientException, PatrimonyException {
		Student student2 = new Student("testInswewee", "490.491.781-20", "4324678", "", "");
		StudentRoomReserve r = new StudentRoomReserve("1/3/20", "9:11", room1, "Sala de Estudos", "60", student1);
		StudentRoomReserve r2 = new StudentRoomReserve("1/3/20", "9:11", room1,"Sala de Estudos", "30", student2);
		StudentRoomReserve r3 = new StudentRoomReserve("1/3/20", "10:00", room1,"Sala de Estudos", "120", student1);
		StudentDAO.getInstance().includeNewStudent(student2);
		this.insert_into(r);
		this.insert_into(r2);
		this.insert_into(r3);
		Vector<StudentRoomReserve> vet2 = MaintainClassroomReservationByStudent.getInstance().getRoomReservesByDate("1/3/20");
		this.delete_from(r);
		this.delete_from(r2);
		this.delete_from(r3);
		StudentDAO.getInstance().deleteStudent(student2);
		boolean resultado = false;
		boolean resultado2 = false;
		boolean resultado3 = false;
		
		Iterator<StudentRoomReserve> it = vet2.iterator();
		while(it.hasNext()){
			StudentRoomReserve obj = it.next();
			if(obj.equals(r))
				resultado = true;
			else if(obj.equals(r2))
				resultado2 = true;
			else if(obj.equals(r3))
				resultado3 = true;
		}
		
		assertTrue("Teste de busca", resultado && resultado2 && resultado3);
	}
	
	@Test
	public void testVetDiaHoje() throws SQLException, ReserveException, ClientException, PatrimonyException {
		Student student2 = new Student("testInswewee", "490.491.781-20", "4324678", "", "");
		StudentRoomReserve r = new StudentRoomReserve("26/02/2013", "20:00", room1, "Sala de Estudos", "60", student1);
		StudentRoomReserve r2 = new StudentRoomReserve("26/02/2013", "20:00", room1,"Sala de Estudos", "30", student2);
		StudentRoomReserve r3 = new StudentRoomReserve("26/02/2013", "21:00", room1,"Sala de Estudos", "120", student1);
		StudentDAO.getInstance().includeNewStudent(student2);
		this.insert_into(r);
		this.insert_into(r2);
		this.insert_into(r3);
		Vector<StudentRoomReserve> vet2 = MaintainClassroomReservationByStudent.getInstance().getRoomReservesByDate("26/02/2013");
		this.delete_from(r);
		this.delete_from(r2);
		this.delete_from(r3);
		StudentDAO.getInstance().deleteStudent(student2);
		boolean resultado = false;
		boolean resultado2 = false;
		boolean resultado3 = false;
		
		Iterator<StudentRoomReserve> it = vet2.iterator();
		while(it.hasNext()){
			StudentRoomReserve obj = it.next();
			if(obj.equals(r))
				resultado = true;
			else if(obj.equals(r2))
				resultado2 = true;
			else if(obj.equals(r3))
				resultado3 = true;
		}
		
		assertTrue("Teste de busca", resultado && resultado2 && resultado3);
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
