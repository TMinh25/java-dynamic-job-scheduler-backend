package vn.com.fpt.jobservice.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            if ("class".equalsIgnoreCase(propertyName.toLowerCase()))
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
}
