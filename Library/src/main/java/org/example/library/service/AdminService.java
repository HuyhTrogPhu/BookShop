package org.example.library.service;

import org.example.library.dto.AdminDto;
import org.example.library.model.Admin;

public interface AdminService {

    Admin findByUserName(String username);

    Admin save(AdminDto adminDto);
}
