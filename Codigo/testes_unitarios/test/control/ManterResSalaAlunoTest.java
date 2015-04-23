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
	private static Room sala1;
	private static Student aluno1;
	private static Vector<StudentRoomReserve> vet;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vet = MaintainClassroomReservationByStudent.getInstance().getRoomReserves();
		sala1 = new Room("123", "Sala de Aula", "120");
		aluno1 = new Student("testInstance", "501.341.852-69", "456678", "", "");
		
		StudentDAO.getInstance().includeNewStudent(aluno1);
		ClassRoomDAO.getInstance().includeARoom(sala1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StudentDAO.getInstance().deleteStudent(aluno1);
		ClassRoomDAO.getInstance().excludeRoom(sala1);
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
		String cadeiras_reservadas = "120";
		String finalidade = "Sala de Estudos";
		String data = "20/12/33";
		String hora = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(data, hora, sala1, finalidade, cadeiras_reservadas, aluno1);
		MaintainClassroomReservationByStudent.getInstance().insertReserve(sala1, aluno1, data, hora, finalidade, cadeiras_reservadas);
		boolean resultado = this.inDB(r);
		boolean resultado2 = r.equals(vet.lastElement());
		if(resultado)
			this.delete_from(r);
		assertTrue("Teste de Insercao.", resultado && resultado2);
	}
	@Test
	public void testAlterar() throws ReserveException, SQLException, ClientException, PatrimonyException {
		String cadeiras_reservadas = "120";
		String finalidade = "Sala de Estudos";
		String data = "20/12/33";
		String hora = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(data, hora, sala1, finalidade, cadeiras_reservadas, aluno1);
		this.insert_into(r);
		vet.add(r);
		StudentRoomReserve r2 = new StudentRoomReserve(data, hora, sala1, finalidade, "100", aluno1);
		MaintainClassroomReservationByStudent.getInstance().changeReserve(finalidade, "100", vet.lastElement());
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
		String cadeiras_reservadas = "120";
		String finalidade = "Sala de Estudos";
		String data = "20/12/33";
		String hora = "9:11";
		StudentRoomReserve r = new StudentRoomReserve(data, hora, sala1, finalidade, cadeiras_reservadas, aluno1);
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
		Student aluno2 = new Student("testInswewee", "490.491.781-20", "4324678", "", "");
		StudentRoomReserve r = new StudentRoomReserve("1/3/20", "9:11", sala1, "Sala de Estudos", "60", aluno1);
		StudentRoomReserve r2 = new StudentRoomReserve("1/3/20", "9:11", sala1,"Sala de Estudos", "30", aluno2);
		StudentRoomReserve r3 = new StudentRoomReserve("1/3/20", "10:00", sala1,"Sala de Estudos", "120", aluno1);
		StudentDAO.getInstance().includeNewStudent(aluno2);
		this.insert_into(r);
		this.insert_into(r2);
		this.insert_into(r3);
		Vector<StudentRoomReserve> vet2 = MaintainClassroomReservationByStudent.getInstance().getRoomReservesByDate("1/3/20");
		this.delete_from(r);
		this.delete_from(r2);
		this.delete_from(r3);
		StudentDAO.getInstance().deleteStudent(aluno2);
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
		Student aluno2 = new Student("testInswewee", "490.491.781-20", "4324678", "", "");
		StudentRoomReserve r = new StudentRoomReserve("26/02/2013", "20:00", sala1, "Sala de Estudos", "60", aluno1);
		StudentRoomReserve r2 = new StudentRoomReserve("26/02/2013", "20:00", sala1,"Sala de Estudos", "30", aluno2);
		StudentRoomReserve r3 = new StudentRoomReserve("26/02/2013", "21:00", sala1,"Sala de Estudos", "120", aluno1);
		StudentDAO.getInstance().includeNewStudent(aluno2);
		this.insert_into(r);
		this.insert_into(r2);
		this.insert_into(r3);
		Vector<StudentRoomReserve> vet2 = MaintainClassroomReservationByStudent.getInstance().getRoomReservesByDate("26/02/2013");
		this.delete_from(r);
		this.delete_from(r2);
		this.delete_from(r3);
		StudentDAO.getInstance().deleteStudent(aluno2);
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
	
	
	private String select_id_aluno(Student a){
		return "SELECT id_aluno FROM aluno WHERE " +
				"aluno.nome = \"" + a.getName() + "\" and " +
				"aluno.cpf = \"" + a.getCpf() + "\" and " +
				"aluno.telefone = \"" + a.getFone() + "\" and " +
				"aluno.email = \"" + a.getEmail() + "\" and " +
				"aluno.matricula = \"" + a.getRegistration() + "\"";
	}
	private String select_id_sala(Room sala){
		return "SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + sala.getCode() + "\" and " +
				"sala.descricao = \"" + sala.getDescription() +  "\" and " +
				"sala.capacidade = " + sala.getCapacity();
	}
	private String where_reserva_sala_aluno(StudentRoomReserve r){
		return " WHERE " +
		"id_aluno = ( " + select_id_aluno(r.getStudent()) + " ) and " +
		"id_sala = ( " + select_id_sala(r.getRoom()) + " ) and " +
		"finalidade = \"" + r.getFinality() + "\" and " +
		"hora = \"" + r.getHour() + "\" and " +
		"data = \"" + r.getDate() + "\" and " +
		"cadeiras_reservadas = " + r.getReservedChairs();
	}
	private String values_reserva_sala_aluno(StudentRoomReserve r){
		return "( " + select_id_aluno(r.getStudent()) + " ), " +
		"( " + select_id_sala(r.getRoom()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHour() + "\", " +
		"\"" + r.getDate() + "\", " +
		r.getReservedChairs();
	}
	private void insert_into(StudentRoomReserve r){
		try {
			this.executeQuery("INSERT INTO " +
					"reserva_sala_aluno (id_aluno, id_sala, finalidade, hora, data, cadeiras_reservadas) " +
					"VALUES ( " + values_reserva_sala_aluno(r) + " );");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void delete_from(StudentRoomReserve r){
		try {
			this.executeQuery("DELETE FROM reserva_sala_aluno " + 
								this.where_reserva_sala_aluno(r) + " ;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private boolean inDB(StudentRoomReserve r) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reserva_sala_aluno " + 
								this.where_reserva_sala_aluno(r) + " ;");
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
