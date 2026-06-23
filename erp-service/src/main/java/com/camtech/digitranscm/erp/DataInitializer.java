package com.camtech.digitranscm.erp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final EmployeeRepository employeeRepository;

    public DataInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (employeeRepository.count() == 0) {
            employeeRepository.save(new Employee("Jean Dupont", "Finance", "Comptable", "jean.dupont@agrocam.cm"));
            employeeRepository.save(new Employee("Marie Mbella", "RH", "Responsable RH", "marie.mbella@agrocam.cm"));
        }
    }
}
