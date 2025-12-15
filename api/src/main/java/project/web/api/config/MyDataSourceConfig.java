package project.web.api.config;


import ext.Systems;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyDataSourceConfig {
    public MyDataSourceConfig(){
        Systems.resolveEnvironment(MyDataSourceConfig.class);
    }
}