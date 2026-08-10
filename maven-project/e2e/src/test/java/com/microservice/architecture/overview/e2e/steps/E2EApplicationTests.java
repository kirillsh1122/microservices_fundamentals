package com.microservice.architecture.overview.e2e.steps;

import com.azure.storage.blob.BlobContainerClient;
import com.microservice.architecture.overview.e2e.E2EApplication;
import com.microservice.architecture.overview.e2e.dto.ResourceIdResponse;
import com.microservice.architecture.overview.e2e.repository.resource.ResourceRepository;
import com.microservice.architecture.overview.e2e.repository.song.SongRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(classes = E2EApplication.class)
public class E2EApplicationTests {

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private BlobContainerClient blobContainerClient;

	private byte [] mp3Data;
	private ResponseEntity<ResourceIdResponse> latestResourceIdResponse;

	@Value("${project.service.gateway.url}")
	private String gatewayUrl;

	@Given("the resource-service is running")
	public void the_resource_service_is_running() {
		String url = "http://localhost:8080/actuator/health";
		ResponseEntity<Map<String, Object>> response =
				restTemplate.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<>() {}
				);
		Map<String, Object> body = response.getBody();

		assertNotNull(body);
		assertEquals("UP", body.get("status"));
	}

	@And("the song-service is running")
	public void the_song_service_is_running() {
		String url = "http://localhost:8081/actuator/health";
		ResponseEntity<Map<String, Object>> response =
				restTemplate.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<>() {}
				);
		Map<String, Object> body = response.getBody();

		assertNotNull(body);
		assertEquals("UP", body.get("status"));
	}

	@And("A valid .mp3 file {string}")
	public void a_valid_mp3_file_with_metadata(String filename) throws IOException {
		mp3Data = loadMp3Binary();
		assertNotNull(mp3Data, "valid-sample-with-required-tags.mp3 file should be loaded successfully");
	}

	@When("the user uploads the .mp3 file to the resource-service")
	public void the_user_uploads_the_mp3_file_to_the_resource_service() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf("audio/mpeg"));
		HttpEntity<byte[]> request = new HttpEntity<>(mp3Data, headers);
		try {
			latestResourceIdResponse = restTemplate.postForEntity(
					gatewayUrl + "/resources",
					request,
					ResourceIdResponse.class
			);
		} catch (Exception e) {
			throw new RuntimeException("Failed to upload file to resource service", e);
		}
	}

	@Then("the resource-service should return a resource ID")
	public void the_resource_service_should_return_a_resource_id() {
		assertNotNull(latestResourceIdResponse, "Response should not be null");
		assertEquals(HttpStatus.OK, latestResourceIdResponse.getStatusCode(), "Upload should be successful");
		assertTrue(latestResourceIdResponse.getBody().id() > 0, "Resource ID should be a positive Long value");
	}

	@And("the resource is present in resource-db")
	public void the_resource_is_present_in_resource_db() {
		Long resourceId = latestResourceIdResponse.getBody().id();
		assertNotNull(resourceRepository.findById(resourceId), "Resource should be present in resource-db");
	}

	@And("the resource is present in song-db")
	public void the_resource_is_present_in_song_db() {
		Long resourceId = latestResourceIdResponse.getBody().id();
		assertNotNull(songRepository.findById(resourceId), "Resource should be present in song-db");
	}

	@And("the resource is present in blob-storage")
	public void the_resource_is_present_in_blob_storage() {
		Long resourceId = latestResourceIdResponse.getBody().id();
		String resourceUrl = resourceRepository.findById(resourceId).get().getResourceURL();
		String blobName = getBlobNameFromURL(resourceUrl);
		assertNotNull(blobContainerClient.getBlobClient(blobName), "Resource should be present in blob-storage");
	}

	private byte [] loadMp3Binary() throws IOException  {
		try (InputStream is = getClass().getClassLoader()
				.getResourceAsStream("valid-sample-with-required-tags.mp3")) {
			return is.readAllBytes();
		}
	}

	private String getBlobNameFromURL(String resourceURL) {
		try {
			URI uri = URI.create(resourceURL);
			String path = uri.getPath();
            return URLDecoder.decode(path.substring(path.lastIndexOf("/") + 1), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw e;
		}
	}

}