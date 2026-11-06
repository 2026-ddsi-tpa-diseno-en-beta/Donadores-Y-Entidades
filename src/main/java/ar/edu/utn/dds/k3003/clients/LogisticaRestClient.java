package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.*;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// @Component le dice a Spring que esta clase es un "Bean" que puede inyectar
@Component
public class LogisticaRestClient implements FachadaLogistica, ConsultaAsignacionesPendientes {

  @Override
  public boolean tieneAsignacionesPendientes(java.util.Set<String> necesidades) {
    if (necesidades.isEmpty()) return false;
    var assignments =
        restTemplate.getForObject(urlLogistica + "/asignaciones", AsignacionDTO[].class);
    if (assignments == null)
      throw new org.springframework.web.client.RestClientException(
          "Respuesta de asignaciones vacía");
    return java.util.Arrays.stream(assignments)
        .anyMatch(
            a -> necesidades.contains(a.necesidadID()) && "ASIGNADA".equals(a.estado().name()));
  }

  private final RestTemplate restTemplate =
      ar.edu.utn.dds.k3003.observability.HttpClients.restTemplate();

  @Value("${URL_LOGISTICA}")
  private String urlLogistica;

  @Override
  public DepositoDTO agregarDeposito(DepositoDTO deposito) {
    return null;
  }

  @Override
  public DepositoDTO buscarDepositoPorID(String depositoID) throws NoSuchElementException {
    return null;
  }

  @Override
  public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID)
      throws NoSuchElementException {
    return null;
  }

  @Override
  public DepositoDTO gestionarDonacion(
      String depositoID, String donacionID, String productoID, Integer cantidad)
      throws NoSuchElementException {
    return null;
  }

  @Override
  public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {}

  @Override
  public AsignacionDTO ejecutarMatchmaking(
      String depositoID, PaqueteDTO paqueteDTO, List<NecesidadMaterialDTO> necesidades) {
    return null;
  }

  @Override
  public void reportarEntrega(PaqueteDTO paqueteDTO) {}

  @Override
  public void setFachadaDonadoresYEntidades(
      FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {}

  @Override
  public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {}

  // 1. Implementás el método que vos USÁS en tu Fachada
  @Override
  public StockDTO consultarStock(String productoID) {
    String url = urlLogistica + "/stock/" + productoID; // Fijate cuál es la ruta real de tu compa
    return restTemplate.getForObject(url, StockDTO.class);
  }

  @Override
  public List<AsignacionDTO> asignarDesdeStock(
      String necesidadID, String productoID, Integer cantidad) {
    String url = urlLogistica + "/stock/asignaciones";

    java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
    requestBody.put("necesidadId", necesidadID);
    requestBody.put("productoId", productoID);
    requestBody.put("cantidad", cantidad);

    {
      var headers = new org.springframework.http.HttpHeaders();
      headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
      AsignacionDTO[] respuesta =
          restTemplate.postForObject(
              url,
              new org.springframework.http.HttpEntity<>(requestBody, headers),
              AsignacionDTO[].class);

      if (respuesta != null) {
        return java.util.Arrays.asList(respuesta);
      } else {
        return new java.util.ArrayList<>();
      }
    }
  }
}
