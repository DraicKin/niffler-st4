package guru.qa.niffler.db.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCredentials {
    private UserEntity userEntity;
    private UserAuthEntity userAuthEntity;
}
