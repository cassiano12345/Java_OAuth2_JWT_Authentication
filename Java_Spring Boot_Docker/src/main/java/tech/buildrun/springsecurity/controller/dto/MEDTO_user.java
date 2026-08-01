package tech.buildrun.springsecurity.controller.dto;

import tech.buildrun.springsecurity.entities.Role;

import java.util.List;
import java.util.Set;

public record MEDTO_user(String nome, List<String> roles) {
}
