package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class DonacionesRestClient implements FachadaDonaciones {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${URL_DONACIONES}")
    private String urlDonaciones;

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        return null;
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
        return null;
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) throws NoSuchElementException {
        return List.of();
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
        return null;
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
        return null;
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) {
        try {

            return restTemplate.getForObject(urlDonaciones + "/productos/" + productoID, ProductoDTO.class);
        } catch (Exception e) {

            throw new NoSuchElementException("Producto no existe en Donaciones");
        }
    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
        return null;
    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) throws NoSuchElementException {
        return null;
    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {

    }

    @Override
    public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {

    }
}