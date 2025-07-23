package dr.dev.scoretuneapi.user.persistence;

import dr.dev.scoretuneapi.user.model.User;

import java.util.Optional;

public interface UserDao {

    Optional<User> findByEmail(String email) ;
}
