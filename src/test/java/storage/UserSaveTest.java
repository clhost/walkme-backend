package storage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.GenericEntityService;
import services.UserService;
import storage.entities.User;
import utils.HibernateUtil;
import utils.SHA256BASE64Encoder;

import java.util.Random;

public class UserSaveTest {
    private static final SHA256BASE64Encoder encoder = new SHA256BASE64Encoder();
    private static final GenericEntityService<User> service = new UserService();
    private User user;

    @Before
    public void init() {
        HibernateUtil.getSession();
    }

    @After
    public void delete() throws Exception {
        service.delete(user.getId());
        HibernateUtil.shutdown();
    }

    @Test
    public void saveFullUser() throws Exception {
        user = new User();
        String salt = SHA256BASE64Encoder.salt();
        String authToken = encoder.encode(salt + System.currentTimeMillis());

        user.setFirstName("Jessie");
        user.setLastName("Pinkman");
        user.setSocialId(new Random().nextLong() + 100000L);
        user.setAuthToken(authToken);
        user.setSalt(salt);
        user.setAvatar("/pic/empty.jpg");

        service.save(user);

        User newUser = service.get(authToken);

        System.out.println(user);
        System.out.println(newUser);

        Assert.assertEquals(user, newUser);
    }
}
