package dr.dev.scoretuneapi.user.service;

import dr.dev.scoretuneapi.user.model.dto.UserDto;
import dr.dev.scoretuneapi.user.persistence.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao ;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(u -> new UserDto(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getRoles(),
                u.getCreatedAt())
        ).toList()  ;
    }

}
