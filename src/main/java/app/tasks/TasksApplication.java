package app.tasks;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication (exclude = { SecurityAutoConfiguration.class })
public class TasksApplication{

	public static void main(String[] args) {
		run(TasksApplication.class, args);
	}

}
