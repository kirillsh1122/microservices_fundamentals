package com.microservice.architecture.overview.e2e.repository.song;

import com.microservice.architecture.overview.e2e.model.song.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
}
