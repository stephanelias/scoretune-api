package dr.dev.scoretuneapi.user.service;

import dr.dev.scoretuneapi.user.model.User;
import dr.dev.scoretuneapi.user.model.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers() ;
}
