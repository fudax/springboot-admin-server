package com.fudax.octopus.detector;


import com.fudax.octopus.constants.Env;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.notify.MailNotifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import static com.fudax.octopus.constants.OctopusConstants.SPECIAL_CONTEXT;
import static com.fudax.octopus.detector.CustomNameJudge.someContextCanPass;
import static com.fudax.octopus.model.ContextUi.eventApply;


public class CustomMailNotifier extends MailNotifier {

    private static final String SUBJECT = "%s%s 目前%s";
    private final JavaMailSender mailSender;


    public CustomMailNotifier(JavaMailSender mailSender, InstanceRepository repository, TemplateEngine templateEngine) {
        super(mailSender, repository, templateEngine);
        this.mailSender = mailSender;
    }

    @Override
    public Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        if (!someContextCanPass(instance.getRegistration().getName(), SPECIAL_CONTEXT)) {
            return Mono.empty();
        }
        Context ctx = new Context();
        ctx.setVariable("info", eventApply(event, instance, this.getBaseUrl(), getLastStatus(event.getInstance())));
        return Mono.fromRunnable(() -> doNotify(ctx, buildMailTitle(instance, ctx)));
    }

    public void doNotify(Context ctx, String subject) {
        ctx.setVariables(this.getAdditionalProperties());
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            message.setText(getBody(ctx).replaceAll("\\s+\\n", "\n"), true);
            message.setSubject(subject);
            message.setTo(this.getTo());
            message.setCc(this.getCc());
            message.setFrom(this.getFrom());
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            throw new RuntimeException("Error sending mail notification", ex);
        }
    }

    private String buildMailTitle(Instance instance, Context ctx) {
        String title;
        String envName = instance.getRegistration().getMetadata().get("env");
        Env env = Env.valueOfName(envName);
        if (instance.getStatusInfo().isOffline()) {
            title = String.format(SUBJECT, env.getName(), "的服务" + instance.getRegistration().getName(), "处于不可用状态");
        } else {
            title = env.getName() + ":" + getSubject(ctx);
        }
        return title;
    }

}
