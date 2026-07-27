package com.microservice.architecture.overview.resource_service.unit.repository;


import com.microservice.architecture.overview.resource_service.model.Resource;
import com.microservice.architecture.overview.resource_service.repository.ResourceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ResourceServiceRepositoryTests {

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
