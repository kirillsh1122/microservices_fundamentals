package com.microservice.architecture.overview.song_service.dto;

import com.microservice.architecture.overview.song_service.model.Song;

public class DtoMapper {
    
    public static SongDTO toSongDto(Song song) {
        return new SongDTO(
            song.getId(),
            song.getName(),
            song.getArtist(),
            song.getAlbum(),
            song.getDuration(),
            song.getYear()
        );
    }

    public static Song toSongEntity(SongDTO songDto) {
        Song song = new Song();
        song.setId(songDto.id());
        song.setName(songDto.name());
        song.setArtist(songDto.artist());
        song.setAlbum(songDto.album());
        song.setDuration(songDto.duration());
        song.setYear(songDto.year());
        return song;
    }

}