package dr.dev.scoretuneapi.user.service;

import dr.dev.scoretuneapi.user.model.User;
import dr.dev.scoretuneapi.user.persistence.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao ;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}
