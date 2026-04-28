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
        -List~Queja~ listaDeQuejas
        +puedeHacerDonacion() Boolean
        +registrarQueja(Queja nuevaQueja) void
    }

    class EntidadBenefica {
        -String id
        -String razonSocial
        -String domicilio
        -String telefono
        -String correo
        -List~NecesidadMaterial~ listaDeNecesidades
        +agregarNecesidad(NecesidadMaterial nuevaNecesidad) void
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
        -Integer nivelDeUrgencia
    }

    class EstadoDonadorEnum {
        <<enum>>
        VERIFICADO
        SOSPECHOSO
        BANEADO
    }

    Donador "1" *-- "0..*" Queja 
    EntidadBenefica "1" *-- "0..*" NecesidadMaterial 
    Donador --> EstadoDonadorEnum 
