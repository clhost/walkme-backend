package io.walkme.services.fields;

import io.walkme.storage.entities.Place;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Depends on {@link Place}
 * Ignore: location, scheduleAsJsonString, schedule
 */
public class PlaceFields {
    public static final String ID = "id";
    public static final String NAME = "place_name";
    public static final String CATEGORY_ID = "category_id";
    public static final String GIS_CATEGORY = "gis_category";
    public static final String ADDRESS_NAME = "address_name";
    public static final String ADDRESS_COMMENT = "address_comment";

    static {
        Class c = Place.class;

        Field[] placeFields = c.getDeclaredFields();
        Field[] thisFields = PlaceFields.class.getDeclaredFields();

        List<String> placeFieldsList = new ArrayList<>();
        List<String> thisFieldsList = new ArrayList<>();

        for (Field field : thisFields) {
            field.setAccessible(true);
            try {
                thisFieldsList.add((String) field.get(PlaceFields.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Field field : placeFields) {
            if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Embedded.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);

                if (!column.name().equals("json_schedule")) {
                    placeFieldsList.add(column.name());
                }
            }

            if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn column = field.getAnnotation(JoinColumn.class);
                placeFieldsList.add(column.name());
            }
        }

        System.out.println(placeFieldsList);
        System.out.println(thisFieldsList);
        if (!placeFieldsList.equals(thisFieldsList)) {
            throw new IllegalStateException("PlaceFields.class fields are not equal to fields which exists in " +
                    "database.");
        }
    }
}
