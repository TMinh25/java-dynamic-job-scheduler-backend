package vn.com.fpt.jobservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
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
        return Date.from(Instant
                .ofEpochSecond(ts.getSeconds(), ts.getNanos()));
    }

    public static Timestamp convertDate2ProtocTimestamp(Date date) {
        long millis = date.getTime();
        long seconds = millis / 1000;
        int nanos = (int) ((millis % 1000) * 1_000_000);

        return Timestamp.newBuilder()
                .setSeconds(seconds)
                .setNanos(nanos)
                .build();
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
}
