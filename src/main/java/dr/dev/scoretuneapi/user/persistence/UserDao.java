package dr.dev.scoretuneapi.user.persistence;

import dr.dev.scoretuneapi.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findByEmail(String email) ;
    User save(User user) ;
    List<User> findAll() ;
}
