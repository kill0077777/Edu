package com.edu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	//GET  http://localhost:80/Edu
		@GetMapping("/Edu")
		@ResponseBody //URL 요청에 대한 응답으로 문자열을 브라우저로 리턴하라는 의미로 쓰였다.
	    public String index() {
	       return "index";
	    }
		//GET  http://localhost:80/
		@GetMapping("/")
		public String root() {

			return "index";
			//http://localhost:80/
			//http://localhost:8090/

			//return "layout";
			//return "redirect:/lecture/list";
			//http://localhost:80/lecture/list	<---- 포워딩(재요청) 강의
			//http://localhost:80/review/list	<---- 포워딩(재요청) 리뷰

		}
}
