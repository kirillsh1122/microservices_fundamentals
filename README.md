# microservices_fundamentals

## Branch: task_1_overview

### Overview
This repository implements a scalable microservices architecture using Spring Boot, Spring Cloud, and Docker. The system demonstrates fundamental microservices patterns including service discovery, API gateway routing, inter-service communication, and asynchronous processing.

### Architecture Components

**5 Core Microservices:**
1. **Eureka Server** (Port 8761) - Service Registry & Discovery
2. **Gateway Service** (Port 8085) - API Gateway for routing requests
3. **Resource Service** (Port 8080) - Resource management with Azure Blob Storage
4. **Song Service** (Port 8081) - Song management service
5. **Resource Processor** (Port 8087) - Asynchronous file processing service

**Supporting Infrastructure:**
- PostgreSQL 17 Alpine databases (Port 5432 & 5433)
- Docker Compose orchestration
- Multi-stage Dockerfiles for optimized containers
- Java 21 with Spring Boot 3.5.15 and Spring Cloud 2025.0.3

### Changes Made on task_1_overview Branch

#### 1. **Azure Blob Service Integration**
   - Integrated Azure Blob Storage for file uploads and document storage
   - Added Azure SDK dependencies to Resource Service
   - Configured Azure connection strings via environment variables
   - Enables scalable cloud-based file management

#### 2. **Docker Compose Configuration Updates**
   - Updated and optimized `compose.yaml` for multi-service orchestration
   - Configured service networking and dependencies
   - Set up environment variable configuration (.env file)
   - Ensured proper service initialization order

#### 3. **Resource Service Enhancements**
   - Updated SQL initialization scripts for resource database
   - Enhanced JPA configuration and entity mappings
   - Integrated Apache Tika for document processing capabilities
   - Added OpenFeign client for inter-service communication

#### 4. **Resource Processor Service Implementation**
   - Added new microservice for asynchronous resource processing
   - Integrates Apache Tika for document text extraction
   - Uses OpenFeign for service-to-service communication with Eureka discovery
   - Processes files independently from the main Resource Service
   - Includes Dockerfile for containerized deployment

#### 5. **Dockerfile Optimizations**
   - Fixed multi-stage build process for efficient image sizes
   - Optimized layer caching for faster builds
   - Applied consistent Java 21 Alpine base images across all services

### Technology Stack

- **Framework:** Spring Boot 3.5.15 with Spring Cloud 2025.0.3
- **Service Discovery:** Eureka Server & Client
- **API Gateway:** Spring Cloud Gateway
- **Data Persistence:** Spring Data JPA with PostgreSQL 17
- **File Processing:** Apache Tika for document parsing
- **Cloud Storage:** Azure Blob Service
- **Inter-service Communication:** OpenFeign
- **Containerization:** Docker & Docker Compose
- **Runtime:** Java 21 Alpine
- **Build Tool:** Maven with Java 21

### Key Features

✓ Service Discovery with Eureka  
✓ API Gateway with Route Configuration  
✓ Azure Blob Storage Integration  
✓ Asynchronous Resource Processing  
✓ Inter-service Communication (Feign)  
✓ PostgreSQL Multi-database Setup  
✓ Dockerized Microservices  
✓ Environment-based Configuration  
✓ Document Processing Capabilities  


