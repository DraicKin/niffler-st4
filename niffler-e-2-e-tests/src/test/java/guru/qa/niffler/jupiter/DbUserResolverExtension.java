package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserCredentials;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DbUserResolverExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(DbUserExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), UserCredentials.class)
                .getUserAuthEntity();
    }
}
