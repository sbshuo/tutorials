```mermaid
graph TD
    %% User/Client Layer
    User[<i class='fa fa-user'></i> User / Customer] -- "Interacts via Web/Mobile App" --> Frontend

    %% Application Layer (FastAPI Backend)
    subgraph FastAPI Backend ["Python / FastAPI App Server"]
        Frontend[Frontend Application] -- "API Requests (e.g., /consult)" --> API_Layer
        
        subgraph API_Layer ["API Endpoints (app/api/v1/)"]
            router_stylist[stylist.py<br>(AI Stylist Endpoints)]
            router_rentals[rentals.py<br>(Rental Logic Endpoints)]
        end
        
        API_Layer -- "Validated Request Data" --> Service_Layer
        
        subgraph Service_Layer ["Business Logic (app/services/)"]
            service_ai_stylist[ai_stylist.py<br>(LLM Orchestration)]
            service_inventory[inventory.py<br>(Fashion Catalog Management)]
        end
        
        %% Component Interactions within Service Layer
        service_ai_stylist -- "1. Formats Prompt" --> Prompts[System Prompts<br>(Config / File)]
        service_ai_stylist -- "2. Async API Call" --> External_AI_APIs
        service_ai_stylist -- "3. Check Cache" --> Redis_Cache[Redis Cache<br>(Response Caching)]
        service_ai_stylist -- "4. Store/Retrieve Session" --> Redis_Cache
        
        service_inventory -- "Manage Catalog" --> SQL_DB[Relational DB (PostgreSQL)<br>(Inventory, Users, Rentals)]
        service_inventory -- "Generate Embeddings" --> External_Embedding_API
        
        service_ai_stylist -- "Recommend Items" --> service_inventory
    end
    
    %% External Services
    subgraph External_APIs ["External Cloud Services (via Async API Calls)"]
        External_AI_APIs[LLM APIs<br>(Gemini / ChatGPT)]
        External_Embedding_API[Embedding API<br>(Vector Generation)]
    end
    
    %% Data Persistence Layer
    subgraph Data_Storage ["Data Persistence Layer"]
        SQL_DB -- "Store/Query Data" --> SQLAlchemy
        Redis_Cache -- "Fast Read/Write" --> Redis_Server[Redis Server]
        
        %% Vector Database
        Vector_DB[Vector Database<br>(AlloyDB / Astra DB)] -- "Semantic Search<br>(Styling Embeddings)" --> service_inventory
        service_inventory -- "Upsert Embeddings" --> Vector_DB
    end

    %% Flow of Style Consultation (Detailed View)
    API_Layer -.-> |Example Flow| Service_Layer
    router_stylist -.-> |"1. Request Advice"| service_ai_stylist
    service_ai_stylist -.-> |"2. Async Call"| External_AI_APIs
    External_AI_APIs -.-> |"3. Async Response"| service_ai_stylist
    service_ai_stylist -.-> |"4. Query Context (Embeddings)"| service_inventory
    service_inventory -.-> |"5. Semantic Search"| Vector_DB
    Vector_DB -.-> |"6. Matching Items"| service_inventory
    service_inventory -.-> |"7. Return Products"| service_ai_stylist
    service_ai_stylist -.-> |"8. Personalize & Return Advice"| router_stylist
    
    %% Styling Classes
    classDef external fill:#f9f,stroke:#333,stroke-width:2px;
    classDef internal fill:#e1f5fe,stroke:#01579b,stroke-width:1px;
    classDef storage fill:#fff9c4,stroke:#fbc02d,stroke-width:1px,stroke-dasharray: 5 5;
    classDef fastapi fill:#e0f2f1,stroke:#00695c,stroke-width:2px;

    class External_AI_APIs,External_Embedding_API external;
    class API_Layer,Service_Layer fastapi;
    class SQL_DB,Vector_DB,Redis_Cache,Redis_Server,Prompts storage;
```
