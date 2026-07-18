package com.microservice.architecture.overview.resource_processor.service;


import com.microservice.architecture.overview.resource_processor.model.ParsedResource;
import com.microservice.architecture.overview.resource_processor.utils.SongMetadataParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResourceProcessorServiceImpl implements ResourceProcessorService {

    @Override
    public ParsedResource processResource(byte[] data) throws java.io.IOException, org.apache.tika.exception.TikaException, org.xml.sax.SAXException {
        // Use the SongMetadataParser to extract metadata from the audio data
        Metadata metadata = SongMetadataParser.extractMetadata(data);

        // Create a ParsedResource object and populate it with the extracted metadata
        ParsedResource parsedResource = new ParsedResource(
                metadata.get("dc:title"),
                metadata.get("xmpDM:artist"),
                metadata.get("xmpDM:album"),
                formatDuration(metadata.get("xmpDM:duration")),
                metadata.get("xmpDM:releaseDate")
        );
        log.info("Parsed Resource: {}", parsedResource);

        return parsedResource;
    }

    private String formatDuration(String durationSeconds) {
        if (durationSeconds == null || durationSeconds.isEmpty()) {
            return "00:00";
        }
        try {
            double seconds = Double.parseDouble(durationSeconds);
            int minutes = (int) seconds / 60;
            int secs = (int) seconds % 60;
            return String.format("%02d:%02d", minutes, secs);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}
