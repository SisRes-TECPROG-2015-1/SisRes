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

import model.Professor;
import model.TeacherRoomReserve;
import model.Sala;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import persistence.FactoryConnection;
import persistence.TeacherDAO;
import persistence.ResSalaProfessorDAO;
import persistence.ClassRoom;
import exception.ClienteException;
import exception.PatrimonyException;
import exception.ReserveException;

public class ResSalaProfessorDAOTest {
	
	private static Sala sala_a;
	private static Sala sala_b;
	private static Professor professor1;
	private static Professor professor2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sala_a = new Sala("S2", "Sala de aula", "130");
		sala_b = new Sala("I6", "Laboratorio", "40");
		professor1 = new Professor("ProfessorUm", "490.491.781-20", "58801", "3333-3333", "prof@email");
		professor2 = new Professor("ProfessorDois", "040.757.021-70", "36106", "3628-3079", "prof@email");
		
		ClassRoom.getInstance().includeARoom(sala_a);
		ClassRoom.getInstance().includeARoom(sala_b);
		TeacherDAO.getInstance().includeNewTeacher(professor1);
		TeacherDAO.getInstance().includeNewTeacher(professor2);		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ClassRoom.getInstance().excludeRoom(sala_a);
		ClassRoom.getInstance().excludeRoom(sala_b);
		TeacherDAO.getInstance().excludeATeacher(professor1);
		TeacherDAO.getInstance().excludeATeacher(professor2);	
	}

	@Test
	public void testInstance() {
		assertTrue("Teste de Instancia", ResSalaProfessorDAO.getInstance() instanceof ResSalaProfessorDAO);
	}
	
	@Test
	public void testIncluir() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de reforco", professor1);
		
		ResSalaProfessorDAO.getInstance().incluir(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		if(resultado)
			this.executeQuery("DELETE FROM reserva_sala_professor WHERE data = \"20/12/34\";");
		
		assertTrue("Teste de Inclusao.", resultado);
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		ResSalaProfessorDAO.getInstance().incluir(null);
	}
	@Test (expected= ReserveException.class)
	public void testReservaPorProfessorInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", new Professor("Inexistente", "501.341.852-69", "456678", "", ""));
		
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirSalaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", new Sala("222", "Laboratorio", "20"),
				"Grupo de Estudos", professor1);
		
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	
	@Test (expected= ReserveException.class)
	public void testIncluirSalaReservadaProf() throws ReserveException, ClienteException, 
											PatrimonyException, SQLException 
	{
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de MDS",  professor1);
		ResSalaProfessorDAO.getInstance().incluir(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Aula de PDS",  professor2);
		
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		}
		
	}
	@Test
	public void testIncluirSalaReservadaAluno() throws ReserveException, ClienteException, 
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
		
		ResSalaProfessorDAO.getInstance().incluir(reserva);
		
			
		boolean resultadoProf = this.inDB(reserva);
		boolean resultadoAluno = this.inDBGeneric("SELECT * FROM reserva_sala_aluno " +
				"INNER JOIN sala ON sala.id_sala = reserva_sala_aluno.id_sala " +
				"INNER JOIN aluno ON aluno.id_aluno = reserva_sala_aluno.id_aluno;");
		
				
		this.executeQuery("DELETE FROM aluno;");
		this.executeQuery("DELETE FROM reserva_sala_aluno;");
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		
		assertTrue("Sala reservada por aluno", (resultadoProf && !resultadoAluno));
		
		}
	public void testIncluirDataPassouAno() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/1990", "8:00", sala_a,
				"Grupo de Estudos", professor1);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouMes() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/01/2013", "8:00", sala_a,
				"Grupo de Estudos", professor1);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirDataPassouDia() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala_a,
				"Grupo de Estudos", professor1);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouHora() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala_a,
				"Grupo de Estudos",  professor1);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	@Test (expected= ReserveException.class)
	public void testIncluirHoraPassouMinutos() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala_a,
				"Grupo de Estudos", professor1);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva);
		} finally {
			if(this.inDB(reserva))
				this.delete_from_professor(reserva);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testIncluirProfessorOcupado() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Aulao pre-prova", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Aulao pre-prova", professor1);
		ResSalaProfessorDAO.getInstance().incluir(reserva);
		try{
			ResSalaProfessorDAO.getInstance().incluir(reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
		
	}
	@Test
	public void testAlterar() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva1 = new TeacherRoomReserve("20/12/13", "8:00", sala_a,
				"Pesquisa", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("21/12/34", "19:00", sala_a,
				"Pesquisa", professor1);
		
		this.executeQuery("INSERT INTO " +
				"reserva_sala_professor (id_professor, id_sala, finalidade, hora, data) " +
				"VALUES ( " + values_reserva_sala_professor(reserva1) + " );");
		
		
		ResSalaProfessorDAO.getInstance().alterar(reserva1, reserva2);
		
		boolean resultado = this.inDB(reserva2);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		assertTrue("Teste de Alteracao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_AntigoNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		ResSalaProfessorDAO.getInstance().alterar(null, reserva);
	}
	@Test (expected= ReserveException.class)
	public void testAlterar_NovoNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
			"Grupo de pesquisa", professor1);
		ResSalaProfessorDAO.getInstance().alterar(reserva, null);
	}
	@Test (expected= ReserveException.class)
	public void testAlterarInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
	}
	public void testAlterarDataPassouAno() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/1990", "8:00", sala_a,
				"Grupo de Estudos", professor2);
		this.insert_into(reserva);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataPassouMes() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/01/2013", "8:00", sala_a,
				"Grupo de Estudos", professor2);
		this.insert_into(reserva);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataPassouDia() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtualAMais(-100000000), this.horaAtual(), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarHoraPassouHora() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(),
				 this.horaAtualAMais(-10000000), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarHoraPassouMinutos() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Estudos",  professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve(this.dataAtual(),
				this.horaAtualAMais(-100000), sala_a,
				"Grupo de Estudos",  professor1);
		this.insert_into(reserva);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
		
		if(this.inDB(reserva))
			this.delete_from_professor(reserva);
		if(this.inDB(reserva2))
			this.delete_from_professor(reserva2);
		}
	}
	
	
	@Test (expected= ReserveException.class)
	public void testAlterarJaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("27/12/34", "9:00", sala_b,
				"Grupo d", professor2);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva2, reserva);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
		
	}
	
	@Test (expected= ReserveException.class)
	public void testAlterarHoraReservaFeita() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "9:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		this.insert_into(reserva2);
		
		TeacherRoomReserve reserva3 = new TeacherRoomReserve("20/12/34", "8:00", sala_b,
				"Grupo de Estudos", professor1);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva2, reserva3);
		} finally {
		
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarDataDifSalaOcupada() throws ReserveException, ClienteException, PatrimonyException, SQLException {
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
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
				
		this.executeQuery("DELETE FROM professor WHERE cpf = \"257.312.954-33\"");
		this.executeQuery("DELETE FROM reserva_sala_professor");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarProfessorInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de pesquisa", new Professor("Nao Existe", "501.341.852-69", "456678", "", ""));
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test (expected= ReserveException.class)
	public void testAlterarSalaInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("21/12/34", "8:00", sala_a,
				"Grupo de pesquisa", professor1);
		this.insert_into(reserva);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "8:00", new Sala("S5", "Sala de aula", "120"),
				"Grupo de Estudos", professor1);
		
		try{
			ResSalaProfessorDAO.getInstance().alterar(reserva, reserva2);
		} finally {
			this.executeQuery("DELETE FROM reserva_sala_professor;");
		}
	}
	@Test
	public void testExcluir() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Grupo de Pesquisa", professor1);
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva.getProfessor().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCodigo() + "\")," +
						"\"Grupo de Pesquisa\", \"08:00\", \"20/12/2034\");");
		
		ResSalaProfessorDAO.getInstance().excludeRoom(reserva);
		
		boolean resultado = this.inDB(reserva);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
		
		assertFalse("Teste de Exclusao.", resultado);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirNulo() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		ResSalaProfessorDAO.getInstance().excludeRoom(null);
	}
	@Test (expected= ReserveException.class)
	public void testExcluirInexistente() throws ReserveException, ClienteException, PatrimonyException, SQLException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", professor1);

		ResSalaProfessorDAO.getInstance().excludeRoom(reserva);
		
		this.executeQuery("DELETE FROM reserva_sala_professor;");
	}
	
		
	@Test
	public void testBuscarPorData() throws SQLException, PatrimonyException, ClienteException, ReserveException {
		TeacherRoomReserve reserva = new TeacherRoomReserve("20/12/34", "8:00", sala_a,
				"Reuniao", professor1);
		
		TeacherRoomReserve reserva2 = new TeacherRoomReserve("20/12/34", "19:00", sala_a,
				"Reuniao", professor1);
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva.getProfessor().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCodigo() + "\")," +
						"\"" + reserva.getFinality() + "\", \"" +
						reserva.getHora() + "\", \"" + reserva.getData() +"\");");
		
		this.executeQuery("INSERT INTO reserva_sala_professor (id_professor,id_sala,finalidade,hora,data) "+
				"VALUES ((SELECT id_professor FROM professor WHERE cpf = \"" + reserva2.getProfessor().getCpf() + "\")," + 
						"(SELECT id_sala FROM sala WHERE codigo = \"" + sala_a.getCodigo() + "\")," +
						"\"" + reserva2.getFinality() + "\", \"" +
						reserva2.getHora() + "\", \"" + reserva2.getData() +"\");");
		
		Vector<TeacherRoomReserve> vet = ResSalaProfessorDAO.getInstance().buscarPorData("20/12/2034");
		
		
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
		
	private String select_id_professor(Professor p){
		return "SELECT id_professor FROM professor WHERE " +
				"professor.nome = \"" + p.getNome() + "\" and " +
				"professor.cpf = \"" + p.getCpf() + "\" and " +
				"professor.telefone = \"" + p.getTelefone() + "\" and " +
				"professor.email = \"" + p.getEmail() + "\" and " +
				"professor.matricula = \"" + p.getMatricula() + "\"";
	}
	private String select_id_sala(Sala sala){
		return "SELECT id_sala FROM sala WHERE " +
				"sala.codigo = \"" + sala.getCodigo() + "\" and " +
				"sala.descricao = \"" + sala.getDescricao() +  "\" and " +
				"sala.capacidade = " + sala.getCapacidade();
	}
	private String where_reserva_sala_professor(TeacherRoomReserve r){
		return " WHERE " +
		"id_professor = ( " + select_id_professor(r.getProfessor()) + " ) and " +
		"id_sala = ( " + select_id_sala(r.getSala()) + " ) and " +
		"finalidade = \"" + r.getFinality() + "\" and " +
		"hora = \"" + r.getHora() + "\" and " +
		"data = \"" + r.getData() + "\"";
	}
	private String values_reserva_sala_professor(TeacherRoomReserve r){
		return "( " + select_id_professor(r.getProfessor()) + " ), " +
		"( " + select_id_sala(r.getSala()) + " ), " +
		"\"" + r.getFinality() + "\", " +
		"\"" + r.getHora() + "\", " +
		"\"" + r.getData() + "\"";
	}
	/*private String atibutes_value_reserva_sala_professor(ReservaSalaProfessor r){
		return "id_professor = ( " + select_id_professor(r.getProfessor()) + " ), " +
		"id_sala = ( " + select_id_sala(r.getSala()) + " ), " +
		"finalidade = \"" + r.getFinalidade() + "\", " +
		"hora = \"" + r.getHora() + "\", " +
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
	private String delete_from_aluno(ReservaSalaProfessor r){
		return "DELETE FROM reserva_sala_aluno WHERE " +
				"hora = \"" + r.getHora() + "\" and " +
				"data = \"" + r.getData() +  "\" ;";
	}
	
	private String update(ReservaSalaProfessor r, ReservaSalaProfessor r2){
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
