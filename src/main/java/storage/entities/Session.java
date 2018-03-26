package storage.entities;

import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Table(name = "wm_session")
@Check(constraints = "state in ('vk', 'ok')")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_token", nullable = false)
    private String sessionToken;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "state")
    private String state;

    public Session(User user, String sessionToken, String accessToken, String state) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.accessToken = accessToken;
        this.state = state;
    }

    public Session() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Session)) {
            return false;
        }

        Session session = (Session) o;

        if (id != session.id) {
            return false;
        }

        if (user != null ? !user.equals(session.user) : session.user != null) {
            return false;
        }

        if (sessionToken != null ? !sessionToken.equals(session.sessionToken) : session.sessionToken != null) {
            return false;
        }

        if (accessToken != null ? !accessToken.equals(session.accessToken) : session.accessToken != null) {
            return false;
        }

        return state != null ? state.equals(session.state) : session.state == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));

        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (sessionToken != null ? sessionToken.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", sessionToken='" + sessionToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
