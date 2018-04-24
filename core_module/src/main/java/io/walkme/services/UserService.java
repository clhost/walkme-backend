package io.walkme.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.NativeQuery;
import io.walkme.services.fields.UserFields;
import io.walkme.storage.entities.User;
import org.hibernate.Session;
import io.walkme.utils.HibernateUtil;

import javax.annotation.Nullable;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * Используется нативный SQL для большей производительности
 */
public class UserService implements EntityService<User, String, UserFields> {
    public static final String TABLE_NAME = "wm_user";
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Nullable
    @Override
    public User get(String byParameter, UserFields columnType) throws Exception {
        User user;
        Session session = null;
        NativeQuery<User> nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + columnType.getName() + " = :uid", User.class);
                    nativeQuery.setParameter("uid", Long.parseLong(byParameter));

                    try {
                        user = nativeQuery.getSingleResult();
                    } catch (NoResultException e) {
                        user = null;
                    }
                    break;
                case SOCIAL_ID:
                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + columnType.getName() + " = :sid", User.class);
                    nativeQuery.setParameter("sid", Long.parseLong(byParameter));

                    try {
                        user = nativeQuery.getSingleResult();
                    } catch (NoResultException e) {
                        user = null;
                    }
                    break;
                case TOKEN:
                    nativeQuery = session.createNativeQuery(
                            "select * from " + TABLE_NAME + " " +
                                    "where " + UserFields.ID.getName() + " = (" +
                                    "select user_id from wm_session where session_token = :tok)", User.class);
                    nativeQuery.setParameter("tok", byParameter);

                    try {
                        user = nativeQuery.getSingleResult();
                    } catch (NoResultException e) {
                        user = null;
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Get by " + columnType + " is still unsupported.");
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
    public List<User> getAll(List<String> byParametersList, UserFields columnType) throws Exception {
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
            logger.info("Saving user: " + user + ".");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(String byParameter, UserFields columnType) throws Exception {
        Session session = null;
        NativeQuery nativeQuery;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            switch (columnType) {
                case ID:
                    nativeQuery = session.createNativeQuery(
                            "delete from " + TABLE_NAME +
                                    " where " + columnType.getName() + " = :uid");
                    nativeQuery.setParameter("uid", Long.parseLong(byParameter));
                    nativeQuery.executeUpdate();
                    break;
                case SOCIAL_ID:
                    nativeQuery = session.createNativeQuery(
                            "delete from " + TABLE_NAME +
                                    " where " + columnType.getName() + " = :sid");
                    nativeQuery.setParameter("sid", Long.parseLong(byParameter));
                    nativeQuery.executeUpdate();
                    break;
                default:
                    throw new UnsupportedOperationException("Delete by " + columnType + " is still unsupported.");
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
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            session.update(user);

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
