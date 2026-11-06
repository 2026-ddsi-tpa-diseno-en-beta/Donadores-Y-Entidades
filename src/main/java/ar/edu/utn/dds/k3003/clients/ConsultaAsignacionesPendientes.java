package ar.edu.utn.dds.k3003.clients;

import java.util.Set;

public interface ConsultaAsignacionesPendientes {
  boolean tieneAsignacionesPendientes(Set<String> necesidades);
}
