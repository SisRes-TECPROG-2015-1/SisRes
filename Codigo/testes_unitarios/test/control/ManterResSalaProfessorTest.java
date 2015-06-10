package test.control;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import control.MaintainClassroomReservationByTeacher;
import exception.ClientException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ManterResSalaProfessorTest {
	private static Room room1;
	private static Teacher teacher1;
	private static Vector<TeacherRoomReserve> vet;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vet = MaintainClassroomReservationByTeacher.getInstance().getRoomsReserves();
		room1 = new Room("123", "Sala de Aula", "120");
		teacher1 = new Teacher("testInstance", "040.757.021-70", "0058801", "3333-3333", "name@email");
		
		TeacherDAO.getInstance().includeNewTeacher(teacher1);
		ClassRoomDAO.getInstance().includeARoom(room1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TeacherDAO.getInstance().excludeATeacher(teacher1);
		ClassRoomDAO.getInstance().excludeRoom(room1);
	}

	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia.", MaintainClassroomReservationByTeacher.getInstance() instanceof MaintainClassroomReservationByTeacher);
	}
	@Test
	public void testSingleton() {
		assertSame("Teste de Instancia.", MaintainClassroomReservationByTeacher.getInstance(), MaintainClassroomReservationByTeacher.getInstance());
	}
	
	
	@Test
	public void testInserir() throws SQLException, ReserveException, ClientException, PatrimonyException {
		String finality = "Sala de Estudos";
		String date = "20/12/33";
		String hour = "9:11";
		TeacherRoomReserve reservation = new TeacherRoomReserve(date, hour, room1, finality, teacher1);
		MaintainClassroomReservationByTeacher.getInstance().insertReserve(room1, teacher1, date, hour, finality);
		boolean resultado = this.inDB(reservation);
		boolean resultado2 = reservation.equals(vet.lastElement());
		if(resultado)
			this.delete_from(reservation);
		assertTrue("Teste de Insercao.", resultado && resultado2);
	}
	@Test
	public void testAlterar() throws ReserveException, SQLException, ClientException, PatrimonyException {
		
		TeacherRoomReserve reservation = new TeacherRoomReserve("20/12/33", "9:11", room1, "Pesquisa", teacher1);
		this.insert_into(reservation);
		vet.add(reservation);
		TeacherRoomReserve reservation2 = new TeacherRoomReserve("20/12/33", "9:11", room1, "Reuniao", teacher1);
		MaintainClassroomReservationByTeacher.getInstance().changeRoomReserve("Reuniao", vet.lastElement());
		boolean resultado = this.inDB(reservation2);
		boolean resultado2 = reservation2.equals(vet.lastElement());
		if(resultado)
			this.delete_from(reservation2);
		if(this.inDB(reservation))
			this.delete_from(reservation);
		assertTrue("Teste de Alteracao.", resultado && resultado2);
	}
	@Test
	public void testExcluir() throws ReserveException, SQLException {
		String finality = "Pesquisa";
		String date = "20/12/33";
		String hour = "9:11";
		TeacherRoomReserve reservation = new TeacherRoomReserve(date, hour, room1, finality, teacher1);
		this.insert_into(reservation);
		vet.add(reservation);
		MaintainClassroomReservationByTeacher.getInstance().excludeReserve(reservation);
		boolean resultado = this.inDB(reservation);
		vet.remove(reservation);

		if(resultado)
			this.delete_from(reservation);
		assertTrue("Teste de Exclusao.", !resultado );
	}

	private String select_id_teacher(Teacher prof){
		return "SELECT id_teacher FROM teacher WHERE " +
				"teacher.name = \"" + prof.getName() + "\" and " +
				"teacher.cpf = \"" + prof.getCpf() + "\" and " +
				"teacher.fone = \"" + prof.getFone() + "\" and " +
				"teacher.email = \"" + prof.getEmail() + "\" and " +
				"teacher.registration = \"" + prof.getRegistration() + "\"";
	}
	private String select_id_room(Room room){
		return "SELECT id_room FROM room WHERE " +
				"room.code = \"" + room.getCode() + "\" and " +
				"room.description = \"" + room.getDescription() +  "\" and " +
				"room.capacity = " + room.getCapacity();
	}
	private String where_reservation_room_teacher(TeacherRoomReserve reservation){
		return " WHERE " +
		"id_teacher = ( " + select_id_teacher(reservation.getTeacher()) + " ) and " +
		"id_room = ( " + select_id_room(reservation.getRoom()) + " ) and " +
		"finality = \"" + reservation.getFinality() + "\" and " +
		"hour = \"" + reservation.getHour() + "\" and " +
		"date = \"" + reservation.getDate() + "\" ";
	}
	private String values_reservation_room_teacher(TeacherRoomReserve reservation){
		return "( " + select_id_teacher(reservation.getTeacher()) + " ), " +
		"( " + select_id_room(reservation.getRoom()) + " ), " +
		"\"" + reservation.getFinality() + "\", " +
		"\"" + reservation.getHour() + "\", " +
		"\"" + reservation.getDate() + "\"";
	}
	private void insert_into(TeacherRoomReserve reservation){
		try {
			this.executeQuery("INSERT INTO " +
					"reservation_room_teacher (id_teacher, id_room, finality, hour, date) " +
					"VALUES ( " + values_reservation_room_teacher(reservation) + " );");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void delete_from(TeacherRoomReserve reservation){
		try {
			this.executeQuery("DELETE FROM reservation_room_teacher " + 
								this.where_reservation_room_teacher(reservation) + " ;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private boolean inDB(TeacherRoomReserve reservation) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reservation_room_teacher " + 
								this.where_reservation_room_teacher(reservation) + " ;");
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
