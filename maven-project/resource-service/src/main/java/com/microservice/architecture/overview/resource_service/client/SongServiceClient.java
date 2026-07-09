package com.microservice.architecture.overview.resource_service.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;

import com.microservice.architecture.overview.resource_service.dto.SongDTO;
import com.microservice.architecture.overview.resource_service.dto.SongIdResponse;
import com.microservice.architecture.overview.resource_service.dto.DeleteResponse;


@FeignClient(name = "song-service")
public interface SongServiceClient {

    @PostMapping("/songs")
    ResponseEntity<SongIdResponse> createSongMetadata(@RequestBody SongDTO songDto);

    @DeleteMapping("/songs")
    ResponseEntity<DeleteResponse> deleteSongMetadata(@RequestParam("id") String songIds);
    
}
