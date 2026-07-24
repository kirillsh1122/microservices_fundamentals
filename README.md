# microservices_fundamentals

### Overview
This repository implements a scalable microservices architecture using Spring Boot, Spring Cloud, and Docker. The system demonstrates fundamental microservices patterns including service discovery, API gateway routing, inter-service communication, asynchronous event processing with Kafka, and cloud-native file storage integration.

### Architecture Components

**5 Core Microservices:**
1. **Eureka Server** (Port 8761) - Service Registry & Discovery
2. **Gateway Service** (Port 8085) - API Gateway for routing requests
3. **Resource Service** (Port 8080) - Resource management with Azure Blob Storage integration
4. **Song Service** (Port 8081) - Song management service with JPA persistence
5. **Resource Processor** (Port 8087) - Asynchronous file processing service with Kafka consumer

**Supporting Infrastructure:**
- PostgreSQL 17 Alpine databases (Port 5432 & 5433)
- Apache Kafka 7.8.0 with KafkaUI for event streaming (Port 9092 & 8090)
- Docker Compose orchestration for microservices
- Multi-stage Dockerfiles with Java 21 Alpine for optimized container images
- Maven-based polyrepo project structure with shared parent POM

### Changes Made

#### 1. **Azure Blob Storage Integration**
   - **Resource Service:** Added Azure Blob Storage client for cloud-native file management
   - **Dependencies:** Spring Cloud Azure Storage Blob Starter & Azure Identity libraries (v6.4.0)
   - **Configuration:** Azure credentials (client-id, client-secret, tenant-id) via environment variables
   - **Container Setup:** Configured blob container named "resource" with dedicated storage account
   - **Benefits:** Scalable cloud-based file storage without local disk constraints

#### 2. **Event-Driven Architecture with Apache Kafka**
   - **Kafka Cluster:** Single-node Kafka 7.8.0 with KafkaUI for monitoring
   - **Docker Network:** Dedicated Kafka network with bridge driver for container communication
   - **Resource Service:** Kafka producer configuration with JSON serialization
   - **Resource Processor:** Kafka consumer with consumer group and JSON deserialization
   - **Configuration:** Kafka bootstrap servers and topic management via environment variables
   - **DDL Configuration:** Database DDL auto set to "none" for safety in production

#### 3. **Docker Compose Orchestration**
   - **Unified Compose:** `compose.yaml` orchestrates all 5 microservices plus PostgreSQL databases
   - **Service Dependencies:** Proper ordering (Eureka → Gateways/Services → Dependent Services)
   - **Network Configuration:** Multi-network support (Kafka network + default network)
   - **Volume Management:** Database initialization scripts mounted as Docker volumes
   - **Environment Management:** Centralized `.env` file with service-specific configurations
   - **Port Mapping:** All services exposed on individual ports for local development
   - **Separate Kafka Compose:** `kafka_compose.yaml` for optional separate Kafka cluster deployment

#### 4. **Resource Service Enhancements**
   - **Spring Kafka Producer:** JPA-integrated Kafka publishing for resource events
   - **Apache Tika Integration:** Tika 3.3.1 core and parsers for document text extraction
   - **OpenFeign Client:** Inter-service communication with Eureka service discovery
   - **Retry Mechanism:** @EnableRetry for resilient inter-service calls
   - **Azure Blob Configuration:** Environment-based credential and endpoint configuration
   - **Database:** PostgreSQL 17 with resources table initialized via init.sql
   - **Configuration File:** YAML-based application configuration with sensible defaults

#### 5. **Resource Processor Microservice - NEW**
   - **Purpose:** Autonomous asynchronous file processing service
   - **Service Registration:** Spring Cloud Netflix Eureka client for discovery
   - **Event Consumption:** Kafka consumer listening for resource processing events
   - **Document Processing:** Apache Tika 3.3.1 for extracting text from various document formats
   - **Inter-service Communication:** OpenFeign clients for Resource Service and Song Service
   - **Service Clients:**
     - SongServiceClient: Service-to-service communication with Song Service
     - ResourceServiceClient: Communication with Resource Service for processing results
   - **Utilities:** SongMetadataParser for extracting metadata from processed resources
   - **Data Transfer:** DTOs for SongDTO, ParsedResource, and ResourceProcessorDefaultResponse
   - **Endpoints:** REST controller for processing status and monitoring
   - **Containerization:** Multi-stage Dockerfile for optimized image size

