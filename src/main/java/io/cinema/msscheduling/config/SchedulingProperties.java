package io.cinema.msscheduling.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ms-scheduling")
public class SchedulingProperties {
    private String moviesHost;
    private int moviesPort;

    private String theaterManagementHost;
    private int theaterManagementPort;

}
