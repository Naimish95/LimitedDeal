package com.example.HelloWorld.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.example.HelloWorld.model.CustForDeal;

public interface CustRepo extends JpaRepository<CustForDeal,Integer>{

}
