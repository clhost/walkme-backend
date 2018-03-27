package services;

import org.hibernate.query.NativeQuery;
import services.fields.UserFields;
import storage.entities.User;
import org.hibernate.Session;
import utils.HibernateUtil;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * Native sql is used for better performance
 */
public class UserService implements GenericEntityService<User, String> {
    private static final String USER_TABLE_NAME = "wm_user";

    /**
     * return null, if user doesn't exists
     */
    @Override
    public User get(String val, String column) throws Exception {
        User user;
        Session session = null;
        NativeQuery<User> nativeQuery;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            switch (column) {
                case UserFields.ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " +
                            USER_TABLE_NAME + " where " + column + " = :uid", User.class);
                    nativeQuery.setParameter("uid", Long.parseLong(val));

                    try {
                        user = nativeQuery.getSingleResult();
                    } catch (NoResultException e) {
                        user = null;
                    }

                    break;
                case UserFields.SOCIAL_ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " +
                                    USER_TABLE_NAME + " where " + column + " = :sid", User.class);
                    nativeQuery.setParameter("sid", Long.parseLong(val));

                    try {
                        user = nativeQuery.getSingleResult();
                    } catch (NoResultException e) {
                        user = null;
                    }

                    break;
                default:
                    throw new UnsupportedOperationException("Get by " + column + " is still unsupported.");
            }

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return user;
    }

    @Override
    public List<User> getAll(List<String> e, String column) throws Exception {
        throw new UnsupportedOperationException("Get all method is still unsupported.");
    }

    @Override
    public void save(User user) throws Exception {
        Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.save(user);

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(String val, String column) throws Exception {
        Session session = null;
        NativeQuery nativeQuery;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            switch (column) {
                case UserFields.ID:
                    nativeQuery = session.createNativeQuery(
                            "delete from " + USER_TABLE_NAME +
                                    " where " + column + " = :uid");
                    nativeQuery.setParameter("uid", Long.parseLong(val));
                    nativeQuery.executeUpdate();
                    break;
                case UserFields.SOCIAL_ID:
                    nativeQuery = session.createNativeQuery(
                            "delete from " + USER_TABLE_NAME +
                                    " where " + column + " = :sid");
                    nativeQuery.setParameter("sid", Long.parseLong(val));
                    nativeQuery.executeUpdate();
                    break;
                default:
                    throw new UnsupportedOperationException("Delete by " + column + " is still unsupported.");
            }

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(User user) throws Exception {
        throw new UnsupportedOperationException("Update operation is still unsupported.");
    }
}
