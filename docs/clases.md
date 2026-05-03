```mermaid
classDiagram
    direction TB

    class Donador {
        -String id
        -String nombre
        -String apellido
        -Integer edad
        -String email
        -String nroDocumento
        -String domicilio
        -String categoria
        -EstadoDonadorEnum estado
        +puedeHacerDonacion() Boolean
    }

    class EntidadBenefica {
        -String id
        -String razonSocial
        -String domicilio
        -String telefono
        -String correo
    }

    class Queja {
        -String id
        -String donadorID
        -String donacionID
        -LocalDate fecha
        -String descripcion
    }

    class NecesidadMaterial {
        -String id
        -String entidadID
        -String productoSolicitadoID
        -String descripcion
        -Integer cantidadObjetivo
        -TipoNecesidadMaterialEnum tipo
    }

    class EstadoDonadorEnum {
        <<enumeration>>
        VERIFICADO
        SOSPECHOSO
        BANEADO
    }

    class TipoNecesidadMaterialEnum {
        <<enumeration>>
        RECURRENTE
        EXTRAORDINARIA
    }

    Donador "1" --> "*" Queja 
    EntidadBenefica "1" --> "*" NecesidadMaterial 
    Donador --> EstadoDonadorEnum
    NecesidadMaterial --> TipoNecesidadMaterialEnum