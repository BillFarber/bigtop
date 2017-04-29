package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.markLogic.bigTop.middle.ldapDomain.Person;

@Controller
@Configuration
public class HomeController {
	@GetMapping("/")
	public String index(Model model, HttpServletRequest request) throws javax.naming.NamingException, IOException {
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("person");
		model.addAttribute("person", person);
		return "home";
	}
}
