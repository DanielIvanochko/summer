package summer.core.utils;

import lombok.extern.slf4j.Slf4j;
import summer.core.context.annotation.Value;
import summer.core.context.exception.SummerException;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ValuePropertiesResolver {
  private static final Pattern VALUE_PROPERTY_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
  private static final String DEFAULT_VALUE_SEPARATOR = ":";
  public static Object resolveValueForParameter(Parameter parameter, Value valueAnnotation, Properties properties) {
    return resolveValueProperty(parameter.getType(), valueAnnotation, properties);
  }

  public static Object resolveValueForField(Field field, Value valueAnnotation, Properties properties) {
    return resolveValueProperty(field.getType(), valueAnnotation, properties);
  }

  private static Object resolveValueProperty(Class<?> type, Value valueAnnotation, Properties properties) {
    String value = valueAnnotation.value();
    Matcher matcher = VALUE_PROPERTY_PATTERN.matcher(value);

    if (matcher.find()) {
      String key = matcher.group(1);
      String propertyValue = properties.getProperty(key);
      if (containsDefaultValue(key) && propertyValue == null) {
        String resolvedProperty = resolveDefaultValue(key);
        properties.put(key, resolvedProperty);
      } else {
        checkProperty(key, propertyValue);
      }

      return cast(properties.get(key), type);
    } else {
      return cast(value, type);
    }
  }

  private static String resolveDefaultValue(String key) {
    String[] separatedValues = key.split(DEFAULT_VALUE_SEPARATOR);
    if(separatedValues.length > 1) {
      return separatedValues[1];
    } else {
      return "";
    }
  }

  private static boolean containsDefaultValue(String key) {
    return key.contains(DEFAULT_VALUE_SEPARATOR);
  }

  private static void checkProperty(String key, String propertyValue) {
    if (propertyValue == null) {
      throw new SummerException("Couldn't find the property: " + key);
    }
  }

  private static Object cast(Object value, Class<?> type) {
    if (value == null || type == null) {
      return null;
    }

    try {
      if (type.equals(Integer.class) || type.equals(int.class)) {
        return Integer.parseInt(value.toString());
      } else if (type.equals(Double.class) || type.equals(double.class)) {
        return Double.parseDouble(value.toString());
      } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
        return Boolean.parseBoolean(value.toString());
      } else if (type.equals(Byte.class) || type.equals(byte.class)) {
        return Byte.parseByte(value.toString());
      } else if (type.equals(Short.class) || type.equals(short.class)) {
        return Short.parseShort(value.toString());
      } else if (type.equals(Long.class) || type.equals(long.class)) {
        return Long.parseLong(value.toString());
      } else if (type.equals(Float.class) || type.equals(float.class)) {
        return Float.parseFloat(value.toString());
      } else if (type.equals(Character.class) || type.equals(char.class)) {
        String stringValue = value.toString();
        if (!stringValue.isEmpty()) {
          return stringValue.charAt(0);
        }
        return null;
      }
    } catch (Exception exe) {
      log.warn("Cannot cast value {} to type {} message {}", value, type, exe.getMessage());
      return null;
    }

    return value;
  }

}
