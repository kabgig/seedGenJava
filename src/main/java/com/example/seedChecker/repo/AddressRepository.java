package com.example.seedChecker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Address a WHERE a.address = :address")
    boolean existsByAddress(@Param("address") String address);

    @Query(value = "DROP INDEX IF EXISTS idx_address", nativeQuery = true)
    void dropAddressIndex();

    @Query(value = "CREATE INDEX idx_address ON Address(address)", nativeQuery = true)
    void createAddressIndex();

}
