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
	DealRepo dr;
	
	@Autowired
	CustRepo cr;
	
	@PostMapping("/createDeal")
	public String createDeal(Deal d){
		dr.save(d);
		return "Deal with id="+d.getDealId()+" created successfully";
	}
	@DeleteMapping("/endDeal/{did}")
	public String endDeal(@PathVariable("did")int dealId) throws SQLException{
		Connection con=DriverManager.getConnection("jdbc:mysql://127.0.0.1/test","root","root");
		PreparedStatement ps=con.prepareStatement("Select * from Deal where dealId=?1");
		ps.setInt(1, dealId);
		
		ResultSet rs=ps.executeQuery();  
		while(rs.next()){  
		int maxitem=rs.getInt("maxItems");
		int ItemsBought=rs.getInt("ItemsBought");
		Timestamp curT=Timestamp.valueOf(LocalDateTime.now());
		Timestamp maxTime=rs.getTimestamp("maxtime");
			if(ItemsBought>=maxitem){
				dr.deleteById(dealId);
				PreparedStatement ps1=con.prepareStatement("Delete from CustForDeal where dealId=?1");
				ps1.setInt(1, dealId);
				ps1.executeUpdate();
				return "Deal with id="+dealId+ " deleted as items are sold out";
			}
			if(curT.after(maxTime)){
				dr.deleteById(dealId);
				PreparedStatement ps1=con.prepareStatement("Delete from CustForDeal where dealId=?1");
				ps1.setInt(1, dealId);
				ps1.executeUpdate();
				return "Deal with id="+dealId+ " deleted as time is over";
			}
		
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
		dr.save(dprev);
		return "Deal has been updated";
	}
	@PostMapping("/claimDeal/{did}")
	public String claimDeal(@PathVariable("did")int dealId,CustForDeal c) throws SQLException{
		//check for items bought
		Connection con=DriverManager.getConnection("jdbc:mysql://127.0.0.1/test","root","root");
		//check if person has bought deal before
		PreparedStatement ps1=con.prepareStatement("Select * from CustForDeal where dealId=?1 and custId=?2");
		ps1.setInt(1, dealId);
		ps1.setInt(2, c.getCustId());
		ResultSet rs1=ps1.executeQuery();
		if(rs1==null){
			//chcek if items bought<maxItems
			PreparedStatement ps=con.prepareStatement("Select * from Deal where dealId=?1");
			ps.setInt(1, dealId);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				int maxitem=rs.getInt("maxItems");
				int ItemsBought=rs.getInt("ItemsBought");
				Timestamp now=Timestamp.valueOf(LocalDateTime.now());
				Timestamp maxTime=rs.getTimestamp("maxtime");
				if(ItemsBought<maxitem && now.before(maxTime)){
					PreparedStatement ps2=con.prepareStatement("Update Deal set ItemsBought=?1 where dealId=?2");
					ps2.setInt(1, ItemsBought+1);
					ps2.setInt(2, dealId);
					ps2.executeUpdate();
					cr.save(c);
				}
				else return "deal cant be made as time is over or maxlimit has reached";
			}
			
		}
		return "You have already bought this item";
		
		
	}
}
