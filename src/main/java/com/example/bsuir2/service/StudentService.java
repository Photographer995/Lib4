package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentRepository;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentGroupRepository groupRepository;

    public StudentService(StudentRepository studentRepository, StudentGroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        final Student student = getStudentById(id);
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Student addStudentToGroup(Long studentId, Long groupId) {
        final Student student = getStudentById(studentId);
        final StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().add(group);
        group.getStudents().add(student);

        studentRepository.save(student);
        groupRepository.save(group);

        return student;
    }

    public Student removeStudentFromGroup(Long studentId, Long groupId) {
        final Student student = getStudentById(studentId);
        final StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().remove(group);
        group.getStudents().remove(student);

        studentRepository.save(student);
        groupRepository.save(group);

        return student;
    }

    public List<Student> findStudentsByFilters(String groupName, String namePart, String emailDomain) {
        return studentRepository.findStudentsByFilters(groupName, namePart, emailDomain);
    }
}
