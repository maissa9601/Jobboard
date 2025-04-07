package com.example.admin.controller;

import com.example.admin.model.Admin;
import com.example.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/admin")

    public class AdminController {
        private final AdminService adminService;

        public AdminController(AdminService adminService) {
            this.adminService = adminService;
        }


        @PostMapping("/create")
        public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
            return ResponseEntity.ok(adminService.createAdmin(admin));
        }


        @GetMapping("/admins")
        public ResponseEntity<List<Admin>> getAllAdmins() {
            return ResponseEntity.ok(adminService.getAllAdmins());
        }


}
