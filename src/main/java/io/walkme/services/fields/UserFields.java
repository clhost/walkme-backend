package services.fields;

import storage.entities.User;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Depends on {@link User}
 */
public class UserFields {
    public static final String ID = "id";
    public static final String SALT = "salt";
    public static final String FIRST_NAME = "f_name";
    public static final String LAST_NAME = "l_name";
    public static final String SOCIAL_ID = "social_id";
    public static final String AVATAR = "avatar";

    static {
        Class c = User.class;

        Field[] userFields = c.getDeclaredFields();
        Field[] thisFields= UserFields.class.getDeclaredFields();

        List<String> userFieldsList = new ArrayList<>();
        List<String> thisFieldsList = new ArrayList<>();

        for (Field field : thisFields) {
            field.setAccessible(true);
            try {
                thisFieldsList.add((String) field.get(UserFields.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Field field : userFields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                userFieldsList.add(column.name());
            }
        }

        if (!userFieldsList.equals(thisFieldsList)) {
            throw new IllegalStateException("UserFields.class fields are not equal to fields which exists in database" +
                    ".");
        }
    }
}
