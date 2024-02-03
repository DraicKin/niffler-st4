package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.jupiter.annotation.DbUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Optional;

public class DbUserExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback  {
    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(DbUserExtension.class);
    private UserRepository userRepository = new UserRepositoryJdbc();
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        Optional<DbUser> annotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class);
        if (annotation.isPresent()) {
            String username = annotation.get().username();
            if (username.isEmpty()) {
                username = RandomStringUtils.randomAlphabetic(8);
            }
            String pass = annotation.get().password();
            if (pass.isEmpty()) {
                pass = RandomStringUtils.randomAlphanumeric(12);
            }
            UserCredentials userData = createUser(username, pass);
            extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), userData);
        }

    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        UserCredentials userCredentials = extensionContext
                .getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), UserCredentials.class);
        userRepository.deleteInAuthById(userCredentials.getUserAuthEntity().getId());
        userRepository.deleteInUserdataById(userCredentials.getUserEntity().getId());
    }
    private UserCredentials createUser(String username, String password) {
        UserCredentials userCredentials = new UserCredentials();
        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUsername(username);
        userAuth.setPassword(password);
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);
        userAuth.setAuthorities(Arrays.stream(Authority.values())
                .map(e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(e);
                    return ae;
                }).toList()
        );

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setCurrency(CurrencyValues.RUB);
        userRepository.createInAuth(userAuth);
        userRepository.createInUserdata(user);
        userCredentials.setUserEntity(user);
        userCredentials.setUserAuthEntity(userAuth);
        return userCredentials;
    }

}
