package ru.samoilov.convert.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@ComponentScan("ru.samoilov.convert")
@EnableWebMvc
public class SpringConfig {

    private final ApplicationContext applicationContext;


    public SpringConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC"); // Драйвер SQLite
        dataSource.setUrl("jdbc:sqlite:C:\\3 project\\Converter\\src\\main\\resources\\db"); // Путь к твоей базе данных SQLite
        // dataSource.setUsername("your_username"); // Если требуется аутентификация
        // dataSource.setPassword("your_password"); // Если требуется аутентификация
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
