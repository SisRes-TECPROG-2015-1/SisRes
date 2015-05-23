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


	
	private static Room sala_a;
	private static Room sala_b;
	private static Teacher professor1;
	private static Teacher professor2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sala_a = new Room("S2", "Room de aula", "130");
		sala_b = new Room("I6", "Laboratorio", "40");
		professor1 = new Teacher("ProfessorUm", "490.491.781-20", "58801", "3333-3333", "prof@email");
		professor2 = new Teacher("ProfessorDois", "040.757.021-70", "36106", "3628-3079", "prof@email");
		
		ClassRoomDAO.getInstance().includeARoom(sala_a);
		ClassRoomDAO.getInstance().includeARoom(sala_b);
		TeacherDAO.getInstance().includeNewTeacher(professor1);
		TeacherDAO.getInstance().includeNewTeacher(professor2);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ClassRoomDAO.getInstance().excludeRoom(sala_a);
		ClassRoomDAO.getInstance().excludeRoom(sala_b);
		TeacherDAO.getInstance().excludeATeacher(professor1);
		TeacherDAO.getInstance().excludeATeacher(professor2);	
	}

	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia", TeacherRoomReserveDAO.getInstance() instanceof TeacherRoomReserveDAO);
	}
	
	@Test
	public void testIncluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de reforco", professor1);
		
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.executeQuery("DELETE FROM reserva_sala_professor WHERE data = \"20/12/34\";");
		
		assertTrue("Teste de Inclusao.", resultado);
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(null);
	}
	@Test (expected= ReserveException.class)
	public void testReservaPorProfessorInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", new Teacher("Inexistente", "501.341.852-69", "456678", "", ""));
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", new Room("222", "Laboratorio", "20"),
				"Grupo de Estudos", professor1);
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirRoomReservadaProf() throws ReserveException, ClientException, 
											PatrimonyException, SQLException 
	{
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de MDS",  professor1);
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de PDS",  professor2);
		
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		}
		
	}
	@Test
	public void testIncluirRoomReservadaAluno() throws ReserveException, ClientException, 
											PatrimonyException, SQLException 
	{
		this.executeQuery("INSERT INTO aluno (nome, cpf, matricula) " +
		"VALUES (\"Aluno\", \"257.312.954-33\", \"33108\");");
		this.executeQuery("INSERT INTO reserva_sala_aluno (id_aluno,id_sala,finalidade,hora,data, cadeiras_reservadas) "+
		"VALUES ((SELECT id_aluno FROM aluno WHERE cpf = \"257.312.954-33\")," +
				"(SELECT id_sala FROM sala WHERE codigo = \"S2\")," +
				"\"Estudo de Fisica\", \"08:00\", \"20/12/2013\", 20);");
		
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Aula de EA",  professor1);
		
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		
			
		boolean resultadoProf = this.inDB(reserva);
		boolean resultadoAluno = this.inDBGeneric("SELECT * FROM reserva_sala_aluno " +
				"INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala " +
				"INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno;");
		
				
		this.executeQuery("DELETE FROM aluno;");
		this.executeQuery("DELETE FROM reserva_sala_aluno;");
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		
		assertTrue("Room reservada por aluno", (resultadoProf && !resultadoAluno));
		
		}
	public void testIncluirDataPassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/1990", "8:00", sala_a,
				"Grupo de Estudos", professor1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/01/2013", "8:00", sala_a,
				"Grupo de Estudos", professor1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala_a,
				"Grupo de Estudos", professor1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouHora() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala_a,
				"Grupo de Estudos",  professor1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala_a,
				"Grupo de Estudos", professor1);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testIncluirProfessorOcupado() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Aulao pre-prova", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Aulao pre-prova", professor1);
		TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva);
		try{
			TeacherRoomReserveDAO.getInstance().saveNewTeacherRoomReserve(reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
		
	}
	@Test
	public void testAlterar() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva1 = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Pesquisa", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("21/12/34", "19:00", sala_a,
				"Pesquisa", professor1);
		
		this.executeQuery("INSERT INTO " +
				"reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) " +
				"VALUES ( " + values_reserva_sala_professor(reserva1) + " );");
		
		
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva1, reserva2);
		
		boolean resultado = this.inDB(reserva2);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		assertTrue("Teste de Alteracao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_AntigoNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(null, reserva);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_NovoNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
			"Grupo de pesquisa", professor1);
		TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, null);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
	}
	public void testAlterarDataPassouAno() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/1990", "8:00", sala_a,
				"Grupo de Estudos", professor2);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataPassouMes() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/01/2013", "8:00", sala_a,
				"Grupo de Estudos", professor2);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataPassouDia() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarHoraPassouHora() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarHoraPassouMinutos() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testAlterarJaInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("27/12/34", "9:00", sala_b,
				"Grupo d", professor2);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva2, reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
	}
	
	@Test (expected= ReserveException.class)
	public void testAlterarHoraReservaFeita() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "9:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		TeacherRoomReserve reserva3 = new TeacherRoomReserve("20/12/34", "8:00", sala_b,
				"Grupo de Estudos", professor1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva2, reserva3);
		} finally {
		
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataDifRoomOcupada() throws ReserveException, ClientException, PatrimonyException, SQLException {
		this.executeQuery("INSERT INTO professor (nome, cpf, matricula) " +
				"VALUES (\"Professor\", \"257.312.954-33\", \"11009988\");");
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"257.312.954-33\")," +
						"(SELECT id_sala FROM sala WHERE codigo = \"S2\")," +
						"\"Aula de Calculo\", \"8:00\", \"20/12/34\");");
		
				
		
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", sala_a,
				"Grupo de Pesquisa", professor1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos", professor1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\"");
		this.executeQuery("DELETE FROM reserva_sala_professor");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarProfessorInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", new Teacher("Nao Existe", "501.341.852-69", "456678", "", ""));
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarRoomInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", new Room("S5", "Room de aula", "120"),
				"Grupo de Estudos", professor1);
		
		try{
			TeacherRoomReserveDAO.getInstance().updateTeacherRoomReserve(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test
	public void testExcluir() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Pesquisa", professor1);
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva.getTeacher().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCode() + "\")," +
						"\"Grupo de Pesquisa\", \"08:00\", \"20/12/2034\");");
		
		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		assertFalse("Teste de Exclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirNulo() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(null);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirInexistente() throws ReserveException, ClientException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", professor1);

		TeacherRoomReserveDAO.getInstance().deleteTeacherReservedRoom(reserva);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
	}
	
		
	@Test
	public void testBuscarPorData() throws SQLException, PatrimonyException, ClientException, ReserveException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "19:00", sala_a,
				"Reuniao", professor1);
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva.getTeacher().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCode() + "\")," +
						"\"" + reserva.getFinality() + "\", \"" +
						reserva.getHour() + "\", \"" + reserva.getDate() +"\");");
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva2.getTeacher().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCode() + "\")," +
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
		
		this.executeQuery("DELETE FROM reserva_sala_professor WHERE data = \"20/12/2034\"");
		
		assertTrue("Teste de busca por data", resultado && resultado2);
	}
		
	private String select_id_professor(Teacher p){
		return "SELECT id_professor FROM professor WHERE " +
				"professor.nome = \"" + p.getName() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getFone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getRegistration() + "\"";
	}
	private String select_id_sala(Room sala){
		return "SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + sala.getCode() + "\" and " +
				"sala.descricao = \"" + sala.getDescription() +  "\" and " +
				"sala.capacidade = " + sala.getCapacity();
	}
	private String where_reserva_sala_professor(TeacherRoomReserve r){
		return " WHERE " +
		"id_professor = ( " + select_id_professor(r.getTeacher()) + " ) and " +
		"id_sala = ( " + select_id_sala(r.getRoom()) + " ) and " +
		"finalidade = \"" + r.getFinality() + "\" and " +
		"hora = \"" + r.getHour() + "\" and " +
		"data = \"" + r.getDate() + "\"";
	}
	private String values_reserva_sala_professor(TeacherRoomReserve r){
		return "( " + select_id_professor(r.getTeacher()) + " ), " +
		"( " + select_id_sala(r.getRoom()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHour() + "\", " +
		"\"" + r.getDate() + "\"";
	}
	/*private String atibutes_value_reserva_sala_professor(ReservaRoomProfessor r){
		return "id_professor = ( " + select_id_professor(r.getProfessor()) + " ), " +
		"id_sala = ( " + select_id_sala(r.getRoom()) + " ), " +
		"finalidade = \"" + r.getFinalidade() + "\", " +
		"hora = \"" + r.getHour() + "\", " +
		"data = \"" + r.getData() + "\"";
	}*/

	private String insert_into(TeacherRoomReserve r){
		return "INSERT INTO " +
				"reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) " +
				"VALUES ( " + values_reserva_sala_professor(r) + " );";
	}
	
	private String delete_from_professor(TeacherRoomReserve r){
		return "DELETE FROM reserva_sala_professor " + this.where_reserva_sala_professor(r) + " ;";
	}
	/*
	private String delete_from_aluno(ReservaRoomProfessor r){
		return "DELETE FROM reserva_sala_aluno WHERE " +
				"hora = \"" + r.getHour() + "\" and " +
				"data = \"" + r.getData() +  "\" ;";
	}
	
	private String update(ReservaRoomProfessor r, ReservaRoomProfessor r2){
		return "UPDATE reserva_sala_professor SET " + 
				this.atibutes_value_reserva_sala_professor(r2) +
				this.where_reserva_sala_professor(r) + " ;";
	}
*/
	
	
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
			
	private boolean inDB(TeacherRoomReserve reserva) throws SQLException{
		return this.inDBGeneric("SELECT * FROM reserva_sala_professor " + 
								this.where_reserva_sala_professor(reserva) + " ;");
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
