package com.microservice.architecture.overview.resource_service.utils;


import java.io.IOException;
import java.io.ByteArrayInputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;


public class SongMetadataParser {

    public static Metadata extractMetadata(byte[] data) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (ByteArrayInputStream inputstream = new ByteArrayInputStream(data)) {
            ParseContext pcontext = new ParseContext();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(inputstream, handler, metadata, pcontext);
        }
        return metadata;
    }
    
}
