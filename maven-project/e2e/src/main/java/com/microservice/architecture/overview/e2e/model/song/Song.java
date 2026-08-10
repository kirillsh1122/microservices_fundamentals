package com.microservice.architecture.overview.e2e.model.song;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SONGS")
public class Song {

    @Id
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "NAME")
    private String name;

    @Column(name = "ARTIST")
    private String artist;

    @Column(name = "ALBUM")
    private String album;

    @Column(name = "DURATION")
    private String duration;

    @Column(name = "YEAR")
    private String year;

}
