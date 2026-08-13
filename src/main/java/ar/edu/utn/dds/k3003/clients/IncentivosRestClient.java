package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class IncentivosRestClient implements FachadaIncentivos {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${URL_INCENTIVOS}")
    private String urlIncentivos;

    @Override
    public InsigniaDTO agregarInsignia(InsigniaDTO insignia) {
        return null;
    }

    @Override
    public MisionDTO agregarMision(MisionDTO mision) {
        return null;
    }
    @Override
    public List<InsigniaDTO> getInsigniasDeDonador(String donadorId) {
        String url = urlIncentivos + "/donadores/" + donadorId + "/insignias";
        try {
            InsigniaDTO[] insignias = restTemplate.getForObject(url, InsigniaDTO[].class);
            return insignias != null ? Arrays.asList(insignias) : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error al contactar a Incentivos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public MisionDTO getMisionEnCursoDeDonador(String donadorId) {
        return null;
    }

    @Override
    public void asignarMisionADonador(String donadorID, MisionDTO misionDTO) throws NoSuchElementException {

    }

    @Override
    public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO) throws NoSuchElementException {

    }

    @Override
    public void procesarDonador(String donadorID) throws NoSuchElementException {

    }

    @Override
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {

    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {

    }

}