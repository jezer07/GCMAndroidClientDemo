package com.example.gcmdemo;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

import com.example.gcmdemo.model.User;


public interface ContactsInterface {
	
	
	@GET("/users.json")
	void contacts(Callback<List<User>> cb);
	
	

}
