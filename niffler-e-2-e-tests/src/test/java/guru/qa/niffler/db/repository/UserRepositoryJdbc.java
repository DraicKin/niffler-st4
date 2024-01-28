package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.*;
import io.qameta.allure.Allure;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

  private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH);
  private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA);

  private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  @Override
  public UserAuthEntity createInAuth(UserAuthEntity user) {
    try (Connection conn = authDs.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement userPs = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
              "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
           PreparedStatement authorityPs = conn.prepareStatement(
               "INSERT INTO \"authority\" " +
                   "(user_id, authority) " +
                   "VALUES (?, ?)")
      ) {

        userPs.setString(1, user.getUsername());
        userPs.setString(2, pe.encode(user.getPassword()));
        userPs.setBoolean(3, user.getEnabled());
        userPs.setBoolean(4, user.getAccountNonExpired());
        userPs.setBoolean(5, user.getAccountNonLocked());
        userPs.setBoolean(6, user.getCredentialsNonExpired());

        userPs.executeUpdate();

        UUID authUserId;
        try (ResultSet keys = userPs.getGeneratedKeys()) {
          if (keys.next()) {
            authUserId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }

        for (Authority authority : Authority.values()) {
          authorityPs.setObject(1, authUserId);
          authorityPs.setString(2, authority.name());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }

        authorityPs.executeBatch();
        conn.commit();
        user.setId(authUserId);
      } catch (Exception e) {
        conn.rollback();
        throw e;
      } finally {
        conn.setAutoCommit(true);
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public UserEntity createInUserdata(UserEntity user) {
    try (Connection conn = udDs.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO \"user\" " +
              "(username, currency) " +
              "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getCurrency().name());
        ps.executeUpdate();

        UUID userId;
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (keys.next()) {
            userId = UUID.fromString(keys.getString("id"));
          } else {
            throw new IllegalStateException("Can`t find id");
          }
        }
        user.setId(userId);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public void deleteInAuthById(UUID id) {

    try (Connection connection = authDs.getConnection()) {
      connection.setAutoCommit(false);
      try(
              PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM \"authority\" " +
                      "WHERE user_id = ?");
              PreparedStatement ps2 = connection.prepareStatement(
                      "DELETE FROM \"user\" " +
                              "WHERE id = ?")
      ) {
        ps.setObject(1, id, Types.OTHER);
        ps.executeUpdate();
        ps2.setObject(1,id, Types.OTHER);
        ps2.executeUpdate();
        Allure.step(ps.toString());
        connection.commit();
      } catch(Exception e) {
        connection.rollback();
        throw e;
      }
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteInUserdataById(UUID id) {
    try (Connection connection = udDs.getConnection()) {
      try(PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM \"user\" " +
                      "WHERE id = ?"
      )) {
        ps.setObject(1, id, Types.OTHER);
        ps.executeUpdate();
        Allure.step(ps.toString());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateInAuth(UserAuthEntity user) {
    try (Connection connection = authDs.getConnection()) {
      try( PreparedStatement ps = connection.prepareStatement(
                      "UPDATE \"user\" " +
                              "SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                              "account_non_locked = ?, credentials_non_expired = ? " +
                              "WHERE id = ?")
      ) {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setBoolean(3, user.getEnabled());
        ps.setBoolean(4, user.getAccountNonExpired());
        ps.setBoolean(5, user.getAccountNonLocked());
        ps.setBoolean(6, user.getCredentialsNonExpired());
        ps.setObject(7, user.getId(), Types.OTHER);
        ps.executeUpdate();
        Allure.step(ps.toString());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void updateInUserdata(UserEntity user) {
    try (Connection connection = udDs.getConnection()) {
      try( PreparedStatement ps = connection.prepareStatement(
              "UPDATE \"user\" " +
                      "SET username = ?, currency = ?, firstname = ?, surname = ?, photo = ? " +
                      "WHERE id = ?")
      ) {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getCurrency().name());
        ps.setString(3, user.getFirstname());
        ps.setString(4, user.getSurname());
        ps.setBytes(5, user.getPhoto());
        ps.setObject(6, user.getId(), Types.OTHER);
        ps.executeUpdate();
        Allure.step(ps.toString());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public UserAuthEntity readInAuthById(UUID id) {
    UserAuthEntity user = new UserAuthEntity();
    try (Connection connection = authDs.getConnection()) {
      try( PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM \"user\" " +
                      "WHERE id = ?");
           PreparedStatement psAuth = connection.prepareStatement(
                   "SELECT * FROM \"authority\" " +
                   "WHERE user_id = ?")
      ) {
        ps.setObject(1, id, Types.OTHER);
        ps.execute();
        try (ResultSet sqlResult = ps.getResultSet()) {
          if (sqlResult.next()) {
            user.setId(sqlResult.getObject("id", UUID.class));
            user.setUsername(sqlResult.getString("username"));
            user.setPassword(sqlResult.getString("password"));
            user.setEnabled(sqlResult.getBoolean("enabled"));
            user.setAccountNonLocked(sqlResult.getBoolean("account_non_locked"));
            user.setAccountNonExpired(sqlResult.getBoolean("account_non_expired"));
            user.setCredentialsNonExpired(sqlResult.getBoolean("credentials_non_expired"));
          } else {
            throw new IllegalStateException("Can`t find user");
          }
        }

        psAuth.setObject(1, id, Types.OTHER);
        psAuth.execute();

        try (ResultSet sqlResult = psAuth.getResultSet()) {
          ArrayList<AuthorityEntity> authorities = new ArrayList<>();
          while (sqlResult.next()) {
            AuthorityEntity ae = new AuthorityEntity();
            ae.setId(sqlResult.getObject("id", UUID.class));
            ae.setAuthority(Authority.valueOf(sqlResult.getString("authority")));
            authorities.add(ae);
          }
          user.setAuthorities(authorities);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }

  @Override
  public UserEntity readInUserdataById(UUID id) {
    UserEntity user = new UserEntity();
    try (Connection connection = udDs.getConnection()) {
      try( PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM \"user\" " +
                      "WHERE id = ?")
      ) {
        ps.setObject(1, id, Types.OTHER);
        ps.execute();
        try (ResultSet sqlResult = ps.getResultSet()) {
          if (sqlResult.next()) {
            user.setId(sqlResult.getObject("id", UUID.class));
            user.setUsername(sqlResult.getString("username"));
            user.setCurrency(CurrencyValues.valueOf(sqlResult.getString("currency")));
            user.setFirstname(sqlResult.getString("firstname"));
            user.setSurname(sqlResult.getString("surname"));
            user.setPhoto(sqlResult.getBytes("photo"));
          } else {
            throw new IllegalStateException("Can`t find user");
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }
}
