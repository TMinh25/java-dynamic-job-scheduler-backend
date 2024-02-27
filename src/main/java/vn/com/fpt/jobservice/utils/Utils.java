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

import java.beans.PropertyDescriptor;
import java.io.IOException;
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

    public static Map<String, Object> remapObjectByKeys(Map<String, Object> input,
                                                        List<Map<String, String>> newKeys) {
        Map<String, Object> output = new HashMap<>();

        List<Map<String, Object>> anonymousObject = new ArrayList<>();

        newKeys.forEach(it -> it.forEach((oldKey, newKey) -> {

            if (newKey.contains(".")) {

                JSONObject jsonObject = new JSONObject();

                if (it.containsKey(oldKey)) {
                     jsonObject = convertToJsonObject(newKey,
                            input.get(oldKey) == null ? "null"
                                    : input.get(oldKey).toString());
                } else if (oldKey.contains(".")){
                    Object nestedData = getNestedData(input, oldKey);
                     jsonObject = convertToJsonObject(newKey,
                            nestedData == null ? "null" : nestedData.toString());
                }

                Map<String, Object> objectMap = jsonToMap(jsonObject.toString());
                anonymousObject.add(objectMap);

            } else if (oldKey.contains(".")) {
                Object nestedData = getNestedData(input, oldKey);
                output.put(newKey, nestedData);

            } else if (it.containsKey(oldKey)) {
                output.put(newKey, input.get(oldKey));
            }

        }));

        if (!anonymousObject.isEmpty()) {
            Map<String, Object> merged = mergeObjects(anonymousObject);
            Map<String, Object> output1 = mergeObjects(output, merged);
            output.clear();
            output.putAll(output1);
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

    public static Map<String, Object> convertToMap(Object json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(Utils.objectToString(json), new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static List<Map<String, String>> convertMapKeyObjectsToMapString(List<Object> mapKeys) {
        if (mapKeys == null) return new ArrayList<>();

        List<Map<String, String>> remapKeys = new ArrayList<>();

        mapKeys.forEach(it -> {
            try {
                JSONObject mJSONObject = new JSONObject(Utils.objectToString(it));
                Map<String, String> remapKey = new HashMap<>();

                remapKey.put(mJSONObject.get("from").toString(), mJSONObject.get("to").toString());
                remapKeys.add(remapKey);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        return remapKeys;
    }

    public static long calculateDateDifferenceInMillis(Date date1, Date date2) {
        return Math.abs(date1.getTime() - date2.getTime());
    }

    public static Map<String, Object> mergeObjects(List<Map<String, Object>> objects) {
        Map<String, Object> objectMerged = new HashMap<>();

        for (Map<String, Object> obj : objects) {
            objectMerged = mergeObjects(obj, objectMerged);
        }

        return objectMerged;
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

    public static JSONObject convertToJsonObject(String input, String value) {

        JSONObject jsonObject = new JSONObject();
        JSONObject currentObject = jsonObject;

        String[] parts = input.split("\\.");

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith("[") && parts[i].endsWith("]")) {

                String key = parts[i].substring(1, parts[i].length() - 1);

                JSONArray jsonArray = new JSONArray();
                JSONObject innerObject = new JSONObject();
                jsonArray.put(innerObject);

                currentObject.put(key, jsonArray);
                currentObject = innerObject;

            } else {
                currentObject.put(parts[i], value);

                if (i < parts.length - 1) {
                    JSONObject innerObject = new JSONObject();
                    currentObject.put(parts[i], innerObject);
                    currentObject = innerObject;
                }
            }
        }

        return jsonObject;
    }

    public static Map<String, Object> jsonToMap(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.toMap();
    }
}
