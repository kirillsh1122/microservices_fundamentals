package com.microservice.architecture.overview.resource_service.integration.repository;


import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.test.database.replace=none"
})
@Testcontainers
public class ResourceServiceRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withEnv("POSTGRES_DB", "resource-db")
                    .withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void ResourceRepository_saveResource_Resource() {

        Resource resource = Resource.builder().resourceURL("https://www.example.com").build();

        Resource savedResource = resourceRepository.save(resource);

        Assertions.assertThat(savedResource).isNotNull();
        Assertions.assertThat(savedResource.getId()).isGreaterThan(0);
    }

    @Test
    public void ResourceRepository_findResourceById_Resource() {

        Resource resource = Resource.builder().resourceURL("https://www.example.com").build();

        Resource savedResource = resourceRepository.save(resource);
        Resource foundResource = resourceRepository.findById(savedResource.getId()).orElse(null);

        Assertions.assertThat(foundResource).isNotNull();
        Assertions.assertThat(foundResource.getId()).isEqualTo(savedResource.getId());
        Assertions.assertThat(foundResource.getResourceURL()).isEqualTo(savedResource.getResourceURL());
    }

    @Test
    public void ResourceRepository_deleteResourceById_void() {

        Resource resource = Resource.builder().resourceURL("https://www.example.com").build();

        Resource savedResource = resourceRepository.save(resource);
        resourceRepository.deleteById(savedResource.getId());
        Resource deletedResource = resourceRepository.findById(savedResource.getId()).orElse(null);

        Assertions.assertThat(deletedResource).isNull();
    }

}
