package com.microservice.architecture.overview.song_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.microservice.architecture.overview.song_service.exceptions.DuplicatedMetadataException;
import com.microservice.architecture.overview.song_service.exceptions.InvalidIdException;
import com.microservice.architecture.overview.song_service.model.Song;
import com.microservice.architecture.overview.song_service.repository.SongRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;

    @Override
    public Song createSong(Song song) {
        Long id = song.getId();
        if (id != null && songRepository.existsById(id)) {
            throw new DuplicatedMetadataException("Metadata for resource ID=" + id + " already exists");
        }
        Song createdSong = songRepository.save(song);
        return createdSong;
    }

    @Override
    public Optional<Song> getSongById(Long id) {
        if (id <= 0) {
            throw new InvalidIdException(
                    "Invalid value '" + id + "' for ID. Must be a positive integer");
        }
        return songRepository.findById(id);
    }

    @Override
    public List<Long> deleteSongByIds(String songIds) {

        if (songIds.length() > 200) {
            throw new InvalidIdException(
                    "CSV string is too long: received " + songIds.length() + " characters, maximum allowed is 200");
        }

        List<Long> songIdsParsed = Arrays.stream(songIds.split(","))
            .map(String::trim)
            .map(id -> {
                try {
                    return Long.parseLong(id);
                } catch (NumberFormatException e) {
                    throw new InvalidIdException(
                            "Invalid ID format: '" + id + "'. Only positive integers are allowed");
                }
            })
            .map(id -> {
                if (id <= 0) {
                    throw new InvalidIdException(
                            "Invalid value '" + id + "' for ID. Must be a positive integer");
                }
                return id;
            })
            .toList();
        
        List<Long> deletedIds = songIdsParsed.stream()
                .filter(songRepository::existsById)
                .toList();

        songRepository.deleteAllById(deletedIds);
        return deletedIds;
    }
    
}
