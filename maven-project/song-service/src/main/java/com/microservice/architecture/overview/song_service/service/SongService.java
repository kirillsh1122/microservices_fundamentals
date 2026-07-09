package com.microservice.architecture.overview.song_service.service;


import java.util.List;
import java.util.Optional;

import com.microservice.architecture.overview.song_service.model.Song;


public interface SongService {

    Optional<Song> getSongById(Long id);

    Song createSong(Song song);

    List<Long> deleteSongByIds(String songIds);
    
}
