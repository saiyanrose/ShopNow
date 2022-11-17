package com.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders,Integer> {

}
