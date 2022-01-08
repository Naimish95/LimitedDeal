package com.example.HelloWorld.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.HelloWorld.dao.CustRepo;
import com.example.HelloWorld.dao.DealRepo;
import com.example.HelloWorld.model.CustForDeal;
import com.example.HelloWorld.model.Deal;

@RestController
@ComponentScan("com.example.HelloWorld.controller")
public class DealCtrl {

	@Autowired
	DealRepo dealRepo;
	
	@Autowired
	CustRepo custRepo;
	
	@PostMapping("/createDeal")
	public String createDeal(Deal d){
		dealRepo.save(d);
		return "Deal with id="+d.getDealId()+" created successfully";
	}
	@DeleteMapping("/endDeal/{did}")
	public String endDeal(@PathVariable("did")int dealId) throws SQLException{
		
		Deal d=dealRepo.findById(dealId).get();
		if(d.getItemsBought()>=d.getMaxItems()){
			dealRepo.deleteById(dealId);
			custRepo.deleteByDealId(dealId);
			return "Deal with id="+dealId+" is ended as maximum limit has reached.";
		}
		if(LocalDateTime.now().isAfter(d.getMaxtime())){
			dealRepo.deleteById(dealId);
			custRepo.deleteByDealId(dealId);
			return "Deal with id="+dealId+" is ended as time limit is over.";
		}
		
		return "Deal cant be ended";
	}
	@PutMapping("/updateDeal/{did}")
	public String updateDeal(@PathVariable("did")int dealId,Deal d){
		LocalDateTime maxTime=d.getMaxtime();
		int maxItems=d.getMaxItems();
		Deal dprev=dr.getById(dealId);
		dprev.setMaxItems(maxItems);
		dprev.setMaxtime(maxTime);
		dealRepo.save(dprev);
		return "Deal has been updated";
	}
	@PostMapping("/claimDeal/{did}")
	public String claimDeal(@PathVariable("did")int dealId,CustForDeal c) throws SQLException{
		//check if person has bought deal before
		Optional<CustForDeal> cust=custRepo.findCustByDealId(dealId, c.getCustId());
		
		if(!cust.isPresent()){
			//chcek if items bought<maxItems
			Deal d=dealRepo.findById(dealId).get();
			if(d.getItemsBought()<d.getMaxItems() && LocalDateTime.now().isAfter(d.getMaxtime())){
			d.setItemsBought(d.getItemsBought()+1);
			dealRepo.save(d);
			custRepo.save(c);
			}
			else return "Deal cant be made as time is over or maxlimit has reached";
		}
		else{
			return "You have already bought this item";
		
		}
		return null;
		
	}
	
}
