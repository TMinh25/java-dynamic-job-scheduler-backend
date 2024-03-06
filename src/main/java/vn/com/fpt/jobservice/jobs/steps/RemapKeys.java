package vn.com.fpt.jobservice.jobs.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.utils.DataMapper;
import vn.com.fpt.jobservice.utils.DataMapper.MapperObject;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.List;
import java.util.Map;

public class RemapKeys extends BaseJobStep {
    private final String valueContextName;

    public RemapKeys(BaseJob baseJob, String valueContextName) {
        super(baseJob);
        this.valueContextName = valueContextName;
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final Task task = (Task) context.get("task");
        final String valueContext = (String) context.get(this.valueContextName);

        try {
            String taskInputData = task.getTaskInputData();
            List<MapperObject> remapKeys = Utils.convertStringToList(taskInputData, new TypeReference<List<MapperObject>>() {
            });
            Map<String, Object> valuemap = Utils.stringToObject(valueContext, Map.class);
            Map<String, Object> keyMappedResult = DataMapper.remapData(valuemap, remapKeys);
            logger("keyMappedResult: " + keyMappedResult.toString());
            context.put("keyMappedResult", keyMappedResult);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
