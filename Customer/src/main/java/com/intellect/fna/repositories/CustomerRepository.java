package com.intellect.fna.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intellect.fna.model.CustomerDetailsVO;

public interface CustomerRepository extends JpaRepository<CustomerDetailsVO, Long> {

}
