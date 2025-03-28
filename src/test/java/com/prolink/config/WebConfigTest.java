package com.prolink.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebConfigTest {

    @InjectMocks
    private WebConfig webConfig;

    private AnnotationConfigWebApplicationContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);
        context.setServletContext(new MockServletContext());
        context.refresh();
    }

    @Test
    void testAddResourceHandlers() {
        // Arrange
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(context, context.getServletContext());

        // Act
        webConfig.addResourceHandlers(registry);

        // Assert
        assertTrue(registry.hasMappingForPattern("/uploads/profile_pictures/**"));
    }
}