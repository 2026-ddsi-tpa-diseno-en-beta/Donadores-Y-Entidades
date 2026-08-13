package ar.edu.utn.dds.k3003.clients;
import java.util.List;
import java.util.ArrayList;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

// @Component le dice a Spring que esta clase es un "Bean" que puede inyectar
@Component
public class LogisticaRestClient implements FachadaLogistica {

    private final RestTemplate restTemplate = new RestTemplate();

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
    public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID) throws NoSuchElementException {
        return null;
    }

    @Override
    public DepositoDTO gestionarDonacion(String depositoID, String donacionID, String productoID, Integer cantidad) throws NoSuchElementException {
        return null;
    }

    @Override
    public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {

    }

    @Override
    public AsignacionDTO ejecutarMatchmaking(String depositoID, PaqueteDTO paqueteDTO, List<NecesidadMaterialDTO> necesidades) {
        return null;
    }

    @Override
    public void reportarEntrega(PaqueteDTO paqueteDTO) {

    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {

    }

    @Override
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {

    }

    // 1. Implementás el método que vos USÁS en tu Fachada
    @Override
    public StockDTO consultarStock(String productoID) {
        String url = urlLogistica + "/stock/" + productoID; // Fijate cuál es la ruta real de tu compa
        return restTemplate.getForObject(url, StockDTO.class);
    }

    @Override
    public List<AsignacionDTO> asignarDesdeStock(String necesidadID, String productoID, Integer cantidad) {
        return new ArrayList<>();
    }

}