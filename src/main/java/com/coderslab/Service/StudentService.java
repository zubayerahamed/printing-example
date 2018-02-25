/**
 * 
 */
package com.coderslab.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.coderslab.entity.Student;

/**
 * @author Zubayer Ahamed
 *
 */
@Service
public class StudentService {

	public List<Student> findAll() {
		List<Student> students = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			Student student = new Student();
			student.setId(i);
			student.setName("Name " + i);
			student.setEmail("email" + i + "@gmail.com");
			students.add(student);
		}
		return students;
	}
}
