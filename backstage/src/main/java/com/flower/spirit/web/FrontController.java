package com.flower.spirit.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class FrontController {
	
	/**
	 * 引导页
	 * @return
	 */
	@RequestMapping(value = {"","/"})
	public String index() {
		return "index";
	}
	

}
