package com.sentinel.appeals.repository;
import com.sentinel.appeals.entity.AppealableSanction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AppealableSanctionRepository extends JpaRepository<AppealableSanction,String>{}
