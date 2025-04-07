package com.example.admin.service;

import com.example.admin.model.Admin;
import com.example.admin.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional
public class AdminService {
    private final AdminRepository adminRepository;


    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;

    }


    public Admin createAdmin(Admin admin) {
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }



    public List<Admin> getAllAdmins() {
        return adminRepository.findAllAdmins();
    }




}
