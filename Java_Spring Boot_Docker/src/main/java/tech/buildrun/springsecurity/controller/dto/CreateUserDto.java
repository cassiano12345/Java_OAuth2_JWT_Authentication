package tech.buildrun.springsecurity.controller.dto;

public record CreateUserDto(String username, String email, String password) {
}