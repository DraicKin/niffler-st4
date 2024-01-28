package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
            Pair<UserAuthEntity, UserEntity> userData = createUser(username, pass);
            extensionContext.getStore(NAMESPACE).put("userAuth", userData.getLeft());
            extensionContext.getStore(NAMESPACE).put("user", userData.getRight());
        }

    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        UserAuthEntity userAuth = extensionContext.getStore(NAMESPACE).get("userAuth", UserAuthEntity.class);
        UserEntity user = extensionContext.getStore(NAMESPACE).get("user", UserEntity.class);
        userRepository.deleteInAuthById(userAuth.getId());
        userRepository.deleteInUserdataById(user.getId());
    }
    private Pair<UserAuthEntity, UserEntity> createUser(String username, String password) {
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
        return Pair.of(userAuth, user);
    }

}
