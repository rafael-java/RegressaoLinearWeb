package com.example.demo_proc.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo_proc.models.RequestModel;
import com.example.demo_proc.models.ResponseModel;
import com.example.demo_proc.service.RegressaoLinearService;

@RestController
@RequestMapping

public class CtrlBasico {

	@Autowired
	private RegressaoLinearService proc;

	@PostMapping(value = "/v1/criar", produces = { "application/json", "application/xml" }, consumes = {
			"application/json", "application/xml" })
	public ResponseEntity<ResponseModel> metodoCriar(@Valid @RequestBody RequestModel req) {
		ResponseModel res = proc.acharModelo(req);
		return ResponseEntity.ok(res);
	}

	@PostMapping(value = "/v1/predizer", produces = { "application/json", "application/xml" }, consumes = {
			"application/json", "application/xml" })
	public ResponseEntity<ResponseModel> metodoCriarEPredizer(@Valid @RequestBody RequestModel req) {
		ResponseModel res = proc.acharModeloEPredizer(req);
		return ResponseEntity.ok(res);
	}

}
