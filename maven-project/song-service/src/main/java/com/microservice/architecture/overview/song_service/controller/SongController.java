package com.microservice.architecture.overview.song_service.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.microservice.architecture.overview.song_service.model.Song;
import com.microservice.architecture.overview.song_service.service.SongService;
import com.microservice.architecture.overview.song_service.exceptions.SongNotFoundException;
import com.microservice.architecture.overview.song_service.dto.SongIdResponse;
import com.microservice.architecture.overview.song_service.dto.DeleteResponse;
import com.microservice.architecture.overview.song_service.dto.SongDTO;
import com.microservice.architecture.overview.song_service.dto.DtoMapper;


@RestController
@RequestMapping("/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable("id") long songId) {
        return songService.getSongById(songId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new SongNotFoundException("Song metadata for ID=" + songId + " not found"));
    }

    @PostMapping
    public ResponseEntity<SongIdResponse> createResource(@RequestBody SongDTO song) {
        Song songEntity = DtoMapper.toSongEntity(song);
        Song savedSong = songService.createSong(songEntity);
        return ResponseEntity.ok(new SongIdResponse(savedSong.getId()));
    }

    @DeleteMapping
    public ResponseEntity<DeleteResponse> deleteResourcesByQuery(@RequestParam("id") String songIds) {
        List<Long> deletedIds = songService.deleteSongByIds(songIds);
        return ResponseEntity.ok(new DeleteResponse(deletedIds));
    }
    
}
