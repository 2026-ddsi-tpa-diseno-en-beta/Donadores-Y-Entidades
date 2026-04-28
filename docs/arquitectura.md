```mermaid
graph LR
    
        C[Cliente]
    

    subgraph Nuestra_Solucion [Nuestra Solución]
        GW{API Gateway}
        
        
        S1[Servicio de Donadores y Entidades]
        
        
        S2[Servicio de Donaciones]
        S3[Servicio de Incentivos]
        S4[Servicio de Logística]
    end

  
    C --> GW
    GW --> S1

    
    S2 -.->|Verifica Donador / Registra Queja| S1
    S3 -.->|Verifica Existencia de Donador| S1
    S4 -.->|Consulta y Satisface Necesidades| S1

    
    S1 -.->|Consulta Insignias y Misiones| S3

