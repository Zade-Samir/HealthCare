package com.healthcare.auth_service.mapper;

import com.healthcare.auth_service.dto.AuthRequestDTO;
import com.healthcare.auth_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User requestDtoToUser(AuthRequestDTO authRequestDTO);
}
