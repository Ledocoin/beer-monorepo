package org.example.sideservicefbeerproj.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sideservicefbeerproj.dto.EmployeeByStore;
import org.example.sideservicefbeerproj.dto.EmployeeDto;
import org.example.sideservicefbeerproj.service.EmployeeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public EmployeeDto getEmployeeById(@PathVariable String id) {
        return employeeService.getById(id);
    }

    @PostMapping
    public EmployeeDto create(@RequestBody @Valid EmployeeDto employee) {
        return employeeService.create(employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        employeeService.deleteById(id);
    }

    @GetMapping("/store/{storeId}")
    public EmployeeByStore getByStore(@PathVariable String storeId) {
        return employeeService.getByStore(storeId);
    }

    @DeleteMapping("/store/{storeId}")
    public void deleteByStore(@PathVariable String storeId) {
        employeeService.deleteByStore(storeId);
    }
}
