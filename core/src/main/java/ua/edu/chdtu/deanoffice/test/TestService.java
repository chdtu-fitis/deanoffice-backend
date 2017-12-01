package ua.edu.chdtu.deanoffice.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.chdtu.deanoffice.entity.TestEntity;

@Service
public class TestService {
    private final TestEntityRepository testEntityRepository;

    @Autowired
    public TestService(TestEntityRepository testEntityRepository) {
        this.testEntityRepository = testEntityRepository;
    }

    public TestEntity addTest(String str) {
        TestEntity testEntity = new TestEntity();
//        testEntity.setCol2(str);
        return testEntityRepository.save(testEntity);
    }

   /* public TestEntity getTest() {
        //just for test purposes
//        addTest("aaa" + Math.random());
        //----------

        TestEntity first = testEntityRepository.getFirst();
        return first;
    }*/
    public TestEntity getTest() {
        //just for test purposes
//        addTest("aaa" + Math.random());
        //----------

        TestEntity first = testEntityRepository.getFirst();
        return first;
    }
}
