package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static guru.qa.niffler.jupiter.User.UserType.*;

public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  private static Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();

  static {
    Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> invitationSentQueue = new ConcurrentLinkedQueue<>();
    Queue<UserJson> invitationRecievedQueue = new ConcurrentLinkedQueue<>();
    friendsQueue.add(user("dima", "12345", WITH_FRIENDS));
    friendsQueue.add(user("duck", "12345", WITH_FRIENDS));
    commonQueue.add(user("bee", "12345", COMMON));
    commonQueue.add(user("barsik", "12345", COMMON));
    invitationSentQueue.add(user("Rabbit", "12345", INVITATION_SENT));
    invitationSentQueue.add(user("bobby", "12345", INVITATION_SENT));
    invitationRecievedQueue.add(user("helicopter", "12345", INVITATION_RECIEVED));
    invitationRecievedQueue.add(user("tony", "12345", INVITATION_RECIEVED));
    users.put(WITH_FRIENDS, friendsQueue);
    users.put(COMMON, commonQueue);
    users.put(INVITATION_SENT, invitationSentQueue);
    users.put(INVITATION_RECIEVED, invitationRecievedQueue);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    Parameter[] parameters = context.getRequiredTestMethod().getParameters();

    boolean isStored = storeTestCandidate(context, parameters);

    if(!isStored) {
      List<Method> beforeEachMethods = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
              .filter(m -> m.isAnnotationPresent(BeforeEach.class))
              .collect(Collectors.toList());
      beforeEachMethods.forEach( m ->
              {
                storeTestCandidate(context, m.getParameters());
              }
      );
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    ArrayList<UserJson> testUsers = context.getStore(NAMESPACE)
            .get(context.getUniqueId(), ArrayList.class);
    for (UserJson testUser : testUsers) {
      users.get(testUser.testData().userType()).add(testUser);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter()
        .getType()
        .isAssignableFrom(UserJson.class) &&
        parameterContext.getParameter().isAnnotationPresent(User.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    ArrayList<UserJson> testUsers = extensionContext.getStore(NAMESPACE)
            .get(extensionContext.getUniqueId(), ArrayList.class);
    for (UserJson testUser : testUsers) {
      if (parameterContext.getParameter().getAnnotation(User.class).value() == testUser.testData().userType()) {
        return testUser;
      }
    }

    return null;
  }

  private static UserJson user(String username, String password, User.UserType userType) {
    return new UserJson(
        null,
        username,
        null,
        null,
        CurrencyValues.RUB,
        null,
        null,
        new TestData(
            password,
            userType
        )
    );
  }

  private static boolean storeTestCandidate(ExtensionContext context, Parameter[] parameters) {
    ArrayList<UserJson> testCandidates = new ArrayList<>();
    for (Parameter parameter : parameters) {
      User annotation = parameter.getAnnotation(User.class);
      if (annotation != null && parameter.getType().isAssignableFrom(UserJson.class)) {
        UserJson testCandidate = null;
        Queue<UserJson> queue = users.get(annotation.value());
        while (testCandidate == null) {
          testCandidate = queue.poll();
        }
        testCandidates.add(testCandidate);
      }
    }
    if (testCandidates.size() > 0) {
      context.getStore(NAMESPACE).put(context.getUniqueId(), testCandidates);
      return true;
    } else {
      return false;
    }

  }
}
