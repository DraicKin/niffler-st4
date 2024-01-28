package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserRepository {

  UserAuthEntity createInAuth(UserAuthEntity user);

  UserEntity createInUserdata(UserEntity user);

  void deleteInAuthById(UUID id);

  void deleteInUserdataById(UUID id);

  void updateInAuth(UserAuthEntity user);

  void updateInUserdata(UserEntity user);

  UserAuthEntity readInAuthById(UUID id);

  UserEntity readInUserdataById(UUID id);

}
