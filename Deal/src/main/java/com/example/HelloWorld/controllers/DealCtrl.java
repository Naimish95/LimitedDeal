package com.example.HelloWorld.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
//@ComponentScan("com.example.HelloWorld.controller")
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
	public String endDeal(@PathVariable("did")int dealId){
		
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
		Deal dprev=dealRepo.getById(dealId);
		dprev.setMaxItems(maxItems);
		dprev.setMaxtime(maxTime);
		dealRepo.save(dprev);
		return "Deal has been updated";
	}
	@PostMapping("/claimDeal/{did}")
	public String claimDeal(@PathVariable("did")int dealId,CustForDeal c){
		//check if person has bought deal before
		Optional<CustForDeal> cust=custRepo.findCustByDealId(dealId, c.getCustId());
		
		if(!cust.isPresent()){
			
			Deal d=dealRepo.findById(dealId).get();
			//chcek if items bought<maxItems and current time<maxTime of deal
			if(d.getItemsBought()<d.getMaxItems() && LocalDateTime.now().isBefore(d.getMaxtime())){
			d.setItemsBought(d.getItemsBought()+1);
			dealRepo.save(d);
			c.setDealId(dealId);
			custRepo.save(c);
			return "Deal made";
			}
			else return "Deal cant be made as time is over or maxlimit has reached";
		}
		else{
			return "You have already bought this item";
		
		}
		
		
	}
	
}