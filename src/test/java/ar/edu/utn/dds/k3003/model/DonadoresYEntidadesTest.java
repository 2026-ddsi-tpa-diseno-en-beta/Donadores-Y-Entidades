package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class DonadoresYEntidadesTest {

  Fachada fachadaInstancia;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    fachadaInstancia = new Fachada();
  }

  // --- TESTS DE DONADORES ---

  @Test
  @DisplayName("Debería agregar un donador correctamente y generar su ID")
  void testAgregarDonador() {
    DonadorDTO donadorParaRegistrar = new DonadorDTO(null, "Lola", "Sistemas", 21, "lola@utn.edu", "12345678", "Medrano 951", EstadoDonadorEnum.VERIFICADO, "COMUN");
    
    DonadorDTO donadorYaRegistrado = fachadaInstancia.agregarDonador(donadorParaRegistrar);
    
    Assertions.assertNotNull(donadorYaRegistrado.id(), "La fachada debe generar el ID del donador");
  }

  @Test
  @DisplayName("Debería encontrar un donador correctamente por su identificador")
  void testBuscarDonadorExitoso() {
    DonadorDTO donadorRegistrado = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Carlos", "Sistemas", 30, "carlos@utn.edu", "87654321", "Medrano 951", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    
    DonadorDTO donadorEncontrado = fachadaInstancia.buscarDonadorPorID(donadorRegistrado.id());
    
    Assertions.assertEquals(donadorRegistrado.nombre(), donadorEncontrado.nombre());
    Assertions.assertEquals(donadorRegistrado.id(), donadorEncontrado.id());
  }

  @Test
  @DisplayName("Debería modificar la categoría de un donador correctamente")
  void testModificarCategoria() {
    DonadorDTO donadorInicial = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Pepe", "Test", 25, "p@p.com", "445566", "Av 1", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    
    DonadorDTO donadorActualizado = fachadaInstancia.modifcarCategoria(donadorInicial.id(), "PLATINO");
    
    Assertions.assertEquals("PLATINO", donadorActualizado.categoria());
  }

  @Test
  @DisplayName("Debería actualizar el estado del donador de Verificado a Sospechoso")
  void testModificarEstadoDonador() {
    DonadorDTO donador = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Jorge", "Perez", 28, "jorge@utn.edu", "998877", "Sarmiento 100", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    
    DonadorDTO actualizado = fachadaInstancia.modificarEstado(donador.id(), EstadoDonadorEnum.SOSPECHOSO);
    
    Assertions.assertEquals(EstadoDonadorEnum.SOSPECHOSO, actualizado.estado());
  }

  @Test
  @DisplayName("Debería permitir donar a un verificado y prohibirlo si es cambiado a Baneado")
  void testPuedeDonarSegunEstado() {
    DonadorDTO donador = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Marta", "Gomez", 40, "marta@utn.edu", "112233", "Calle Falsa 123", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    
    Assertions.assertTrue(fachadaInstancia.puedeDonar(donador.id()));
    
    fachadaInstancia.modificarEstado(donador.id(), EstadoDonadorEnum.BANEADO);
    Assertions.assertFalse(fachadaInstancia.puedeDonar(donador.id()));
  }

  @Test
  @DisplayName("Debería lanzar NoSuchElementException al buscar un ID de donador que no existe")
  void testErrorDonadorNoEncontrado() {
    Assertions.assertThrows(NoSuchElementException.class, () -> {
        fachadaInstancia.buscarDonadorPorID("ID-INEXISTENTE");
    });
  }

  // --- TESTS DE QUEJAS ---

  @Test
  @DisplayName("Debería registrar una queja vinculada a un donador")
  void testAgregarQueja() {
    DonadorDTO donadorDeLaQueja = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Ana", "L", 22, "a@l.com", "3", "Calle 3", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    QuejaDTO quejaParaVincular = new QuejaDTO(null, "DON-789", donadorDeLaQueja.id(), LocalDate.now(), "Paquete dañado");
    
    QuejaDTO quejaResultado = fachadaInstancia.agregarQueja(quejaParaVincular);
    
    Assertions.assertNotNull(quejaResultado.id(), "La queja debe tener un ID generado");
  }

  @Test
  @DisplayName("Debería recuperar todas las quejas de un donador específico")
  void testObtenerQuejasDeDonador() {
    DonadorDTO donador = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Lucas", "DDS", 20, "lucas@utn.edu", "554433", "Campus 1", EstadoDonadorEnum.VERIFICADO, "COMUN"));
    fachadaInstancia.agregarQueja(new QuejaDTO(null, "DON-001", donador.id(), LocalDate.now(), "Queja 1"));
    fachadaInstancia.agregarQueja(new QuejaDTO(null, "DON-002", donador.id(), LocalDate.now(), "Queja 2"));
    
    List<QuejaDTO> listaQuejas = fachadaInstancia.obtenerQuejasDe(donador.id());
    
    Assertions.assertEquals(2, listaQuejas.size());
  }

  // --- TESTS DE ENTIDADES Y NECESIDADES ---

  @Test
  @DisplayName("Debería agregar una entidad benéfica y permitir buscarla")
  void testAgregarYBuscarEntidad() {
    EntidadBeneficaDTO entidadHabilitada = new EntidadBeneficaDTO("ENT-100", "Fundación DDS", "Medrano 951", "4444-5555", "contacto@dds.com");
    fachadaInstancia.agregarEntidad(entidadHabilitada);
    
    EntidadBeneficaDTO entidadEncontrada = fachadaInstancia.buscarEntidadPorID("ENT-100");
    
    Assertions.assertEquals("Fundación DDS", entidadEncontrada.razonSocial());
  }

  @Test
  @DisplayName("Debería registrar una necesidad material para una entidad")
  void testRegistrarNecesidad() {
    fachadaInstancia.agregarEntidad(new EntidadBeneficaDTO("ENT-100", "Fundación DDS", "Dir", "123", "m@m.com"));
    NecesidadMaterialDTO necesidadPendiente = new NecesidadMaterialDTO(null, "ENT-100", 1, "Arroz", 100, "PROD-ARROZ", TipoNecesidadMaterialEnum.EXTRAORDINARIA);
    
    NecesidadMaterialDTO necesidadRegistrada = fachadaInstancia.registrarNecesidad(necesidadPendiente);
    
    Assertions.assertNotNull(necesidadRegistrada.id());
  }

  @Test
  @DisplayName("Debería reducir la cantidad de una necesidad al satisfacerla parcialmente")
  void testSatisfacerNecesidad() {
    fachadaInstancia.agregarEntidad(new EntidadBeneficaDTO("ENT-200", "Comedor", "Dir", "1", "c@c.com"));
    NecesidadMaterialDTO original = fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "ENT-200", 2, "Leche", 50, "PROD-LECHE", TipoNecesidadMaterialEnum.EXTRAORDINARIA));
    
    NecesidadMaterialDTO actualizada = fachadaInstancia.satisfacerNecesidad(original.id(), 20);
    
    Assertions.assertEquals(30, actualizada.cantidadObjetivo());
  }

  @Test
  @DisplayName("Debería listar solo necesidades que aún no han sido totalmente satisfechas")
  void testObtenerNecesidadesInsatisfechas() {
    fachadaInstancia.agregarEntidad(new EntidadBeneficaDTO("ENT-500", "Ayuda", "Dir", "5", "a@a.com"));
    fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "ENT-500", 1, "Arroz 1", 10, "PRODUCTO-TEST", TipoNecesidadMaterialEnum.EXTRAORDINARIA));
    NecesidadMaterialDTO satisfecha = fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "ENT-500", 1, "Arroz 2", 5, "PRODUCTO-TEST", TipoNecesidadMaterialEnum.EXTRAORDINARIA));
    
    fachadaInstancia.satisfacerNecesidad(satisfecha.id(), 5); // Cantidad llega a 0
    
    List<NecesidadMaterialDTO> insatisfechas = fachadaInstancia.obtenerNecesidadesInsatisfechasDe("PRODUCTO-TEST");
    
    Assertions.assertEquals(1, insatisfechas.size(), "Debe devolver solo la necesidad con cantidad > 0");
  }

  // --- TESTS DE ESTADÍSTICAS ---

  @Test
  @DisplayName("Debería generar el DTO de estadísticas con la información correcta del donador")
  void testObtenerEstadisticasDonador() {
    DonadorDTO donador = fachadaInstancia.agregarDonador(new DonadorDTO(null, "Esteban", "Quito", 35, "esteban@utn.edu", "776655", "Calle 10", EstadoDonadorEnum.VERIFICADO, "ORO"));
    
    DonadorStatsDTO estadisticas = fachadaInstancia.estadisticasDonador(donador.id());
    
    Assertions.assertNotNull(estadisticas);
    Assertions.assertEquals("Esteban", estadisticas.nombre());
    Assertions.assertEquals("ORO", estadisticas.categoria());
  }
  
  // --- TESTS LISTAS ---

    @Test
    @DisplayName("Debería listar múltiples donadores correctamente")
    void testListarMultiplesDonadores() {
        // Agregamos 3 donadores
        fachadaInstancia.agregarDonador(new DonadorDTO(null, "Lola", "S", 20, "l@u.com", "1", "D1", EstadoDonadorEnum.VERIFICADO, "C1"));
        fachadaInstancia.agregarDonador(new DonadorDTO(null, "Pepe", "P", 30, "p@u.com", "2", "D2", EstadoDonadorEnum.VERIFICADO, "C2"));
        fachadaInstancia.agregarDonador(new DonadorDTO(null, "Ana", "A", 25, "a@u.com", "3", "D3", EstadoDonadorEnum.VERIFICADO, "C3"));

        List<DonadorDTO> lista = fachadaInstancia.listarDonadores();

        Assertions.assertEquals(3, lista.size(), "La lista debería tener exactamente 3 donadores");
    }

    @Test
    @DisplayName("Debería devolver lista vacía si no hay entidades registradas")
    void testListarEntidadesVacia() {
        List<EntidadBeneficaDTO> lista = fachadaInstancia.listarEntidades();
        
        Assertions.assertNotNull(lista);
        Assertions.assertTrue(lista.isEmpty(), "La lista debería estar vacía al iniciar");
    }

    // --- TESTS NECESIDADES ---

    @Test
    @DisplayName("No debería permitir satisfacer una necesidad con cantidad mayor a la disponible")
    void testSatisfacerNecesidadExcedida() {
        fachadaInstancia.agregarEntidad(new EntidadBeneficaDTO(null, "Comedor 1", "Dir", "123", "c@c.com"));
        NecesidadMaterialDTO n = fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "1", 1, "Leche", 10, "PROD-1", TipoNecesidadMaterialEnum.EXTRAORDINARIA));

        fachadaInstancia.satisfacerNecesidad(n.id(), 50);

        List<NecesidadMaterialDTO> resultado = fachadaInstancia.obtenerNecesidadesInsatisfechasDe("PROD-1");
        Assertions.assertTrue(resultado.isEmpty(), "La necesidad debería considerarse satisfecha (cantidad 0)");
    }

    @Test
    @DisplayName("Debería filtrar necesidades de distintos productos")
    void testFiltradoPorProducto() {
        fachadaInstancia.agregarEntidad(new EntidadBeneficaDTO(null, "Entidad X", "D", "T", "E"));
        

        fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "1", 1, "Arroz 1", 10, "ARROZ", TipoNecesidadMaterialEnum.EXTRAORDINARIA));
        fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "1", 1, "Arroz 2", 20, "ARROZ", TipoNecesidadMaterialEnum.EXTRAORDINARIA));
        fachadaInstancia.registrarNecesidad(new NecesidadMaterialDTO(null, "1", 1, "Fideos", 5, "FIDEOS", TipoNecesidadMaterialEnum.EXTRAORDINARIA));

        List<NecesidadMaterialDTO> soloArroz = fachadaInstancia.obtenerNecesidadesInsatisfechasDe("ARROZ");

        Assertions.assertEquals(2, soloArroz.size(), "Debería haber encontrado solo las 2 de Arroz");
        Assertions.assertTrue(soloArroz.stream().allMatch(n -> n.productoSolicitadoID().equals("ARROZ")));
    }


}