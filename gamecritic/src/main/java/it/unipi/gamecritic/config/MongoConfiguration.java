package it.unipi.gamecritic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


@Configuration
public class MongoConfiguration {
    @Autowired
    MongoDatabaseFactory mongoDbFactory;
    @Autowired
    MongoMappingContext mongoMappingContext;

    @Bean
    public MappingMongoConverter mappingMongoConverter() {
        @SuppressWarnings("null")
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        @SuppressWarnings("null")
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

}