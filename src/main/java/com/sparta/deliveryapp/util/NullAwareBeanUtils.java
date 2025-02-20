package com.sparta.deliveryapp.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import java.lang.reflect.InvocationTargetException;

public class NullAwareBeanUtils {

  public static void copyNonNullProperties(Object source, Object target) {
    try {
      // BeanUtilsBean을 통해 복사
      BeanUtilsBean beanUtilsBean = new BeanUtilsBean();

      // source 객체의 모든 속성 가져오기
      org.apache.commons.beanutils.PropertyUtils.describe(source).forEach((propertyName, propertyValue) -> {
        try {
          // password 필드는 제외하고 복사
          if ("password".equals(propertyName)) {
            return; // password는 복사하지 않음
          }

          // null이 아닌 속성만 복사
          if (propertyValue != null) {
            beanUtilsBean.copyProperty(target, propertyName, propertyValue);
          }
        } catch (IllegalAccessException | InvocationTargetException e) {
          e.printStackTrace();
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error copying non-null properties", e);
    }
  }
}