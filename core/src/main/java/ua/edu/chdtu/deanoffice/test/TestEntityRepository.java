package ua.edu.chdtu.deanoffice.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.edu.chdtu.deanoffice.entity.TestEntity;

public interface TestEntityRepository extends JpaRepository<TestEntity, Integer> {
    @Query(value = "select e.* from Test e order by ID desc limit 1", nativeQuery = true)
    TestEntity getFirst();
}