#### 6. **Database Configuration & Initialization**
   - **Resource Database:** PostgreSQL 17 Alpine on port 5432
     - Table: `resources` with auto-incremented ID and resource URL
   - **Song Database:** PostgreSQL 17 Alpine on port 5433
     - Table: `songs` with comprehensive schema (id, name, artist, album, year, duration)
   - **Init Scripts:** SQL scripts automatically executed on container startup
   - **Credentials:** Environment-based configuration for security

#### 7. **Dockerfile Optimizations**
   - **Multi-Stage Build:** Maven build stage → Runtime stage for reduced image sizes
   - **Build Stage:** Maven 4.0.0 RC5 with Eclipse Temurin 21 and Alpine
   - **Runtime Stage:** Eclipse Temurin 21 JRE Alpine 3.23 for lightweight containers
   - **Dependency Caching:** Pre-download dependencies layer for faster builds
   - **Layer Optimization:** Separation of concerns for Docker layer caching efficiency

#### 8. **Dependency Management & Versioning**
   - **Parent POM:** Spring Boot 3.5.0 with centralized version management
   - **Spring Cloud:** Version 2025.0.0 with Netflix Eureka and OpenFeign
   - **Azure Spring Cloud:** Version 6.4.0 for Azure service integration
   - **Apache Tika:** Version 3.3.1 for document processing
   - **Spring Kafka:** Managed by Spring Boot parent for compatibility
   - **Spring Retry:** Cross-cutting concern for resilience patterns
   - **Lombok:** Annotation processor for reducing boilerplate code

#### 9. **Service-to-Service Communication**
   - **OpenFeign:** Declarative REST clients with automatic Eureka discovery
   - **Resource Processor Clients:**
     - Calls Resource Service for resource metadata and updates
     - Calls Song Service for song information retrieval
   - **Resource Service Client:** Calls Resource Processor and Song Service as needed
   - **Load Balancing:** Spring Cloud default load balancing for discovery
   - **Error Handling:** Feign exception handling with custom fallbacks

#### 10. **Application Configuration**
   - **Resource Service (application.yml):**
     - Database: PostgreSQL with environment-based JDBC URLs
     - Kafka: Producer with JSON serialization
     - Azure: Credential and storage account configuration
     - Eureka: Service registration and discovery
   - **Song Service (application.properties):**
     - Basic Eureka client configuration
     - PostgreSQL database on separate port
     - Service naming for discovery
   - **Resource Processor (application.yaml):**
     - Kafka consumer group configuration
     - Auto-offset reset to earliest for replay capability
     - Eureka client configuration for discovery

### Technology Stack

- **Framework:** Spring Boot 3.5.0 with Spring Cloud 2025.0.0
- **Service Discovery:** Eureka Server & Client (Netflix)
- **API Gateway:** Spring Cloud Gateway
- **Data Persistence:** Spring Data JPA with PostgreSQL 17 Alpine
- **Event Streaming:** Apache Kafka 7.8.0 with Confluent images
- **Event Monitoring:** KafkaUI for cluster visualization
- **File Processing:** Apache Tika 3.3.1 (core + standard parsers)
- **Cloud Storage:** Azure Blob Storage Service (Spring Cloud Azure 6.4.0)
- **Inter-service Communication:** OpenFeign with Spring Cloud
- **Resilience:** Spring Retry for transient failure handling
- **Containerization:** Docker & Docker Compose with multi-stage builds
- **Runtime:** Java 21 Eclipse Temurin JRE Alpine 3.23
- **Build Tool:** Maven 4.0.0 RC5 with Java 21 compiler
- **Code Generation:** Lombok for annotation processing

### Key Features Implemented

✓ **Service Discovery:** Eureka server with client registration and discovery  
✓ **API Gateway:** Spring Cloud Gateway with route configuration  
✓ **Azure Blob Storage:** Cloud-native file management with credential-based authentication  
✓ **Event-Driven Architecture:** Kafka-based asynchronous processing pipeline  
✓ **Microservice Orchestration:** Docker Compose with service dependencies and networking  
✓ **Document Processing:** Apache Tika for multi-format text extraction  
✓ **Inter-service Communication:** OpenFeign with automatic service discovery  
✓ **Resilience Patterns:** Retry mechanisms for fault tolerance  
✓ **Multi-Database Setup:** Separate PostgreSQL instances for different services  
✓ **Dockerized Deployment:** Production-ready multi-stage Docker images  
✓ **Environment Configuration:** 12-factor app compliance with .env file  
✓ **Kafka Monitoring:** KafkaUI for real-time event stream visualization


