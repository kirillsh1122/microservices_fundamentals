package com.microservice.architecture.overview.song_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name = "SONGS")
public class Song {

    @Id
    @NotNull(message = "id is required")
    @Min(value = 1, message = "id must be a positive number")
    @Column(name = "ID")
    private Long id;
    
    @NotBlank(message = "Song name is required")
    @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 characters")
    @Column(name = "NAME")
    private String name;

    @NotBlank(message = "Artist name is required")
    @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
    @Column(name = "ARTIST")
    private String artist;

    @NotBlank(message = "Album name is required")
    @Size(min = 1, max = 100, message = "Album name must be between 1 and 100 characters")
    @Column(name = "ALBUM")
    private String album;

    @NotBlank(message = "Duration is required")
    @Pattern(regexp = "^[0-9]{2}:[0-5][0-9]$", message = "Duration must be in mm:ss format with leading zeros")
    @Column(name = "DURATION")
    private String duration;

    @NotBlank(message = "Year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be between 1900 and 2099")
    @Column(name = "YEAR")
    private String year;

}
