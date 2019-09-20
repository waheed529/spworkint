package com.packtpub.springsecurity.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This displays the welcome screen that shows what will be happening in project.
 *
 * @author Rob Winch
 *
 */
@Controller
public class WelcomeController {
	/*@RequestMapping("/")
	public String welcome() {
		return "index";
	}*/
	
	@RequestMapping("/")
	public ModelAndView welcome() {
		return new ModelAndView("index");
	}
}