package storage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.GenericEntityService;
import services.UserService;
import services.fields.UserFields;
import storage.entities.User;
import utils.HibernateUtil;
import utils.SHA256BASE64Encoder;

import java.util.Random;

public class UserSaveTest {
    private static final GenericEntityService<User, String> service = new UserService();
    private User user;

    @Before
    public void init() {
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
    }

    @After
    public void delete() throws Exception {
        service.delete(String.valueOf(user.getSocialId()), UserFields.SOCIAL_ID);
        HibernateUtil.shutdown();
    }

    @Test
    public void saveFullUser() throws Exception {
        user = new User();
        String salt = SHA256BASE64Encoder.salt();

        user.setFirstName("Jessie");
        user.setLastName("Pinkman");
        user.setSocialId(new Random().nextLong());
        user.setSalt(salt);
        user.setAvatar("/pic/empty.jpg");

        service.save(user);

        User newUser = service.get(String.valueOf(user.getSocialId()), UserFields.SOCIAL_ID);

        System.out.println(user);
        System.out.println(newUser);

        Assert.assertEquals(user, newUser);
    }
}
