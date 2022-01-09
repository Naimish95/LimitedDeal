package com.example.HelloWorld.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import com.example.HelloWorld.model.CustForDeal;

public interface CustRepo extends JpaRepository<CustForDeal,Integer>{

	@Query("delete from CustForDeal where dealId=?1")
	public void deleteByDealId(int dealId);
	
	@Query("from CustForDeal where dealId=?1 and custId=?2")
	public Optional<CustForDeal> findCustByDealId(int dealId,int custId);
}
