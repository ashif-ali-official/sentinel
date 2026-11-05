package com.sentinel.appeals.repository;
import com.sentinel.appeals.entity.Appeal;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppealRepository extends JpaRepository<Appeal,String>{}
