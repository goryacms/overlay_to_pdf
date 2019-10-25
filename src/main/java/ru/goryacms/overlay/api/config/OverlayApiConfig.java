package ru.goryacms.overlay.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(
        basePackages = {"ru.goryacms.overlay.api"},
        excludeFilters = {
                @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION)}
)
public class OverlayApiConfig {

    /**
     * Логер
     */
    private static final Logger LOG = LoggerFactory.getLogger(OverlayApiConfig.class);

    @Autowired
    private Environment env;

}
