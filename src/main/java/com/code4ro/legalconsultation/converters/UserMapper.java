package com.code4ro.legalconsultation.converters;

import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User map(UserDto userDto);

    UserDto map(User User);

    List<UserDto> map(List<User> users);
}
