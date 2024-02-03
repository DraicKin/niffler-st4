package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.db.repository.UserRepositorySJdbc;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class UserRepositoryExtension implements TestInstancePostProcessor {
  @Override
  public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
    for (Field field : o.getClass().getDeclaredFields()) {
      if (field.getType().isAssignableFrom(UserRepository.class)) {
        field.setAccessible(true);
        String repo = extensionContext.getConfigurationParameter("repository").orElse("jdbc");
        switch (repo) {
          case "jdbc" -> field.set(o, new UserRepositoryJdbc());
          case "sjdbc" -> field.set(o, new UserRepositorySJdbc());
          case "hibernate" -> field.set(o, new UserRepositoryHibernate());
          default -> throw new RuntimeException("Parameter repository is incorrect");
        }
      }
    }
  }
}
