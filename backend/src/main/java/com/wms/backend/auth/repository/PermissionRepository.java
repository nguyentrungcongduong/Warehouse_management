package com.wms.backend.auth.repository;

import com.wms.backend.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Optional<Permission> findByCode(String code);

    List<Permission> findByCodeIn(List<String> codes);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
