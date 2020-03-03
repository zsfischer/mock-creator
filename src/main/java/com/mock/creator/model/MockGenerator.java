package com.mock.creator.model;

import com.github.javafaker.Faker;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class FakerGenerator
{
    private Faker faker;
    private Random random;

    public FakerGenerator()
    {
        this.faker = new Faker();
        random = new Random();
    }

    public <T> T generateFakeValues(Class<T> clazz)
    {
        Object t;
        try
        {
            t = clazz.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            System.out.println("Hiba új instance létrehozásánál!");
            return null;
        }

        Field[] fields = clazz.getDeclaredFields();
        goThroughField(fields, t, new ArrayList<>());

        Class parentClass = clazz.getSuperclass();
        while (parentClass != null)
        {
            fields = parentClass.getDeclaredFields();
            goThroughField(fields, t, new ArrayList<>());
            parentClass = parentClass.getSuperclass();
        }

        return (T) t;
    }

    public <T> T generateFakeValuesWithException(Class<T> clazz, List<String> fieldNames)
    {
        Object t;
        try
        {
            t = clazz.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            System.out.println("Error while trying to create new instance from " + clazz);
            return null;
        }

        Field[] fields = clazz.getDeclaredFields();
        goThroughField(fields, t, fieldNames);

        Class parentClass = clazz.getSuperclass();
        while (parentClass != null)
        {
            fields = parentClass.getDeclaredFields();
            goThroughField(fields, t, fieldNames);
            parentClass = parentClass.getSuperclass();
        }

        return (T) t;
    }

    private void goThroughField(Field[] fields, Object t, List<String> fieldNames)
    {
        for (Field field : fields)
        {
            ReflectionUtils.makeAccessible(field);
            if (fieldNames.contains(field.getName()))
            {
                ReflectionUtils.setField(field, t, null);
            }
            else
            {
                ReflectionUtils.setField(field, t, createObject(field));
            }
        }
    }

    private Object createObject(Field field)
    {
        Class<?> fieldType = field.getType();
        int rnd;
        Class genericType;
        if (fieldType.isEnum())
        {
            return fieldType.getEnumConstants()[this.faker.number()
                    .numberBetween(0, fieldType.getEnumConstants().length - 1)];
        }
        else if (fieldType.isAssignableFrom(List.class))
        {
            List<Object> list = new ArrayList();
            rnd = this.faker.number().numberBetween(1, 8);
            genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            for (int i = 0; i < rnd; ++i)
            {
                list.add(createObjectFromType(genericType));
            }
            return list;
        }
        else if (fieldType.isAssignableFrom(Set.class))
        {
            Set<Object> set = new HashSet<>();
            rnd = this.faker.number().numberBetween(2, 10);
            genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            for (int i = 0; i < rnd; ++i)
            {
                set.add(createObjectFromType(genericType));
            }
            return set;
        }
        else if (fieldType.isAssignableFrom(Map.class))
        {
            Map<Object, Object> map = new HashMap<>();
            rnd = this.faker.number().numberBetween(2, 12);
            Class key = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            Class value = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];

            for (int i = 0; i < rnd; ++i)
            {
                map.put(createObjectFromType(key), createObjectFromType(value));
            }
            return map;
        }
        else
        {
            return createObjectFromType(fieldType);
        }
    }

    private Object createObjectFromType(Class<?> clazz)
    {
        if (clazz.equals(String.class))
        {
            return faker.lorem().characters(10);
        }
        else if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE))
        {
            return faker.number().numberBetween(0, 100000);
        }
        else if (clazz.equals(Long.class) || clazz.equals(Long.TYPE))
        {
            return faker.number().randomNumber();
        }
        else if (clazz.equals(Date.class))
        {
            return faker.date().birthday();
        }
        else if (clazz.equals(LocalDate.class))
        {
            return faker.date().birthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        else if (clazz.equals(LocalTime.class))
        {
            return faker.date().birthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();
        }
        else if (clazz.equals(LocalDateTime.class))
        {
            return faker.date().birthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        else if (clazz.equals(Instant.class))
        {
            return faker.date().birthday().toInstant();
        }
        else if (clazz.equals(Byte.class) || clazz.equals(Byte.TYPE))
        {
            return ((Integer) faker.number().numberBetween(0, 10000)).byteValue();
        }
        else if (clazz.equals(Short.class) || clazz.equals(Short.TYPE))
        {
            return (short) faker.number().numberBetween(0, 1000);
        }
        else if (clazz.equals(Character.class) || clazz.equals(Character.TYPE))
        {
            return faker.lorem().character();
        }
        else if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE))
        {
            return random.nextBoolean();
        }
        else
        {
            return null;
        }
    }


}
