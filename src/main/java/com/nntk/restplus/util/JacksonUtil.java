package com.nntk.restplus.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class JacksonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

    public static String objectToJson(Object object) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (JsonGenerationException | JsonMappingException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }

        return writer.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T jsonToObject(String json, Class<?> objectClass) {
        try {
            return (T) mapper.readValue(json, objectClass);
        } catch (JsonParseException | JsonMappingException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }

        return null;
    }
}
