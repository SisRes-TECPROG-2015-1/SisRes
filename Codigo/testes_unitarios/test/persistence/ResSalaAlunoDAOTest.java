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

import model.Aluno;
import model.StudentRoomReserve;
import model.Sala;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.StudentDAO;
import persistence.FactoryConnection;
import persistence.StudentRoomReserveDAO;
import persistence.ClassRoom;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ResSalaAlunoDAOTest {

	private static Sala sala1;
	private static Sala sala2;
	private static Aluno aluno1;
	private static Aluno aluno2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sala1 = new Sala("123", "Sala de Aula", "120");
		sala2 = new Sala("543", "Laboratorio", "30");
		aluno1 = new Aluno("testInstance", "501.341.852-69", "456678", "", "");
		aluno2 = new Aluno("Incluindo Matricula Igual", "490.491.781-20", "345543", "2222-2222", "aluno2@email");
		
		StudentDAO.getInstance().includeNewStudent(aluno1);
		StudentDAO.getInstance().includeNewStudent(aluno2);
		ClassRoom.getInstance().includeARoom(sala1);
		ClassRoom.getInstance().includeARoom(sala2);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StudentDAO.getInstance().deleteStudent(aluno1);
		StudentDAO.getInstance().deleteStudent(aluno2);
		ClassRoom.getInstance().excluir(sala1);
		ClassRoom.getInstance().excluir(sala2);
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
	public void testIncluir() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
		assertTrue("Teste de Inclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testIncluirNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(null);
	}
	@Test (expected= ReserveException.class)
	public void testIncluirAlunoInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", new Aluno("tepp", "501.341.852-69", "456678", "", ""));
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		boolean resultado = this.inDB(reserva);

		if(resultado)
			this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirSalaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", new Sala("22277883", "Laboratorio", "120"),
				"Grupo de Estudos", "120", aluno1);
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		boolean resultado = this.inDB(reserva);

		if(resultado)
			this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirSalaReservadaProf() throws ReserveException, ClienteException, 
											PatrimonyException, SQLException 
	{
		this.executeQuery("INSERT INTO professor (nome, cpf, matricula) " +
		"VALUES (\"ProfessorDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
		"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"257.312.954-33\")," +
				"(SELECT id_sala FROM sala WHERE codigo = \"123\")," +
				"\"Aula de Calculo\", \"08:00\", \"20/12/2034\");");
		
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "60", aluno1);
		
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
		if(this.inDB(reserva))
			this.delete_from(reserva);
		
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reserva_sala_professor WHERE data = \"20/12/2034\";");
		
		}
		
	}
	@Test (expected= ReserveException.class)
	public void testIncluirAlunoOcupado() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala2,
				"Grupo de Estudos", ""+sala2.getCapacidade(), aluno1);
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
	public void testIncluirSalaCheia() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "60", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "70", aluno2);
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
	public void testIncluirDataPassouAno() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/1990", "8:00", sala1,
				"Grupo de Estudos", "60", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouMes() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/01/2013", "8:00", sala1,
				"Grupo de Estudos", "60", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouDia() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala1,
				"Grupo de Estudos", "60", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouHora() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala1,
				"Grupo de Estudos", "60", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouMinutos() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala1,
				"Grupo de Estudos", "60", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().saveNewStudentRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from(reserva);
		}
	}
	
	
	@Test
	public void testAlterar() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		
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
	public void testAlterarNulo1() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(null, reserva);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarNulo2() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
			"Grupo de Estudos", "120", aluno1);
		StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, null);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
			if(this.inDB(reserva2))
				this.delete_from(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarJaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("27/12/34", "9:00", sala2,
				"Grupo d", ""+sala2.getCapacidade(), aluno2);
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
	public void testAlterarHoraDifAlunoOcupado() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "9:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		StudentRoomReserve reserva3 = new StudentRoomReserve("20/12/34", "8:00", sala2,
				"Grupo de Estudos", ""+sala2.getCapacidade(), aluno1);
		
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
	public void testAlterarDataDifSalaOcupado() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		this.executeQuery("INSERT INTO professor (nome, cpf, matricula) " +
				"VALUES (\"ProfessorDAO\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"257.312.954-33\")," +
						"(SELECT id_sala FROM sala WHERE codigo = \"123\")," +
						"\"Aula de Calculo\", \"08:00\", \"20/12/2034\");");
				
		
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		
		try{
			StudentRoomReserveDAO.getInstance().updateStudentRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from(reserva);
		if(this.inDB(reserva2))
			this.delete_from(reserva2);
		
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\";");
		this.executeQuery("DELETE FROM reserva_sala_professor WHERE data = \"20/12/2034\";");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarAlunoInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", new Aluno("tepp", "501.341.852-69", "456678", "", ""));
		
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
	public void testAlterarSalaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		this.insert_into(reserva);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", new Sala("22277883", "Laboratorio", "120"),
				"Grupo de Estudos", "120", aluno1);
		
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
	public void testAlterarNaoHaCadeira() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "20", aluno2);
		StudentRoomReserve reserva3 = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno2);
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
	public void testAlterarDataPassouAno() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/12/1990", "8:00", sala1,
				"Grupo de Estudos", "20", aluno2);
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
	public void testAlterarDataPassouMes() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve("20/01/2013", "8:00", sala1,
				"Grupo de Estudos", "20", aluno2);
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
	public void testAlterarDataPassouDia() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala1,
				"Grupo de Estudos", "60", aluno1);
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
	public void testAlterarHoraPassouHora() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala1,
				"Grupo de Estudos", "60", aluno1);
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
	public void testAlterarHoraPassouMinutos() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "30", aluno1);
		StudentRoomReserve reserva2 = new StudentRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala1,
				"Grupo de Estudos", "60", aluno1);
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
	public void testExcluir() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);
		this.insert_into(reserva);
		
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
		assertFalse("Teste de Exclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(null);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "8:00", sala1,
				"Grupo de Estudos", "120", aluno1);

		StudentRoomReserveDAO.getInstance().deleteStudentReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.delete_from(reserva);
	}
	
	
	@Test
	public void testBuscarPorDia() throws SQLException, PatrimonyException, ClienteException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "8:00", sala1,
				"Grupo de Estudos", "40", aluno1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", sala1,
				"Grupo de Estudos", "50", aluno1);
		
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
	public void testBuscarPorHora() throws SQLException, PatrimonyException, ClienteException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("20/12/34", "9:00", sala1,
				"Grupo de Estudos", "40", aluno1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "09:00", sala1,
				"Grupo de Estudos", "50", aluno1);
		
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
	public void testCadeirasDisponiveis() throws SQLException, PatrimonyException, ClienteException, ReserveException {
		StudentRoomReserve reserva = new StudentRoomReserve("21/12/34", "19:00", sala1,
				"Grupo de Estudos", "40", aluno1);
		
		StudentRoomReserve reserva2 = new StudentRoomReserve("21/12/34", "19:00", sala1,
				"Grupo de Estudos", "50", aluno1);
		
		this.insert_into(reserva);
		this.insert_into(reserva2);
		int c = StudentRoomReserveDAO.getInstance().getAvailableChairs(sala1, "21/12/34", "19:00");
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
	
	
	private String select_id_aluno(Aluno a){
		return "SELECT id_aluno FROM aluno WHERE " +
				"aluno.nome = \"" + a.getNome() + "\" and " +
				"aluno.cpf = \"" + a.getCpf() + "\" and " +
				"aluno.telefone = \"" + a.getTelefone() + "\" and " +
				"aluno.email = \"" + a.getEmail() + "\" and " +
				"aluno.matricula = \"" + a.getMatricula() + "\"";
	}
	private String select_id_sala(Sala sala){
		return "SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + sala.getCodigo() + "\" and " +
				"sala.descricao = \"" + sala.getDescricao() +  "\" and " +
				"sala.capacidade = " + sala.getCapacidade();
	}
	private String where_reserva_sala_aluno(StudentRoomReserve r){
		return " WHERE " +
		"id_aluno = ( " + select_id_aluno(r.getAluno()) + " ) and " +
		"id_sala = ( " + select_id_sala(r.getSala()) + " ) and " +
		"finalidade = \"" + r.getFinality() + "\" and " +
		"hora = \"" + r.getHora() + "\" and " +
		"data = \"" + r.getData() + "\" and " +
		"cadeiras_reservadas = " + r.getReservedChairs();
	}
	private String values_reserva_sala_aluno(StudentRoomReserve r){
		return "( " + select_id_aluno(r.getAluno()) + " ), " +
		"( " + select_id_sala(r.getSala()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHora() + "\", " +
		"\"" + r.getData() + "\", " +
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
