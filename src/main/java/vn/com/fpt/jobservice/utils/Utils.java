package vn.com.fpt.jobservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import vn.com.fpt.jobservice.model.MappingModel;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Utils {

    private static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            // Check if the property is null
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
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
            if ("class".equalsIgnoreCase(propertyName)) continue;

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
            return Date.from(Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()));
        }
        return null;
    }

    public static Timestamp convertDate2ProtocTimestamp(Date date) {
        if (date != null) {
            long millis = date.getTime();
            long seconds = millis / 1000;
            int nanos = (int) ((millis % 1000) * 1_000_000);

            return Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build();
        }
        return Timestamp.newBuilder().setSeconds(0).setNanos(0).build();
    }

    public static <T> List<T> convertRepeatedAny2List(List<Any> inputList) {
        try {
            List<T> outputList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (Any any : inputList) {
                try {
                    T convertedObject = objectMapper.readValue(any.getValue().toStringUtf8(), new TypeReference<T>() {
                    });
                    outputList.add(convertedObject);
                } catch (Exception e) {
                    log.error("Error converting taskInputData: " + e.getMessage());
                }
            }
            return outputList;
        } catch (Exception e) {
            log.error(String.format("Can not convert taskInputData to List<%s>: %s", new TypeReference<T>() {
            }.getClass().getName(), e.getMessage()));
        }
        return new ArrayList<T>();
    }

    public static String objectToString(Object obj) throws JsonProcessingException {
        String res = null;
        try {
            res = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("objectToString failed :" + obj, e);
            throw e;
        }
        return res;
    }

    public static <T> T stringToObject(String str, Class<T> type) throws JsonProcessingException {
        try {
            return mapper.readValue(str, type);
        } catch (Exception e) {
            log.error("stringToObject failed :" + str, e);
            throw e;
        }
    }

    public static Map<String, Object> mergeObjects(Map<String, Object> obj1, Map<String, Object> obj2) {
        Map<String, Object> objectMerged = new HashMap<>();

        for (String key : obj1.keySet()) {
            if (obj2.containsKey(key)) {
                Object value1 = obj1.get(key);
                Object value2 = obj2.get(key);
                if (value1 instanceof Map && value2 instanceof Map) {
                    objectMerged.put(key, mergeObjects((Map<String, Object>) value1, (Map<String, Object>) value2));
                } else if (value1 instanceof List && value2 instanceof List) {
                    ((List<Object>) value1).addAll((List<Object>) value2);
                    objectMerged.put(key, value1);
                } else {
                    objectMerged.put(key, value1);
                    objectMerged.put(key, value2);
                }
            } else {
                objectMerged.put(key, obj1.get(key));
            }
        }
        for (String key : obj2.keySet()) {
            if (!objectMerged.containsKey(key)) {
                objectMerged.put(key, obj2.get(key));
            }
        }

        return objectMerged;
    }

    public static Map<String, Object> jsonToMap(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.toMap();
    }

    public static Comparator<String> getNestedFieldComparator() {
        return Comparator.comparingInt(Utils::getDepth)
                .thenComparing(Comparator.naturalOrder());
    }

    public static int getDepth(String field) {
        // Count the number of dots to determine the depth of nesting
        return (int) field.chars().filter(ch -> ch == '.').count();
    }

    public static long calculateDateDifferenceInMillis(Date date1, Date date2) {
        return Math.abs(date1.getTime() - date2.getTime());
    }

    public static <T> List<T> convertStringToList(String jsonString, TypeReference<List<T>> valueTypeRef) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, valueTypeRef);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
