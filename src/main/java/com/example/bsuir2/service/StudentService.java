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
    private final CacheService cacheService;

    public StudentService(StudentRepository studentRepository, StudentGroupRepository groupRepository, CacheService cacheService) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.cacheService = cacheService;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        Student cached = (Student) cacheService.getFromCache(id);
        if (cached != null) return cached;

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        cacheService.putInCache(id, student);
        return student;
    }

    public Student createStudent(Student student) {
        Student saved = studentRepository.save(student);
        cacheService.putInCache(saved.getId(), saved);
        return saved;
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        Student saved = studentRepository.save(student);
        cacheService.putInCache(id, saved);
        return saved;
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
        cacheService.removeFromCache(id);
    }

    public Student addStudentToGroup(Long studentId, Long groupId) {
        Student student = getStudentById(studentId);
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().add(group);
        group.getStudents().add(student);

        studentRepository.save(student);
        groupRepository.save(group);
        
        cacheService.putInCache(studentId, student);
        cacheService.putInCache(groupId, group);

        return student;
    }

    public Student removeStudentFromGroup(Long studentId, Long groupId) {
        Student student = getStudentById(studentId);
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().remove(group);
        group.getStudents().remove(student);

        studentRepository.save(student);
        groupRepository.save(group);

        cacheService.putInCache(studentId, student);
        cacheService.putInCache(groupId, group);

        return student;
    }

    public List<Student> findStudentsByFilters(String groupName, String namePart, String emailDomain) {
        return studentRepository.findStudentsByFilters(groupName, namePart, emailDomain);
    }
}
