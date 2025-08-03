package dr.dev.scoretuneapi.user.persistence;

import dr.dev.scoretuneapi.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDaoImpl extends UserDao, JpaRepository<User, Long> {

    @Override
    Optional<User> findByEmail(String email);
}
