package com.microservice.architecture.overview.storage_service.model;


import com.microservice.architecture.overview.storage_service.constants.STORAGE_ENTRY_TYPE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "STORAGE_ENTRY")
public class StorageEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="STORAGE_TYPE")
    private STORAGE_ENTRY_TYPE storageType;

    @Column(name="CONTAINER_NAME")
    private String containerName;

    @Column(name="PATH")
    private String path;
}
