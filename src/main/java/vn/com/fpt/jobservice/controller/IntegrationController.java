package vn.com.fpt.jobservice.controller;

import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationListResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.fpt.jobservice.entity.StepHistory;
import vn.com.fpt.jobservice.model.StepHistoryModel;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.repositories.StepHistoryRepository;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integrations")
public class IntegrationController {
    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Autowired
    StepHistoryRepository stepHistoryRepository;

    @GetMapping("testtest")
    @Transactional
    public List<StepHistoryModel> GetTestTest() {
        List<StepHistory> stepHistories =  stepHistoryRepository.findByTaskHistoryIdOrderByStepDesc(1L);
        return stepHistories.stream()
                .map(StepHistory::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping()
    public GetIntegrationListResult getIntegrationGRPC() {
        return integrationServiceGrpc.getListIntegration();
    }

    @GetMapping("/execute/{id}")
    public ExecuteIntegrationResult executeIntegrationGRPC(@PathVariable("id") Long id) {
        GetIntegrationResult integrationData = integrationServiceGrpc.getIntegrationById(id);
        String structureStr = integrationData.getStructure();
        IntegrationStructure structure = Utils.stringToObject(structureStr, IntegrationStructure.class);

        return integrationServiceGrpc.executeIntegration(structure);
    }
}
