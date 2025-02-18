package com.assignment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.assignment.dao.CategoryDAO;
import com.assignment.entity.Category;


@Controller
public class CategoryController {
	@Autowired
	CategoryDAO cateDao;

	@RequestMapping("/admin/category/index")
	public String index(@RequestParam(value = "tab", required = false) String tab, Model model) {
		Category item = new Category();
		model.addAttribute("item", item);
		//List<Category> items = dao.findAll();
		//model.addAttribute("items", items);
		Pageable pageable = PageRequest.of(0, 1); 
		Page<Category> page = cateDao.findAll(pageable); 
		model.addAttribute("page", page); 
		model.addAttribute("activeTabs", tab);
		return "views/admin";
	}
}
