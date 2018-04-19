package io.walkme.services.fields;

import io.walkme.storage.entities.FavoriteRoute;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Depends on {@link FavoriteRoute}
 * Ignore: jsonRoute
 */
public class FavoriteRouteFields {
    public static final String TABLE_NAME = "wm_fav_route";
    public static final String ID = "id";

    static {
        Class c = FavoriteRoute.class;

        Field[] routeFields = c.getDeclaredFields();
        Field[] thisFields = FavoriteRouteFields.class.getDeclaredFields();

        List<String> routeFieldsList = new ArrayList<>();
        List<String> thisFieldsList = new ArrayList<>();

        for (Field field : thisFields) {
            field.setAccessible(true);
            try {
                if (!field.get(FavoriteRoute.class).equals(FavoriteRouteFields.TABLE_NAME)) {
                    thisFieldsList.add((String) field.get(PlaceFields.class));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Field field : routeFields) {
            if (field.isAnnotationPresent(Transient.class) || field.isAnnotationPresent(Embedded.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);

                if (!column.name().equals("route")) {
                    routeFieldsList.add(column.name());
                }
            }

            if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn column = field.getAnnotation(JoinColumn.class);
                routeFieldsList.add(column.name());
            }
        }

        System.out.println(routeFieldsList);
        System.out.println(thisFieldsList);
        if (!routeFieldsList.equals(thisFieldsList)) {
            throw new IllegalStateException("FavoriteRouteFields.class fields are not equal to fields which exists in" +
                    " database.");
        }
    }
}
