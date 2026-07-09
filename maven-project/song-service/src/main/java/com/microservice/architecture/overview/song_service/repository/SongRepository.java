package com.microservice.architecture.overview.song_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.microservice.architecture.overview.song_service.model.Song;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {
    
}
