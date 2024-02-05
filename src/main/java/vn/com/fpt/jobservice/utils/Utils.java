package vn.com.fpt.jobservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.time.Instant;
import java.util.*;

@Slf4j
public class Utils {

    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            // Check if the property is null
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static OriginalAndUpdatedData getOriginalAndUpdatedData(Object src, Object dst) {
        Map<String, Object> oldData = new HashMap<>();
        Map<String, Object> newData = new HashMap<>();
        BeanWrapper wrappedSrc = new BeanWrapperImpl(src);
        BeanWrapper wrappedDst = new BeanWrapperImpl(dst);
        for (PropertyDescriptor propertyDescriptor : wrappedSrc.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            if ("class".equalsIgnoreCase(propertyName))
                continue;

            Object srcValue = wrappedSrc.getPropertyValue(propertyName);
            Object dstValue = wrappedDst.getPropertyValue(propertyName);

            if (srcValue != null && dstValue != null && !srcValue.equals(dstValue)) {
                oldData.put(propertyName, srcValue);
                newData.put(propertyName, dstValue);
            }
        }
        return new OriginalAndUpdatedData(oldData, newData);
    }

    public static Date convertProtocTimestamp2Date(Timestamp ts) {
        if (ts.getSeconds() != 0 && ts.getNanos() != 0) {
            return Date.from(Instant
                    .ofEpochSecond(ts.getSeconds(), ts.getNanos()));
        }
        return null;
    }

    public static Timestamp convertDate2ProtocTimestamp(Date date) {
        if (date != null) {
            long millis = date.getTime();
            long seconds = millis / 1000;
            int nanos = (int) ((millis % 1000) * 1_000_000);

            return Timestamp.newBuilder()
                    .setSeconds(seconds)
                    .setNanos(nanos)
                    .build();
        }
        return Timestamp.newBuilder().setSeconds(0).setNanos(0).build();
    }

    public static <T> List<T> convertRepeatedAny2List(List<Any> inputList) {
        try {
            List<T> outputList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (Any any : inputList) {
                try {
                    T convertedObject = objectMapper.readValue(any.getValue().toStringUtf8(), new TypeReference<T>() {});
                    outputList.add(convertedObject);
                } catch (Exception e) {
                    log.error("Error converting taskInputData: " + e.getMessage());
                }
            }
            return outputList;
        } catch (Exception e) {
            log.error(String.format("Can not convert taskInputData to List<%s>: %s",
                    new TypeReference<T>() {}.getClass().getName(),
                    e.getMessage()));
        }
        return new ArrayList<T>();
    }

    public static Map<String, Object> remapKeys(Map<String, Object> input, Map<String, String> newKeys) {
        Map<String, Object> output = new HashMap<>();

        for (Map.Entry<String, String> entry : newKeys.entrySet()) {
            String oldKey = entry.getKey();
            String newKey = entry.getValue();

            if (oldKey.contains(".")) {
                Object nestedData = getNestedData(input, oldKey);
                output.put(newKey, nestedData);
            } else if (newKeys.containsKey(oldKey)) {
                output.put(newKey, input.get(oldKey));
            }
        }

        return output;
    }

    private static Object getNestedData(Map<String, Object> input, String key) {
        try {

            String[] nestedKeys = key.split("\\.");
            Map<String, Object> nestedData = input;

            for (int i = 0; i < nestedKeys.length - 1; i++) {
                String nestedKey = nestedKeys[i];
                nestedData = (Map<String, Object>) nestedData.get(nestedKey);
            }

            String lastKey = nestedKeys[nestedKeys.length - 1];
            return nestedData.get(lastKey);
        } catch (Exception e) {
            return null;
        }
    }


    public static String objectToString(Object obj) {
        String res = null;
        try {
            res =  mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("objectToString failed :" + obj, e);
        }
        return res;
    }

    public static <T> T stringToObject(String str, Class<T> type) {
        try {
            return mapper.readValue(str, type);
        } catch (Exception e) {
            log.error("stringToObject failed :" + str, e);
        }
        return null;
    }
}
