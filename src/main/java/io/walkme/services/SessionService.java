package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;
import storage.entities.Session;
import utils.HibernateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final String SESSION_TABLE_NAME = "wm_session";
    private static SessionService instance;
    private static final Logger logger = LogManager.getLogger(SessionService.class);

    /**
     * key - user id
     * value - session token
     */
    private final ConcurrentHashMap<Long, String> sessions = new ConcurrentHashMap<>();

    private SessionService() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String text;//sc.next();
                try {
                    text = reader.readLine();
                    if (text.equals("/sessions")) {
                        sessions.forEach((a, s) -> System.out.println("[" + a + "=" + s + "]"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static SessionService getInstance() {
        logger.info("Getting instance");
        logger.error("Getting instance");
        logger.warn("Getting instance");
        logger.trace("Getting instance");
        logger.debug("Getting instance");
        logger.fatal("Getting instance");
        SessionService result = instance;
        if (result == null) {
            synchronized (SessionService.class) {
                result = instance;
                if (result == null) {
                    result = new SessionService();
                    instance = result;
                }
            }
        }

        return result;
    }

    public void loadFromDatabase() {
        org.hibernate.Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query<Session> query = session.createNativeQuery(
                    "select * from " + SESSION_TABLE_NAME + ";",
                    Session.class);

            if (sessions.size() == 0) {
                query.getResultStream().forEach(s -> sessions.put(s.getUser().getId(), s.getSessionToken()));
            } else {
                throw new IllegalStateException("The sessions map must be empty for performing this operation.");
            }

            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean isSessionExist(String token) {
        return sessions.containsValue(token);
    }

    public void saveSession(Session session) {
        org.hibernate.Session hibSession = null;
        try {
            hibSession = HibernateUtil.getSession();
            hibSession.beginTransaction();

            hibSession.saveOrUpdate(session);
            hibSession.getTransaction().commit();

            sessions.put(session.getUser().getId(), session.getSessionToken());

        } finally {
            if (hibSession != null) {
                hibSession.close();
            }
        }
    }

    public void deleteSession(Long userId) {
        sessions.remove(userId);
    }

    public void clear() {
        org.hibernate.Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query<Session> query = session.createNativeQuery(
                    "delete from " + SESSION_TABLE_NAME + ";",
                    Session.class);

            query.executeUpdate();
            session.getTransaction().commit();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static void main(String[] args) {
        SessionService.getInstance();
        System.exit(0);
    }
}
