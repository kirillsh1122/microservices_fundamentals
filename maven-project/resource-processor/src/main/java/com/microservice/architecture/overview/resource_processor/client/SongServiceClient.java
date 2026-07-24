package com.microservice.architecture.overview.resource_processor.client;

import com.microservice.architecture.overview.resource_processor.dto.SongDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;


@FeignClient(name = "song-service")
public interface SongServiceClient {

    @PostMapping("/songs")
    ResponseEntity<?> createSongMetadata(@RequestBody SongDTO songDto);

}
