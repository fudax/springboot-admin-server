package com.fudax.sep.octopus.config;

import com.fudax.sep.octopus.detector.CustomMailNotifier;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

/**
 * @author liuyi4
 */
@Configuration
public class MailAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JavaMailSender mailSender;

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("admin.notify.mail")
    public CustomMailNotifier customMailNotifier(InstanceRepository repository) {
        return new CustomMailNotifier(mailSender, repository, customMailTemplateEngine());
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateEngine customMailTemplateEngine() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        return templateEngine;
    }
}
