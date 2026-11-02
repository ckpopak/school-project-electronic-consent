package com.school.econsent.repo;

import com.school.econsent.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

  List<Student> findByGuardianIdOrderByFullNameAsc(Long guardianId);
}
