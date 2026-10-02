package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.StockDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.*;
import ar.edu.utn.dds.k3003.metrics.DonadorMetricas;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.repositories.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FlujosDonadoresTest {
  Fachada fachada; DonadoresRepository donadores; EntidadesRepository entidades;
  FachadaLogistica logistica; FachadaDonaciones donaciones; SimpleMeterRegistry registry;
  EntidadBenefica entidad;
  @BeforeEach void setup() {
    donadores=mock(DonadoresRepository.class); entidades=mock(EntidadesRepository.class);
    fachada=new Fachada(donadores,entidades,mock(NecesidadMaterialRepository.class));
    registry=new SimpleMeterRegistry(); ReflectionTestUtils.setField(fachada,"metrics",new DonadorMetricas(registry));
    logistica=mock(FachadaLogistica.class); donaciones=mock(FachadaDonaciones.class);
    fachada.setFachadaLogistica(logistica); fachada.setFachadaDonaciones(donaciones);
    entidad=new EntidadBenefica(); entidad.setId("e");
    when(entidades.findById("e")).thenReturn(Optional.of(entidad));
  }
  NecesidadMaterialDTO need(TipoNecesidadMaterialEnum type) {
    return new NecesidadMaterialDTO(null,"e",5,"Necesidad de prueba",10,0,"p",type);
  }
  @Test void reservarStockNoSatisfaceNecesidadAntesDeLaEntrega() {
    when(logistica.consultarStock("p")).thenReturn(new StockDTO("p",20));
    var creada=fachada.registrarNecesidad(need(TipoNecesidadMaterialEnum.EXTRAORDINARIA));
    assertEquals(0,creada.cantidadAsignada()); verify(logistica).asignarDesdeStock(creada.id(),"p",10);
    assertEquals(1, registry.get("donadores.necesidades.registradas").counter().count());
    assertEquals(0, registry.get("donadores.necesidades.unidades.entregadas").counter().count());
  }
  @Test void stockInsuficientePermiteReservaParcialExtraordinaria() {
    when(logistica.consultarStock("p")).thenReturn(new StockDTO("p",3));
    var creada=fachada.registrarNecesidad(need(TipoNecesidadMaterialEnum.EXTRAORDINARIA));
    verify(logistica).asignarDesdeStock(creada.id(),"p",3); assertEquals(0,creada.cantidadAsignada());
  }
  @Test void recurrenteNoReservaStockInsuficiente() {
    when(logistica.consultarStock("p")).thenReturn(new StockDTO("p",3));
    fachada.registrarNecesidad(need(TipoNecesidadMaterialEnum.RECURRENTE));
    verify(logistica,never()).asignarDesdeStock(anyString(),anyString(),anyInt());
  }
  @Test void errorRemotoNoSeConvierteEnAltaExitosa() {
    when(logistica.consultarStock("p")).thenThrow(new ResourceAccessException("offline"));
    assertThrows(ResourceAccessException.class,()->fachada.registrarNecesidad(need(TipoNecesidadMaterialEnum.EXTRAORDINARIA)));
    verify(entidades,never()).saveAndFlush(any());
    assertEquals(0, registry.get("donadores.necesidades.registradas").counter().count());
  }
  @Test void validaUrgenciaAntesDeConsultarServicios() {
    var dto=new NecesidadMaterialDTO(null,"e",11,"Inválida",10,0,"p",TipoNecesidadMaterialEnum.EXTRAORDINARIA);
    assertThrows(IllegalArgumentException.class,()->fachada.registrarNecesidad(dto)); verifyNoInteractions(logistica,donaciones);
  }
  @Test void registroSiempreComienzaEnCategoriaOcasional() {
    var dto=new DonadorDTO(null,"F","B",25,"f@example.com","123","Domicilio",EstadoDonadorEnum.BANEADO,"TRANSFORMADOR");
    var creado=fachada.agregarDonador(dto);
    assertEquals("OCASIONAL",creado.categoria()); assertEquals(EstadoDonadorEnum.VERIFICADO,creado.estado());
    assertEquals(1,registry.get("donadores.registrados").counter().count());
  }
  @Test void historialConservaTransicionesPorQuejas() {
    var donador=new Donador("F","B",25,"f@example.com","123","Domicilio");
    for (int n=0;n<10;n++) donador.registrarQueja(new Queja("q"+n,"d","x"+n,"Queja",java.time.LocalDate.now()));
    assertEquals(List.of(EstadoDonadorEnum.VERIFICADO,EstadoDonadorEnum.SOSPECHOSO,EstadoDonadorEnum.BANEADO),donador.getHistorialEstados());
    assertFalse(donador.puedeHacerDonacion());
  }
}
