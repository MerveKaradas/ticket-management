package com.kafein.ticket_management.mapper;

import org.mapstruct.Mapper;

import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.model.User;

// public class UserMapper {

//     public static ResponseUserDto toResponseUserDto(User user) {
//         if (user == null) {
//             throw new IllegalArgumentException("Kullanici nesnesi null geldi");
//         }

//         return ResponseUserDto.builder()
//             .id(user.getId())
//             .name(user.getName())
//             .surname(user.getSurname())
//             .email(user.getEmail())
//             .role(user.getRole())
//             .build();
        
//     }


    
// }

@Mapper(componentModel = "spring")
public interface UserMapper {

    public ResponseUserDto toDto(User user);

}
