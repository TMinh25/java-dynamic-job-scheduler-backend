package vn.com.fpt.jobservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.io.IOException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.repositories.TaskRepository;

@SpringBootTest
class JobServiceApplicationTests {

	@Autowired
	TaskRepository taskRepository;

	@Test
	void contextLoads() {
	}

	@Test
	public void whenGeneratingUUIDAsString_thenUUIDGeneratedVersion() throws IOException {
		Task task = new Task();
		task.setName("a");
		String saved = taskRepository.save(task).getId();
		Assertions.assertThat(saved).isNotEmpty();
	}
}
