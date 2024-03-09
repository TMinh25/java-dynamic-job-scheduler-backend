package vn.com.fpt.jobservice.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DataMapper {
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MapperObject {
        private String to;
        private String from;
        private Boolean required;
        private String defaultValue;
        private Boolean isInserted = false;

        public MapperObject() {
            // Default constructor for Jackson
        }

        public MapperObject(String to, String from, Boolean required, String defaultValue) {
            this.to = to;
            this.from = from;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }

    private static Boolean isArrayKey(String key) {
        return key.startsWith("[") && key.endsWith("]");
    }

    private static Boolean isArrayPath(String path) {
        return path.contains("[") && path.contains("]");
    }

    public static Map<String, Object> remapData(List<Map<String, Object>> input, List<MapperObject> remapKeys) {
        List<MapperObject> listRemapKeys = new ArrayList<>();
        for (int i = 0; i < remapKeys.size(); ) {
            MapperObject mapperObject = remapKeys.get(i);
            String fromKey = mapperObject.getFrom();
            String toKey = mapperObject.getTo();
            if (fromKey == null && isArrayPath(toKey)) {
                remapKeys.remove(i);
                listRemapKeys.add(mapperObject);
            } else if (fromKey != null && fromKey.startsWith("[].")) {
                remapKeys.remove(i);
                mapperObject.setFrom(fromKey.substring(3));
                listRemapKeys.add(mapperObject);
            } else {
                i++;
            }
        }

        List<Map<String, Object>> listKeyMapped = new ArrayList<>();
        for (Map<String, Object> valueRecord : input) {
            Map<String, Object> mappedRecord = DataMapper.remapData(valueRecord, listRemapKeys);
            listKeyMapped.add(mappedRecord);
        }

        Map<String, Object> keyMappedResult = DataMapper.remapData(new HashMap<>(), remapKeys);

        Map<String, Object> arrayMerged = Utils.mergeObjects(listKeyMapped);
        keyMappedResult = Utils.mergeObjects(keyMappedResult, arrayMerged);

        return keyMappedResult;
    }

    public static Map<String, Object> remapData(Map<String, Object> input, List<MapperObject> remapKeys) {
        Map<String, Object> output = new HashMap<>();
        JSONArray currentArray = null;

        for (int i = 0; i < remapKeys.size(); i++) {
            MapperObject mapperObject = remapKeys.get(i);

            String fromKey = mapperObject.getFrom();
            String toKey = mapperObject.getTo();
            Boolean isRequired = mapperObject.getRequired();
            String defaultValue = mapperObject.getDefaultValue();

            if (fromKey == null) {
                if (defaultValue != null) {
                    JSONObject jsonObject = getToValue(toKey, new JSONObject(output), defaultValue);
                    Map<String, Object> objectMap = Utils.jsonToMap(jsonObject.toString());
                    output.clear();
                    output.putAll(objectMap);
                } else {
                    if (isRequired) {
                        throw new NullPointerException(String.format("Data field is required: %S", toKey));
                    }
                }
                continue;
            }
            boolean isArrayFromKey = isArrayPath(fromKey);
            boolean isNestedFromKey = fromKey.contains(".") || isArrayFromKey;
            boolean isArrayToKey = isArrayPath(toKey);
            boolean isNestedToKey = toKey.contains(".") || isArrayToKey;

            if (isNestedToKey) {
                JSONObject jsonObject;
                if (isNestedFromKey) {
                    Object nestedData = getNestedData(input, fromKey);
                    jsonObject = getToValue(toKey, new JSONObject(output), nestedData);
                } else {
                    if (isArrayToKey && !mapperObject.getIsInserted()) {
                        mapperObject.setIsInserted(true);
                        remapKeys.add(mapperObject);
                        continue;
                    } else {
                        jsonObject = getToValue(toKey, new JSONObject(output), input.get(fromKey));
                    }
                }

                Map<String, Object> objectMap = Utils.jsonToMap(jsonObject.toString());
                output.clear();
                output.putAll(objectMap);
            } else if (isNestedFromKey) {
                Object nestedData = getNestedData(input, fromKey);
                output.put(toKey, nestedData);
            } else {
                output.put(toKey, input.get(fromKey));
            }
        }

        return output;
    }


    public static Object getNestedData(Map<String, Object> input, String path) {
        try {
            if (isArrayPath(path)) {
                return getFromValue(new JSONObject(input), path);
            }

            String[] nestedKeys = path.split("\\.");
            Map<String, Object> nestedData = input;

            for (int i = 0; i < nestedKeys.length - 1; i++) {
                String nestedKey = nestedKeys[i];
                nestedData = (Map<String, Object>) nestedData.get(nestedKey);
            }

            String lastKey = nestedKeys[nestedKeys.length - 1];
            return nestedData.get(lastKey);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static Object getFromValue(JSONObject input, String fromKey) {
        String[] keyArray = fromKey.split("\\.");
        JSONObject currentData = input;
        JSONArray currentArray = null;

        try {
            String lastKey = keyArray[keyArray.length - 1];

            for (String key : keyArray) {
                if (!key.equalsIgnoreCase(lastKey)) {
                    if (isArrayKey(key)) {
                        String arrayKey = key.substring(1, key.length() - 1);
                        currentArray = currentData.getJSONArray(arrayKey);
                    } else {
                        currentData = currentData.getJSONObject(key);
                    }
                }
            }

            if (isArrayKey(lastKey)) {
                lastKey = lastKey.substring(1, lastKey.length() - 1);
                return currentData.getJSONArray(lastKey);
            } else if (currentArray != null) {
                JSONArray outputArray = new JSONArray();
                for (Object object : currentArray) {
                    JSONObject convertedObject = (JSONObject) object;
                    outputArray.put(convertedObject.get(lastKey));
                }
                return outputArray;
            } else {
                return currentData.getJSONObject(lastKey);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static JSONObject getToValue(String input, JSONObject output, Object value) {
        JSONObject currentObject = output;
        JSONArray currentArray = new JSONArray();
        String[] keyParts = input.split("\\.");

        for (int i = 0; i < keyParts.length; i++) {
            String keyPart = keyParts[i];
            boolean isLastPart = i == keyParts.length - 1;

            if (!isArrayKey(keyPart)) {
                if (!currentArray.isEmpty()) {
                    if (isLastPart) {
                        if (value instanceof JSONArray) {
                            for (int innerI = 0; innerI < ((JSONArray) value).length(); innerI++) {
                                String valueObject = String.valueOf(((JSONArray) value).get(innerI));
                                JSONObject tempObject = currentArray.getJSONObject(innerI);
                                tempObject.put(keyPart, valueObject);
                                currentArray.put(innerI, tempObject);
                            }
                        } else {
                            for (int innerI = 0; innerI < currentArray.length(); innerI++) {
                                JSONObject tempObject = currentArray.getJSONObject(innerI);
                                tempObject.put(keyPart, value);
                                currentArray.put(innerI, tempObject);
                            }
                        }
                    } else {
                        if (currentObject.has(keyPart) && currentObject.get(keyPart) instanceof JSONArray) {
                            currentArray = currentObject.getJSONArray(keyPart);
                        } else {
                            JSONObject tempObject = new JSONObject();
                            JSONObject innerObject = new JSONObject();
                            innerObject.put(keyPart, tempObject);
                            currentArray.clear();
                            for (Object v : (JSONArray) value) {
                                currentArray.put(innerObject);
                            }
                        }
                    }
                } else {
                    if (isLastPart) {
                        currentObject.put(keyPart, value);
                    } else {
                        if (currentObject.has(keyPart)) {
                            currentObject = currentObject.getJSONObject(keyPart);
                        } else {
                            JSONObject tempObject = new JSONObject();
                            currentObject.put(keyPart, tempObject);
                            currentObject = tempObject;
                        }
                    }
                }
            } else {
                keyPart = keyPart.substring(1, keyPart.length() - 1);

                if (value instanceof JSONArray) {
                    if (isLastPart) {
                        currentObject.put(keyPart, value);
                    } else {
                        if (currentObject.has(keyPart) && currentObject.get(keyPart) instanceof JSONArray) {
                            currentArray = currentObject.getJSONArray(keyPart);
                        } else {
                            for (Object v : (JSONArray) value) {
                                JSONObject innerObject = new JSONObject();
                                currentArray.put(innerObject);
                            }
                            currentObject.put(keyPart, currentArray);
                        }
                    }
                } else {
                    if (currentObject.has(keyPart) && currentObject.get(keyPart) instanceof JSONArray) {
                        currentArray = currentObject.getJSONArray(keyPart);
                    } else {
                        JSONObject tempJsonObject = new JSONObject();
                        JSONArray tempJsonArray = new JSONArray();
                        tempJsonArray.put(tempJsonObject);
                        currentArray = tempJsonArray;
                        currentObject.put(keyPart, tempJsonArray);
                    }
                }
            }
        }
        return output;
    }
}
