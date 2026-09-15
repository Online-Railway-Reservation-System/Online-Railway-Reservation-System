package com.railway.train;

import com.railway.train.dto.TrainRequest;
import com.railway.train.dto.TrainResponse;
import com.railway.train.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainServiceApplicationTests {

    @Autowired
    private TrainService trainService;

    @Test
    void testTrainCrudOperations() {
        TrainRequest req = new TrainRequest("22625", "Double Decker Express", "AC_DOUBLE_DECKER", true);
        TrainResponse created = trainService.createTrain(req);

        assertNotNull(created);
        assertEquals("22625", created.getTrainNumber());

        TrainResponse found = trainService.getTrainByNumber("22625");
        assertNotNull(found);
        assertEquals("Double Decker Express", found.getTrainName());

        trainService.deleteTrain(created.getId());
        TrainResponse deleted = trainService.getTrainById(created.getId());
        assertFalse(deleted.isActiveStatus());
    }
}