package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.converters.UserMapper;
import com.code4ro.legalconsultation.model.dto.PageDto;
import com.code4ro.legalconsultation.model.dto.UserDto;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.service.impl.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @ApiOperation(value = "Save a new user in the platform",
            response = UserDto.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping
    public UserDto save(
            @ApiParam("The DTO object containing new user information") @RequestBody @Valid final UserDto userDto) {
        return userService.saveAndSendRegistrationMail(userDto);
    }

    @ApiOperation(value = "Save a list of users in the platform",
            response = List.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/bulk")
    public List<UserDto> saveAll(
            @ApiParam("List of DTO objects containing new users information") @Valid @RequestBody final List<UserDto> userDtos) {
        return userService.saveAndSendRegistrationMail(userDtos);
    }

    @ApiOperation(value = "Return a single user from the platform based on id",
            response = UserDto.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/{id}")
    public UserDto getOne(
            @ApiParam("Id of the user object being requested") @PathVariable final String id) {
        return userService.getOne(id);
    }

    @ApiOperation(value = "Return a paginated list of users from the platform",
            response = PageDto.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public PageDto<UserDto> findAll(@ApiParam("Page object information being requested") final Pageable pageable) {
        final Page<User> userPage = userService.findAll(pageable);
        Page<UserDto> usersDto = userPage.map(userMapper::map);
        return new PageDto<>(usersDto);
    }

    @ApiOperation(value = "Delete a user from the platform based on id",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping(value = "/{id}")
    public void deleteById(@ApiParam("Id of the user object being deleted") @PathVariable final String id) {
        userService.deleteById(id);
    }

    @ApiOperation(value = "Extract user information from an uploaded csv file",
            response = List.class,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/extract", consumes = "multipart/form-data")
    public List<UserDto> extractFromCsv(
            @ApiParam("CSV file containing user information that is being uploaded") @RequestParam("file") final MultipartFile file) {
        return userService.extractFromCsv(file);
    }

    @ApiOperation(value = "Extract user information from a copy/paste",
            response = List.class,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/extract-from-copy")
    public List<UserDto> extractFromCopyPaste(
            @ApiParam("List of Strings with all the user details separated by comma")
            @RequestBody final List<String> usersList) {
        return userService.extractFromCopyPaste(usersList);
    }

    @ApiOperation(value = "Search fields within the user for a given input term",
            response = List.class,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/search")
    public List<UserDto> searchByTerm(@ApiParam("String of the input searching term")
                                      @RequestParam("searchTerm") final String searchTerm) {
        return userMapper.map(userService.searchByTerm(searchTerm));
    }
}
