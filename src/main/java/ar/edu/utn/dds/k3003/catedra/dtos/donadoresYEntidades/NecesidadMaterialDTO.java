package ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades;

public record NecesidadMaterialDTO(
    String id,
    String entidadID,
    Integer nivelDeUrgencia,
    String descripcion,
    Integer cantidadObjetivo,
    Integer cantidadAsignada,
    String productoSolicitadoID,
    TipoNecesidadMaterialEnum tipo) {}
